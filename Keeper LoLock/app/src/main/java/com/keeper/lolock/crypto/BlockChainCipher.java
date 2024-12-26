package com.keeper.lolock.crypto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class BlockChainCipher {

    public static final int CHUNK_SIZE = 64 * 1024;
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";
    private static final String HASH_ALGORITHM = "SHA-256";

    private BlockChainCipher() {
    }

    public static void encryptChainedStream(
            InputStream in,
            OutputStream out,
            byte[] aesKey,
            byte[] iv,
            byte[] genesisAnchor,
            CryptoManager.ProgressListener listener,
            long totalInputBytes
    ) throws IOException, GeneralSecurityException {

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(aesKey, KEY_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] previousHash = Arrays.copyOf(genesisAnchor, genesisAnchor.length);

        DataOutputStream dataOut = new DataOutputStream(out);
        byte[] readBuffer = new byte[CHUNK_SIZE];
        long processedBytes = 0;

        int bytesRead;
        while ((bytesRead = in.read(readBuffer)) != -1) {
            byte[] cipherChunk = cipher.update(readBuffer, 0, bytesRead);

            if (cipherChunk != null && cipherChunk.length > 0) {
                digest.reset();
                digest.update(previousHash);
                digest.update(cipherChunk);
                byte[] currentHash = digest.digest();

                dataOut.writeInt(cipherChunk.length);
                dataOut.write(currentHash);
                dataOut.write(cipherChunk);

                System.arraycopy(currentHash, 0, previousHash, 0, previousHash.length);
            }

            processedBytes += bytesRead;
            if (listener != null) {
                listener.onProgress(processedBytes, totalInputBytes);
            }
        }

        byte[] finalChunk = cipher.doFinal();
        if (finalChunk != null && finalChunk.length > 0) {
            digest.reset();
            digest.update(previousHash);
            digest.update(finalChunk);
            byte[] currentHash = digest.digest();

            dataOut.writeInt(finalChunk.length);
            dataOut.write(currentHash);
            dataOut.write(finalChunk);

            System.arraycopy(currentHash, 0, previousHash, 0, previousHash.length);
        }

        dataOut.writeInt(0);
        dataOut.write(previousHash);
        dataOut.flush();
    }

    public static void decryptChainedStream(
            InputStream in,
            OutputStream out,
            byte[] aesKey,
            byte[] iv,
            byte[] genesisAnchor,
            CryptoManager.ProgressListener listener,
            long totalBytes
    ) throws IOException, GeneralSecurityException {

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(aesKey, KEY_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] previousHash = Arrays.copyOf(genesisAnchor, genesisAnchor.length);

        DataInputStream dataIn = new DataInputStream(in);
        long processedBytes = 0;

        while (true) {
            int chunkLen;
            try {
                chunkLen = dataIn.readInt();
            } catch (EOFException e) {
                break;
            }

            if (chunkLen == 0) {
                byte[] terminalAnchor = new byte[32];
                dataIn.readFully(terminalAnchor);
                if (!MessageDigest.isEqual(previousHash, terminalAnchor)) {
                    throw new SecurityException("Corrupted blockchain termination anchor");
                }
                break;
            }

            byte[] expectedHash = new byte[32];
            dataIn.readFully(expectedHash);

            byte[] cipherChunk = new byte[chunkLen];
            dataIn.readFully(cipherChunk);

            digest.reset();
            digest.update(previousHash);
            digest.update(cipherChunk);
            byte[] computedHash = digest.digest();

            if (!MessageDigest.isEqual(expectedHash, computedHash)) {
                throw new SecurityException("Cryptographic block chain integrity check failed");
            }

            System.arraycopy(computedHash, 0, previousHash, 0, previousHash.length);

            byte[] plainChunk = cipher.update(cipherChunk);
            if (plainChunk != null && plainChunk.length > 0) {
                out.write(plainChunk);
            }

            processedBytes += chunkLen;
            if (listener != null) {
                listener.onProgress(processedBytes, totalBytes);
            }
        }

        byte[] finalPlain = cipher.doFinal();
        if (finalPlain != null && finalPlain.length > 0) {
            out.write(finalPlain);
        }
        out.flush();
    }
}
