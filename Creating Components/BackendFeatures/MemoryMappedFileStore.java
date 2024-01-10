package com.keeper.components.backend;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public final class MemoryMappedFileStore {

    private MemoryMappedFileStore() {
    }

    public static byte[] readMapped(File file) throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r");
             FileChannel channel = raf.getChannel()) {
            long size = channel.size();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, size);
            byte[] data = new byte[(int) size];
            buffer.get(data);
            return data;
        }
    }
}
