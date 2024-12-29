package com.keeper.lolock.ui;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import com.google.android.material.button.MaterialButton;
import com.keeper.lolock.R;
import com.keeper.lolock.crypto.CryptoManager;
import com.keeper.lolock.crypto.DirectoryCryptoEngine;
import com.keeper.lolock.service.FileCryptoService;
import com.keeper.lolock.service.PasswordExpiryWorker;
import com.keeper.lolock.service.SecurityPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MainActivity extends AppCompatActivity {

    private SecurityPreferences securityPreferences;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private TextView tvPasswordStatusDesc;
    private TextView tvRotationBadge;
    private MaterialButton btnMasterPasswordAction;
    private LinearLayout layoutProgressCard;
    private TextView tvProgressTitle;
    private TextView tvProgressSubtitle;
    private ProgressBar progressBar;

    private ActivityResultLauncher<String[]> fileEncryptLauncher;
    private ActivityResultLauncher<String[]> fileDecryptLauncher;
    private ActivityResultLauncher<Uri> folderEncryptLauncher;
    private ActivityResultLauncher<Uri> folderDecryptLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        securityPreferences = new SecurityPreferences(this);
        PasswordExpiryWorker.schedulePeriodicCheck(this);

        initViews();
        initLaunchers();
        updateSecurityStateUi();
    }

    private void initViews() {
        tvPasswordStatusDesc = findViewById(R.id.tvPasswordStatusDesc);
        tvRotationBadge = findViewById(R.id.tvRotationBadge);
        btnMasterPasswordAction = findViewById(R.id.btnMasterPasswordAction);
        layoutProgressCard = findViewById(R.id.layoutProgressCard);
        tvProgressTitle = findViewById(R.id.tvProgressTitle);
        tvProgressSubtitle = findViewById(R.id.tvProgressSubtitle);
        progressBar = findViewById(R.id.progressBar);

        btnMasterPasswordAction.setOnClickListener(v -> showPasswordConfigDialog());

        MaterialButton btnEncryptFile = findViewById(R.id.btnEncryptFile);
        MaterialButton btnEncryptFolder = findViewById(R.id.btnEncryptFolder);
        MaterialButton btnDecryptFile = findViewById(R.id.btnDecryptFile);
        MaterialButton btnDecryptFolder = findViewById(R.id.btnDecryptFolder);

        btnEncryptFile.setOnClickListener(v -> {
            if (ensureMasterPasswordConfigured()) {
                fileEncryptLauncher.launch(new String[]{"*/*"});
            }
        });

        btnEncryptFolder.setOnClickListener(v -> {
            if (ensureMasterPasswordConfigured()) {
                folderEncryptLauncher.launch(null);
            }
        });

        btnDecryptFile.setOnClickListener(v -> {
            if (ensureMasterPasswordConfigured()) {
                fileDecryptLauncher.launch(new String[]{"*/*"});
            }
        });

        btnDecryptFolder.setOnClickListener(v -> {
            if (ensureMasterPasswordConfigured()) {
                folderDecryptLauncher.launch(null);
            }
        });
    }

    private void initLaunchers() {
        fileEncryptLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        requestPasswordAndExecute((password) -> executeSingleFileEncryption(uri, password));
                    }
                }
        );

        fileDecryptLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        requestPasswordAndExecute((password) -> executeSingleFileDecryption(uri, password));
                    }
                }
        );

        folderEncryptLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocumentTree(),
                uri -> {
                    if (uri != null) {
                        requestPasswordAndExecute((password) -> executeDirectoryEncryption(uri, password));
                    }
                }
        );

        folderDecryptLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocumentTree(),
                uri -> {
                    if (uri != null) {
                        requestPasswordAndExecute((password) -> executeDirectoryDecryption(uri, password));
                    }
                }
        );
    }

    private void updateSecurityStateUi() {
        if (!securityPreferences.isPasswordConfigured()) {
            tvPasswordStatusDesc.setText(R.string.status_password_not_set);
            tvRotationBadge.setText("Setup Required");
            tvRotationBadge.setTextColor(ContextCompat.getColor(this, R.color.status_red));
            btnMasterPasswordAction.setText(R.string.action_set_password);
        } else {
            long remainingDays = securityPreferences.getRemainingDaysUntilRotation();
            tvPasswordStatusDesc.setText("Master password active. Next routine security rotation in " + remainingDays + " days.");
            tvRotationBadge.setText(remainingDays + "d Remaining");
            tvRotationBadge.setTextColor(ContextCompat.getColor(this, R.color.status_green));
            btnMasterPasswordAction.setText(R.string.action_change_password);
        }
    }

    private boolean ensureMasterPasswordConfigured() {
        if (!securityPreferences.isPasswordConfigured()) {
            Toast.makeText(this, "Please initialize Master Password first", Toast.LENGTH_SHORT).show();
            showPasswordConfigDialog();
            return false;
        }
        return true;
    }

    private void showPasswordConfigDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_set_password);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        EditText etNewPassword = dialog.findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = dialog.findViewById(R.id.etConfirmPassword);
        MaterialButton btnSavePassword = dialog.findViewById(R.id.btnSavePassword);
        TextView btnDismiss = dialog.findViewById(R.id.btnDismissPasswordDialog);

        btnDismiss.setOnClickListener(v -> dialog.dismiss());
        btnSavePassword.setOnClickListener(v -> {
            String p1 = etNewPassword.getText().toString();
            String p2 = etConfirmPassword.getText().toString();

            if (p1.isEmpty() || p1.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!p1.equals(p2)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            securityPreferences.markPasswordConfigured();
            updateSecurityStateUi();
            dialog.dismiss();
            Toast.makeText(this, "Master security state updated", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private interface PasswordConsumer {
        void accept(char[] password);
    }

    private void requestPasswordAndExecute(PasswordConsumer consumer) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_set_password);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView tvTitle = dialog.findViewById(R.id.tvPasswordDialogTitle);
        tvTitle.setText("Verify Container Key");
        EditText etNewPassword = dialog.findViewById(R.id.etNewPassword);
        etNewPassword.setHint(R.string.hint_enter_password);
        EditText etConfirmPassword = dialog.findViewById(R.id.etConfirmPassword);
        etConfirmPassword.setVisibility(View.GONE);
        MaterialButton btnSavePassword = dialog.findViewById(R.id.btnSavePassword);
        btnSavePassword.setText("Authenticate and Proceed");
        TextView btnDismiss = dialog.findViewById(R.id.btnDismissPasswordDialog);

        btnDismiss.setOnClickListener(v -> dialog.dismiss());
        btnSavePassword.setOnClickListener(v -> {
            String pass = etNewPassword.getText().toString();
            if (pass.isEmpty()) {
                Toast.makeText(this, "Password required", Toast.LENGTH_SHORT).show();
                return;
            }
            dialog.dismiss();
            consumer.accept(pass.toCharArray());
        });

        dialog.show();
    }

    private void executeSingleFileEncryption(Uri sourceUri, char[] password) {
        startService();
        showProgress("Encrypting File", "Deriving Argon2id keys & streaming blocks...");

        executorService.execute(() -> {
            DocumentFile srcDoc = DocumentFile.fromSingleUri(this, sourceUri);
            String originalName = srcDoc != null && srcDoc.getName() != null ? srcDoc.getName() : "file";
            String mime = srcDoc != null && srcDoc.getType() != null ? srcDoc.getType() : "application/octet-stream";
            long length = srcDoc != null ? srcDoc.length() : 0L;

            File cacheOutput = new File(getCacheDir(), originalName + DirectoryCryptoEngine.CONTAINER_EXTENSION);

            try (InputStream in = getContentResolver().openInputStream(sourceUri);
                 OutputStream out = new FileOutputStream(cacheOutput)) {

                if (in == null) {
                    throw new IllegalStateException("Cannot access source stream");
                }

                CryptoManager.encryptStream(
                        in,
                        out,
                        password,
                        originalName,
                        mime,
                        length,
                        (processed, total) -> updateProgressPercent(processed, total)
                );

                DocumentFile parentDir = srcDoc != null ? srcDoc.getParentFile() : null;
                if (parentDir != null) {
                    DocumentFile targetDoc = parentDir.createFile("application/octet-stream", originalName + DirectoryCryptoEngine.CONTAINER_EXTENSION);
                    if (targetDoc != null) {
                        try (InputStream cacheIn = new FileInputStream(cacheOutput);
                             OutputStream targetOut = getContentResolver().openOutputStream(targetDoc.getUri())) {
                            byte[] buffer = new byte[64 * 1024];
                            int r;
                            while ((r = cacheIn.read(buffer)) != -1) {
                                targetOut.write(buffer, 0, r);
                            }
                            targetOut.flush();
                        }
                        srcDoc.delete();
                    }
                }

                mainHandler.post(() -> {
                    hideProgress();
                    Toast.makeText(this, "Encrypted: " + originalName + DirectoryCryptoEngine.CONTAINER_EXTENSION, Toast.LENGTH_LONG).show();
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    hideProgress();
                    Toast.makeText(this, "Encryption failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } finally {
                if (cacheOutput.exists()) {
                    cacheOutput.delete();
                }
                stopService();
            }
        });
    }

    private void executeSingleFileDecryption(Uri sourceUri, char[] password) {
        startService();
        showProgress("Decrypting File", "Verifying block chain & deciphering...");

        executorService.execute(() -> {
            File cacheOutput = new File(getCacheDir(), "decrypted_" + System.currentTimeMillis() + ".tmp");

            try (InputStream in = getContentResolver().openInputStream(sourceUri);
                 OutputStream out = new FileOutputStream(cacheOutput)) {

                if (in == null) {
                    throw new IllegalStateException("Cannot access source stream");
                }

                CryptoManager.FileHeader header = CryptoManager.decryptStream(
                        in,
                        out,
                        password,
                        (processed, total) -> updateProgressPercent(processed, total)
                );

                DocumentFile srcDoc = DocumentFile.fromSingleUri(this, sourceUri);
                DocumentFile parentDir = srcDoc != null ? srcDoc.getParentFile() : null;
                if (parentDir != null) {
                    DocumentFile targetDoc = parentDir.createFile(header.mimeType, header.originalFilename);
                    if (targetDoc != null) {
                        try (InputStream cacheIn = new FileInputStream(cacheOutput);
                             OutputStream targetOut = getContentResolver().openOutputStream(targetDoc.getUri())) {
                            byte[] buffer = new byte[64 * 1024];
                            int r;
                            while ((r = cacheIn.read(buffer)) != -1) {
                                targetOut.write(buffer, 0, r);
                            }
                            targetOut.flush();
                        }
                        srcDoc.delete();
                    }
                }

                mainHandler.post(() -> {
                    hideProgress();
                    Toast.makeText(this, "Restored: " + header.originalFilename, Toast.LENGTH_LONG).show();
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    hideProgress();
                    Toast.makeText(this, "Decryption failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } finally {
                if (cacheOutput.exists()) {
                    cacheOutput.delete();
                }
                stopService();
            }
        });
    }

    private void executeDirectoryEncryption(Uri treeUri, char[] password) {
        startService();
        showProgress("Encrypting Directory", "Starting batch traversal...");

        executorService.execute(() -> {
            DirectoryCryptoEngine.encryptDocumentTree(
                    this,
                    treeUri,
                    password,
                    new DirectoryCryptoEngine.BatchCallback() {
                        @Override
                        public void onFileStart(String fileName, int index, int total) {
                            mainHandler.post(() -> tvProgressSubtitle.setText("[" + index + "/" + total + "] " + fileName));
                        }

                        @Override
                        public void onFileProgress(long currentBytes, long totalBytes) {
                            updateProgressPercent(currentBytes, totalBytes);
                        }

                        @Override
                        public void onFileComplete(String fileName) {
                        }

                        @Override
                        public void onError(String fileName, Exception e) {
                        }

                        @Override
                        public void onAllComplete(int succeeded, int failed) {
                            mainHandler.post(() -> {
                                hideProgress();
                                Toast.makeText(MainActivity.this, "Batch encrypted: " + succeeded + " files (" + failed + " errors)", Toast.LENGTH_LONG).show();
                            });
                            stopService();
                        }
                    }
            );
        });
    }

    private void executeDirectoryDecryption(Uri treeUri, char[] password) {
        startService();
        showProgress("Decrypting Directory", "Starting batch restoration...");

        executorService.execute(() -> {
            DirectoryCryptoEngine.decryptDocumentTree(
                    this,
                    treeUri,
                    password,
                    new DirectoryCryptoEngine.BatchCallback() {
                        @Override
                        public void onFileStart(String fileName, int index, int total) {
                            mainHandler.post(() -> tvProgressSubtitle.setText("[" + index + "/" + total + "] " + fileName));
                        }

                        @Override
                        public void onFileProgress(long currentBytes, long totalBytes) {
                            updateProgressPercent(currentBytes, totalBytes);
                        }

                        @Override
                        public void onFileComplete(String fileName) {
                        }

                        @Override
                        public void onError(String fileName, Exception e) {
                        }

                        @Override
                        public void onAllComplete(int succeeded, int failed) {
                            mainHandler.post(() -> {
                                hideProgress();
                                Toast.makeText(MainActivity.this, "Batch restored: " + succeeded + " files (" + failed + " errors)", Toast.LENGTH_LONG).show();
                            });
                            stopService();
                        }
                    }
            );
        });
    }

    private void showProgress(String title, String subtitle) {
        layoutProgressCard.setVisibility(View.VISIBLE);
        tvProgressTitle.setText(title);
        tvProgressSubtitle.setText(subtitle);
        progressBar.setProgress(0);
    }

    private void updateProgressPercent(long current, long total) {
        if (total > 0) {
            int pct = (int) ((current * 100) / total);
            mainHandler.post(() -> progressBar.setProgress(pct));
        }
    }

    private void hideProgress() {
        layoutProgressCard.setVisibility(View.GONE);
    }

    private void startService() {
        Intent intent = new Intent(this, FileCryptoService.class);
        intent.setAction(FileCryptoService.ACTION_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void stopService() {
        Intent intent = new Intent(this, FileCryptoService.class);
        intent.setAction(FileCryptoService.ACTION_STOP);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
