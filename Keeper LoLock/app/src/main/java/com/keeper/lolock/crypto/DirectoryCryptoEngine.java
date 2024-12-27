package com.keeper.lolock.crypto;

import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public final class DirectoryCryptoEngine {

    public static final String CONTAINER_EXTENSION = ".lolock";

    public interface BatchCallback {
        void onFileStart(String fileName, int index, int total);
        void onFileProgress(long currentBytes, long totalBytes);
        void onFileComplete(String fileName);
        void onError(String fileName, Exception e);
        void onAllComplete(int succeeded, int failed);
    }

    private DirectoryCryptoEngine() {
    }

    public static void encryptLocalDirectory(
            File directory,
            char[] password,
            boolean deleteOriginal,
            BatchCallback callback
    ) {
        List<File> plainFiles = new ArrayList<>();
        collectPlainFiles(directory, plainFiles);

        int total = plainFiles.size();
        int succeeded = 0;
        int failed = 0;

        for (int i = 0; i < total; i++) {
            File src = plainFiles.get(i);
            if (callback != null) {
                callback.onFileStart(src.getName(), i + 1, total);
            }

            File dest = new File(src.getParentFile(), src.getName() + CONTAINER_EXTENSION);
            try (InputStream in = new FileInputStream(src);
                 OutputStream out = new FileOutputStream(dest)) {

                CryptoManager.encryptStream(
                        in,
                        out,
                        password,
                        src.getName(),
                        "application/octet-stream",
                        src.length(),
                        (bytes, max) -> {
                            if (callback != null) {
                                callback.onFileProgress(bytes, max);
                            }
                        }
                );

                if (deleteOriginal) {
                    src.delete();
                }
                succeeded++;
                if (callback != null) {
                    callback.onFileComplete(src.getName());
                }
            } catch (Exception e) {
                failed++;
                if (dest.exists()) {
                    dest.delete();
                }
                if (callback != null) {
                    callback.onError(src.getName(), e);
                }
            }
        }

        if (callback != null) {
            callback.onAllComplete(succeeded, failed);
        }
    }

    public static void decryptLocalDirectory(
            File directory,
            char[] password,
            boolean deleteContainer,
            BatchCallback callback
    ) {
        List<File> lockedFiles = new ArrayList<>();
        collectLockedFiles(directory, lockedFiles);

        int total = lockedFiles.size();
        int succeeded = 0;
        int failed = 0;

        for (int i = 0; i < total; i++) {
            File container = lockedFiles.get(i);
            if (callback != null) {
                callback.onFileStart(container.getName(), i + 1, total);
            }

            File tempDecrypted = new File(container.getParentFile(), container.getName() + ".tmp");
            try (InputStream in = new FileInputStream(container);
                 OutputStream out = new FileOutputStream(tempDecrypted)) {

                CryptoManager.FileHeader header = CryptoManager.decryptStream(
                        in,
                        out,
                        password,
                        (bytes, max) -> {
                            if (callback != null) {
                                callback.onFileProgress(bytes, max);
                            }
                        }
                );

                out.flush();
                File finalFile = new File(container.getParentFile(), header.originalFilename);
                tempDecrypted.renameTo(finalFile);

                if (deleteContainer) {
                    container.delete();
                }
                succeeded++;
                if (callback != null) {
                    callback.onFileComplete(finalFile.getName());
                }
            } catch (Exception e) {
                failed++;
                if (tempDecrypted.exists()) {
                    tempDecrypted.delete();
                }
                if (callback != null) {
                    callback.onError(container.getName(), e);
                }
            }
        }

        if (callback != null) {
            callback.onAllComplete(succeeded, failed);
        }
    }

    public static void encryptDocumentTree(
            Context context,
            Uri treeUri,
            char[] password,
            BatchCallback callback
    ) {
        DocumentFile root = DocumentFile.fromTreeUri(context, treeUri);
        if (root == null || !root.isDirectory()) {
            return;
        }

        List<DocumentFile> files = new ArrayList<>();
        collectDocumentFiles(root, files, false);

        int total = files.size();
        int succeeded = 0;
        int failed = 0;

        for (int i = 0; i < total; i++) {
            DocumentFile src = files.get(i);
            String name = src.getName();
            if (name == null || name.endsWith(CONTAINER_EXTENSION)) {
                continue;
            }

            if (callback != null) {
                callback.onFileStart(name, i + 1, total);
            }

            DocumentFile parent = src.getParentFile();
            if (parent == null) {
                parent = root;
            }

            DocumentFile target = parent.createFile("application/octet-stream", name + CONTAINER_EXTENSION);
            if (target == null) {
                failed++;
                continue;
            }

            try (InputStream in = context.getContentResolver().openInputStream(src.getUri());
                 OutputStream out = context.getContentResolver().openOutputStream(target.getUri())) {

                if (in == null || out == null) {
                    throw new IllegalStateException("Failed to open streams");
                }

                CryptoManager.encryptStream(
                        in,
                        out,
                        password,
                        name,
                        src.getType(),
                        src.length(),
                        (bytes, max) -> {
                            if (callback != null) {
                                callback.onFileProgress(bytes, max);
                            }
                        }
                );

                src.delete();
                succeeded++;
                if (callback != null) {
                    callback.onFileComplete(name);
                }
            } catch (Exception e) {
                failed++;
                target.delete();
                if (callback != null) {
                    callback.onError(name, e);
                }
            }
        }

        if (callback != null) {
            callback.onAllComplete(succeeded, failed);
        }
    }

    public static void decryptDocumentTree(
            Context context,
            Uri treeUri,
            char[] password,
            BatchCallback callback
    ) {
        DocumentFile root = DocumentFile.fromTreeUri(context, treeUri);
        if (root == null || !root.isDirectory()) {
            return;
        }

        List<DocumentFile> files = new ArrayList<>();
        collectDocumentFiles(root, files, true);

        int total = files.size();
        int succeeded = 0;
        int failed = 0;

        for (int i = 0; i < total; i++) {
            DocumentFile container = files.get(i);
            String name = container.getName();
            if (name == null) {
                continue;
            }

            if (callback != null) {
                callback.onFileStart(name, i + 1, total);
            }

            DocumentFile parent = container.getParentFile();
            if (parent == null) {
                parent = root;
            }

            File tempFile = new File(context.getCacheDir(), "dec_" + System.currentTimeMillis() + ".tmp");
            try {
                CryptoManager.FileHeader header;
                try (InputStream in = context.getContentResolver().openInputStream(container.getUri());
                     OutputStream out = new FileOutputStream(tempFile)) {

                    if (in == null) {
                        throw new IllegalStateException("Failed to open container stream");
                    }

                    header = CryptoManager.decryptStream(
                            in,
                            out,
                            password,
                            (bytes, max) -> {
                                if (callback != null) {
                                    callback.onFileProgress(bytes, max);
                                }
                            }
                    );
                }

                DocumentFile restoredFile = parent.createFile(header.mimeType, header.originalFilename);
                if (restoredFile != null) {
                    try (InputStream in = new FileInputStream(tempFile);
                         OutputStream out = context.getContentResolver().openOutputStream(restoredFile.getUri())) {
                        byte[] buffer = new byte[64 * 1024];
                        int r;
                        while ((r = in.read(buffer)) != -1) {
                            out.write(buffer, 0, r);
                        }
                        out.flush();
                    }
                    container.delete();
                    succeeded++;
                    if (callback != null) {
                        callback.onFileComplete(header.originalFilename);
                    }
                }
            } catch (Exception e) {
                failed++;
                if (callback != null) {
                    callback.onError(name, e);
                }
            } finally {
                if (tempFile.exists()) {
                    tempFile.delete();
                }
            }
        }

        if (callback != null) {
            callback.onAllComplete(succeeded, failed);
        }
    }

    private static void collectPlainFiles(File dir, List<File> list) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                collectPlainFiles(f, list);
            } else if (!f.getName().endsWith(CONTAINER_EXTENSION)) {
                list.add(f);
            }
        }
    }

    private static void collectLockedFiles(File dir, List<File> list) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                collectLockedFiles(f, list);
            } else if (f.getName().endsWith(CONTAINER_EXTENSION)) {
                list.add(f);
            }
        }
    }

    private static void collectDocumentFiles(DocumentFile dir, List<DocumentFile> list, boolean lockedOnly) {
        DocumentFile[] files = dir.listFiles();
        for (DocumentFile f : files) {
            if (f.isDirectory()) {
                collectDocumentFiles(f, list, lockedOnly);
            } else {
                String name = f.getName();
                if (name != null) {
                    boolean isLocked = name.endsWith(CONTAINER_EXTENSION);
                    if (lockedOnly == isLocked) {
                        list.add(f);
                    }
                }
            }
        }
    }
}
