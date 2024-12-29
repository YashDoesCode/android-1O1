package com.keeper.lolock.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import com.google.android.material.button.MaterialButton;
import com.keeper.lolock.R;
import com.keeper.lolock.crypto.CryptoManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class UnlockActivity extends AppCompatActivity {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private Uri targetUri;
    private String targetFileName = "container.lolock";
    private long targetFileSize = 0L;

    private TextView tvTargetFileName;
    private TextView tvTargetFileSize;
    private EditText etUnlockPassword;
    private ProgressBar pbUnlockProgress;
    private MaterialButton btnUnlockAndOpen;
    private MaterialButton btnDecryptToStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);

        initViews();
        resolveTargetUri(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveTargetUri(intent);
    }

    private void initViews() {
        tvTargetFileName = findViewById(R.id.tvTargetFileName);
        tvTargetFileSize = findViewById(R.id.tvTargetFileSize);
        etUnlockPassword = findViewById(R.id.etUnlockPassword);
        pbUnlockProgress = findViewById(R.id.pbUnlockProgress);
        btnUnlockAndOpen = findViewById(R.id.btnUnlockAndOpen);
        btnDecryptToStorage = findViewById(R.id.btnDecryptToStorage);
        TextView btnCancel = findViewById(R.id.btnCancelUnlock);

        btnCancel.setOnClickListener(v -> finish());

        btnUnlockAndOpen.setOnClickListener(v -> {
            String password = etUnlockPassword.getText().toString();
            if (password.isEmpty()) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            executeUnlockAndOpen(password.toCharArray());
        });

        btnDecryptToStorage.setOnClickListener(v -> {
            String password = etUnlockPassword.getText().toString();
            if (password.isEmpty()) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            executeDecryptToStorage(password.toCharArray());
        });
    }

    private void resolveTargetUri(Intent intent) {
        if (intent == null) {
            finish();
            return;
        }

        targetUri = intent.getData();
        if (targetUri == null && intent.getClipData() != null && intent.getClipData().getItemCount() > 0) {
            targetUri = intent.getClipData().getItemAt(0).getUri();
        }

        if (targetUri == null) {
            Toast.makeText(this, "No target container provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        queryFileMetadata();
        tvTargetFileName.setText(targetFileName);
        tvTargetFileSize.setText(formatFileSize(targetFileSize));
    }

    private void queryFileMetadata() {
        if ("content".equalsIgnoreCase(targetUri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(targetUri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE);
                    if (nameIdx != -1) {
                        targetFileName = cursor.getString(nameIdx);
                    }
                    if (sizeIdx != -1) {
                        targetFileSize = cursor.getLong(sizeIdx);
                    }
                }
            } catch (Exception ignored) {
            }
        } else if ("file".equalsIgnoreCase(targetUri.getScheme())) {
            File f = new File(targetUri.getPath());
            targetFileName = f.getName();
            targetFileSize = f.length();
        }
    }

    private void executeUnlockAndOpen(char[] password) {
        setUiBusy(true);

        executorService.execute(() -> {
            File cacheDir = new File(getCacheDir(), "decrypted");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            File tempFile = new File(cacheDir, "preview_" + System.currentTimeMillis() + ".tmp");

            try (InputStream in = getContentResolver().openInputStream(targetUri);
                 OutputStream out = new FileOutputStream(tempFile)) {

                if (in == null) {
                    throw new IllegalStateException("Failed to open container stream");
                }

                CryptoManager.FileHeader header = CryptoManager.decryptStream(
                        in,
                        out,
                        password,
                        (bytes, max) -> updateProgress(bytes, max)
                );

                out.flush();

                File finalDecryptedFile = new File(cacheDir, header.originalFilename);
                if (finalDecryptedFile.exists()) {
                    finalDecryptedFile.delete();
                }
                tempFile.renameTo(finalDecryptedFile);

                mainHandler.post(() -> {
                    setUiBusy(false);
                    openFileWithNativeIntent(finalDecryptedFile, header.mimeType);
                    finish();
                });

            } catch (Exception e) {
                if (tempFile.exists()) {
                    tempFile.delete();
                }
                mainHandler.post(() -> {
                    setUiBusy(false);
                    Toast.makeText(UnlockActivity.this, "Authentication or integrity error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void executeDecryptToStorage(char[] password) {
        setUiBusy(true);

        executorService.execute(() -> {
            File cacheDir = new File(getCacheDir(), "decrypted");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            File tempFile = new File(cacheDir, "store_" + System.currentTimeMillis() + ".tmp");

            try (InputStream in = getContentResolver().openInputStream(targetUri);
                 OutputStream out = new FileOutputStream(tempFile)) {

                if (in == null) {
                    throw new IllegalStateException("Failed to open container stream");
                }

                CryptoManager.FileHeader header = CryptoManager.decryptStream(
                        in,
                        out,
                        password,
                        (bytes, max) -> updateProgress(bytes, max)
                );

                out.flush();

                DocumentFile srcDoc = DocumentFile.fromSingleUri(this, targetUri);
                DocumentFile parentDir = srcDoc != null ? srcDoc.getParentFile() : null;

                if (parentDir != null) {
                    DocumentFile targetDoc = parentDir.createFile(header.mimeType, header.originalFilename);
                    if (targetDoc != null) {
                        try (InputStream cacheIn = new FileInputStream(tempFile);
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
                    setUiBusy(false);
                    Toast.makeText(UnlockActivity.this, "Decrypted to storage: " + header.originalFilename, Toast.LENGTH_LONG).show();
                    finish();
                });

            } catch (Exception e) {
                if (tempFile.exists()) {
                    tempFile.delete();
                }
                mainHandler.post(() -> {
                    setUiBusy(false);
                    Toast.makeText(UnlockActivity.this, "Failed to restore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void openFileWithNativeIntent(File file, String mimeType) {
        try {
            Uri contentUri = FileProvider.getUriForFile(
                    this,
                    "com.keeper.lolock.fileprovider",
                    file
            );

            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setDataAndType(contentUri, (mimeType != null && !mimeType.isEmpty()) ? mimeType : "*/*");
            viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(Intent.createChooser(viewIntent, "Open with"));
        } catch (Exception e) {
            Toast.makeText(this, "Decrypted successfully, but no matching app found to open file", Toast.LENGTH_LONG).show();
        }
    }

    private void setUiBusy(boolean busy) {
        pbUnlockProgress.setVisibility(busy ? View.VISIBLE : View.GONE);
        btnUnlockAndOpen.setEnabled(!busy);
        btnDecryptToStorage.setEnabled(!busy);
        etUnlockPassword.setEnabled(!busy);
    }

    private void updateProgress(long current, long total) {
        if (total > 0) {
            int pct = (int) ((current * 100) / total);
            mainHandler.post(() -> pbUnlockProgress.setProgress(pct));
        }
    }

    private String formatFileSize(long bytes) {
        if (bytes <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return String.format(Locale.US, "%.1f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
