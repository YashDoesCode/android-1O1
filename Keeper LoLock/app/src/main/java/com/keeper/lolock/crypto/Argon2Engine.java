package com.keeper.lolock.crypto;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.security.SecureRandom;
import java.util.Arrays;

public final class Argon2Engine {

    public static final int SALT_LENGTH = 16;
    public static final int DERIVED_KEY_LENGTH = 64;
    public static final int AES_KEY_LENGTH = 32;
    public static final int HMAC_KEY_LENGTH = 32;
    private static final int MEMORY_KB = 16384;
    private static final int ITERATIONS = 3;
    private static final int PARALLELISM = 1;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private Argon2Engine() {
    }

    public static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        SECURE_RANDOM.nextBytes(bytes);
        return bytes;
    }

    public static DerivedKeys deriveKeys(char[] password, byte[] salt) {
        if (password == null || salt == null) {
            throw new IllegalArgumentException("Password and salt must not be null");
        }

        byte[] passwordBytes = new byte[password.length * 2];
        for (int i = 0; i < password.length; i++) {
            passwordBytes[i * 2] = (byte) ((password[i] & 0xFF00) >> 8);
            passwordBytes[i * 2 + 1] = (byte) (password[i] & 0x00FF);
        }

        try {
            Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                    .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                    .withMemoryAsKB(MEMORY_KB)
                    .withIterations(ITERATIONS)
                    .withParallelism(PARALLELISM)
                    .withSalt(salt)
                    .build();

            Argon2BytesGenerator generator = new Argon2BytesGenerator();
            generator.init(params);

            byte[] derived = new byte[DERIVED_KEY_LENGTH];
            generator.generateBytes(passwordBytes, derived, 0, derived.length);

            byte[] encKey = Arrays.copyOfRange(derived, 0, AES_KEY_LENGTH);
            byte[] hmacKey = Arrays.copyOfRange(derived, AES_KEY_LENGTH, DERIVED_KEY_LENGTH);

            Arrays.fill(derived, (byte) 0);
            return new DerivedKeys(encKey, hmacKey);
        } finally {
            Arrays.fill(passwordBytes, (byte) 0);
        }
    }

    public static final class DerivedKeys implements AutoCloseable {
        private final byte[] encKey;
        private final byte[] hmacKey;

        public DerivedKeys(byte[] encKey, byte[] hmacKey) {
            this.encKey = encKey;
            this.hmacKey = hmacKey;
        }

        public byte[] getEncKey() {
            return encKey;
        }

        public byte[] getHmacKey() {
            return hmacKey;
        }

        @Override
        public void close() {
            if (encKey != null) {
                Arrays.fill(encKey, (byte) 0);
            }
            if (hmacKey != null) {
                Arrays.fill(hmacKey, (byte) 0);
            }
        }
    }
}
