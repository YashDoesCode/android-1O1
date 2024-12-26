package com.keeper.lolock.crypto;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class CryptoManager {

    public static final byte[] MAGIC = new byte[]{'L', 'O', 'L', 'O', 'C', 'K'};
    public static final byte VERSION = 0x01;
    public static final int IV_LENGTH = 16;
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String HASH_ALGORITHM = "SHA-256";

    public interface ProgressListener {
        void onProgress(long bytesProcessed, long totalBytes);
    }

    public static final class FileHeader {
        public final String originalFilename;
        public final String mimeType;
        public final long originalFileSize;
        public final byte[] salt;
        public final byte[] iv;
        public final byte[] verificationTag;
        public final byte[] genesisAnchor;

        public FileHeader(
                String originalFilename,
                String mimeType,
                long originalFileSize,
                byte[] salt,
                byte[] iv,
                byte[] verificationTag,
                byte[] genesisAnchor
        ) {
            this.originalFilename = originalFilename;
            this.mimeType = mimeType;
            this.originalFileSize = originalFileSize;
            this.salt = salt;
            this.iv = iv;
            this.verificationTag = verificationTag;
            this.genesisAnchor = genesisAnchor;
        }
    }

    private CryptoManager() {
    }

    public static void encryptStream(
            InputStream in,
            OutputStream out,
            char[] password,
            String originalFilename,
            String mimeType,
            long inputLength,
            ProgressListener listener
    ) throws IOException, GeneralSecurityException {

        byte[] salt = Argon2Engine.generateRandomBytes(Argon2Engine.SALT_LENGTH);
        byte[] iv = Argon2Engine.generateRandomBytes(IV_LENGTH);

        try (Argon2Engine.DerivedKeys keys = Argon2Engine.deriveKeys(password, salt)) {
            byte[] verificationTag = computeHmac(keys.getHmacKey(), salt);

            byte[] filenameBytes = (originalFilename != null ? originalFilename : "file")
                    .getBytes(StandardCharsets.UTF_8);
            byte[] mimeBytes = (mimeType != null ? mimeType : "application/octet-stream")
                    .getBytes(StandardCharsets.UTF_8);

            MessageDigest genesisDigest = MessageDigest.getInstance(HASH_ALGORITHM);
            genesisDigest.update(MAGIC);
            genesisDigest.update(VERSION);
            genesisDigest.update(salt);
            genesisDigest.update(iv);
            genesisDigest.update(filenameBytes);
            genesisDigest.update(mimeBytes);
            genesisDigest.update(longToBytes(inputLength));
            byte[] genesisAnchor = genesisDigest.digest();

            DataOutputStream dataOut = new DataOutputStream(out);
            dataOut.write(MAGIC);
            dataOut.writeByte(VERSION);
            dataOut.write(salt);
            dataOut.write(iv);
            dataOut.write(verificationTag);
            dataOut.writeShort(filenameBytes.length);
            dataOut.write(filenameBytes);
            dataOut.writeShort(mimeBytes.length);
            dataOut.write(mimeBytes);
            dataOut.writeLong(inputLength);
            dataOut.write(genesisAnchor);
            dataOut.flush();

            BlockChainCipher.encryptChainedStream(
                    in,
                    out,
                    keys.getEncKey(),
                    iv,
                    genesisAnchor,
                    listener,
                    inputLength
            );
        }
    }

    public static FileHeader readHeader(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(in);

        byte[] magic = new byte[MAGIC.length];
        dataIn.readFully(magic);
        if (!Arrays.equals(MAGIC, magic)) {
            throw new IllegalArgumentException("Invalid container signature");
        }

        byte version = dataIn.readByte();
        if (version != VERSION) {
            throw new IllegalArgumentException("Unsupported container version: " + version);
        }

        byte[] salt = new byte[Argon2Engine.SALT_LENGTH];
        dataIn.readFully(salt);

        byte[] iv = new byte[IV_LENGTH];
        dataIn.readFully(iv);

        byte[] verificationTag = new byte[32];
        dataIn.readFully(verificationTag);

        short filenameLen = dataIn.readShort();
        byte[] filenameBytes = new byte[filenameLen];
        dataIn.readFully(filenameBytes);
        String filename = new String(filenameBytes, StandardCharsets.UTF_8);

        short mimeLen = dataIn.readShort();
        byte[] mimeBytes = new byte[mimeLen];
        dataIn.readFully(mimeBytes);
        String mime = new String(mimeBytes, StandardCharsets.UTF_8);

        long originalLength = dataIn.readLong();

        byte[] genesisAnchor = new byte[32];
        dataIn.readFully(genesisAnchor);

        return new FileHeader(filename, mime, originalLength, salt, iv, verificationTag, genesisAnchor);
    }

    public static boolean verifyPassword(FileHeader header, char[] password) {
        if (header == null || password == null) {
            return false;
        }
        try (Argon2Engine.DerivedKeys keys = Argon2Engine.deriveKeys(password, header.salt)) {
            byte[] computedTag = computeHmac(keys.getHmacKey(), header.salt);
            return MessageDigest.isEqual(header.verificationTag, computedTag);
        } catch (Exception e) {
            return false;
        }
    }

    public static FileHeader decryptStream(
            InputStream in,
            OutputStream out,
            char[] password,
            ProgressListener listener
    ) throws IOException, GeneralSecurityException {

        FileHeader header = readHeader(in);

        try (Argon2Engine.DerivedKeys keys = Argon2Engine.deriveKeys(password, header.salt)) {
            byte[] computedTag = computeHmac(keys.getHmacKey(), header.salt);
            if (!MessageDigest.isEqual(header.verificationTag, computedTag)) {
                throw new SecurityException("Authentication failure: invalid credentials");
            }

            BlockChainCipher.decryptChainedStream(
                    in,
                    out,
                    keys.getEncKey(),
                    header.iv,
                    header.genesisAnchor,
                    listener,
                    header.originalFileSize
            );

            return header;
        }
    }

    private static byte[] computeHmac(byte[] key, byte[] data) throws GeneralSecurityException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(new SecretKeySpec(key, HMAC_ALGORITHM));
        return mac.doFinal(data);
    }

    private static byte[] longToBytes(long value) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return result;
    }
}
