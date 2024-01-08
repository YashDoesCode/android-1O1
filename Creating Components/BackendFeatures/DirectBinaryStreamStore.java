package com.keeper.components.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public final class DirectBinaryStreamStore {

    private final File baseDir;

    public DirectBinaryStreamStore(File baseDir) {
        this.baseDir = baseDir;
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }

    public void store(String key, InputStream stream) throws Exception {
        File target = new File(baseDir, key);
        try (OutputStream out = new FileOutputStream(target)) {
            byte[] buf = new byte[64 * 1024];
            int r;
            while ((r = stream.read(buf)) != -1) {
                out.write(buf, 0, r);
            }
            out.flush();
        }
    }

    public InputStream retrieve(String key) throws Exception {
        File target = new File(baseDir, key);
        return new FileInputStream(target);
    }
}
