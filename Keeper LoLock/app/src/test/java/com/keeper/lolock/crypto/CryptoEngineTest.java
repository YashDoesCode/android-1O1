package com.keeper.lolock.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Arrays;
import java.util.Random;

public class CryptoEngineTest {

    @BeforeClass
    public static void setup() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testArgon2KeyDerivationDeterminism() {
        char[] pass = "MasterVaultKey#2024".toCharArray();
        byte[] salt = Argon2Engine.generateRandomBytes(16);

        Argon2Engine.DerivedKeys k1 = Argon2Engine.deriveKeys(pass, salt);
        Argon2Engine.DerivedKeys k2 = Argon2Engine.deriveKeys(pass, salt);

        Assert.assertArrayEquals(k1.getEncKey(), k2.getEncKey());
        Assert.assertArrayEquals(k1.getHmacKey(), k2.getHmacKey());

        byte[] otherSalt = Argon2Engine.generateRandomBytes(16);
        Argon2Engine.DerivedKeys k3 = Argon2Engine.deriveKeys(pass, otherSalt);
        Assert.assertFalse(Arrays.equals(k1.getEncKey(), k3.getEncKey()));
    }

    @Test
    public void testStreamEncryptionAndDecryptionRoundTrip() throws Exception {
        char[] password = "QuantumSecurePassword!88".toCharArray();
        String originalFilename = "confidential_archive.tar.gz";
        String mimeType = "application/gzip";

        byte[] originalPayload = new byte[150 * 1024];
        new Random(42).nextBytes(originalPayload);

        ByteArrayOutputStream cipherOut = new ByteArrayOutputStream();
        CryptoManager.encryptStream(
                new ByteArrayInputStream(originalPayload),
                cipherOut,
                password,
                originalFilename,
                mimeType,
                originalPayload.length,
                null
        );

        byte[] cipherBytes = cipherOut.toByteArray();
        Assert.assertTrue(cipherBytes.length > originalPayload.length);

        CryptoManager.FileHeader header = CryptoManager.readHeader(new ByteArrayInputStream(cipherBytes));
        Assert.assertEquals(originalFilename, header.originalFilename);
        Assert.assertEquals(mimeType, header.mimeType);
        Assert.assertEquals(originalPayload.length, header.originalFileSize);
        Assert.assertTrue(CryptoManager.verifyPassword(header, password));
        Assert.assertFalse(CryptoManager.verifyPassword(header, "wrongPassword".toCharArray()));

        ByteArrayOutputStream plainOut = new ByteArrayOutputStream();
        CryptoManager.FileHeader decryptedHeader = CryptoManager.decryptStream(
                new ByteArrayInputStream(cipherBytes),
                plainOut,
                password,
                null
        );

        Assert.assertEquals(originalFilename, decryptedHeader.originalFilename);
        Assert.assertArrayEquals(originalPayload, plainOut.toByteArray());
    }

    @Test
    public void testEmptyPayloadEncryptionAndDecryption() throws Exception {
        char[] password = "EmptyFileTest#123".toCharArray();
        byte[] emptyPayload = new byte[0];

        ByteArrayOutputStream cipherOut = new ByteArrayOutputStream();
        CryptoManager.encryptStream(
                new ByteArrayInputStream(emptyPayload),
                cipherOut,
                password,
                "empty.txt",
                "text/plain",
                0,
                null
        );

        ByteArrayOutputStream plainOut = new ByteArrayOutputStream();
        CryptoManager.FileHeader decryptedHeader = CryptoManager.decryptStream(
                new ByteArrayInputStream(cipherOut.toByteArray()),
                plainOut,
                password,
                null
        );

        Assert.assertEquals("empty.txt", decryptedHeader.originalFilename);
        Assert.assertEquals(0, plainOut.toByteArray().length);
    }

    @Test(expected = SecurityException.class)
    public void testWrongPasswordRejection() throws Exception {
        char[] password = "CorrectMasterPassword".toCharArray();
        byte[] payload = "Secured Content".getBytes(StandardCharsets.UTF_8);

        ByteArrayOutputStream cipherOut = new ByteArrayOutputStream();
        CryptoManager.encryptStream(
                new ByteArrayInputStream(payload),
                cipherOut,
                password,
                "note.txt",
                "text/plain",
                payload.length,
                null
        );

        ByteArrayOutputStream plainOut = new ByteArrayOutputStream();
        CryptoManager.decryptStream(
                new ByteArrayInputStream(cipherOut.toByteArray()),
                plainOut,
                "IncorrectPassword".toCharArray(),
                null
        );
    }

    @Test(expected = SecurityException.class)
    public void testBlockTamperingDetection() throws Exception {
        char[] password = "DefenseGradeAccess#99".toCharArray();
        byte[] payload = "Top secret system telemetry and payload".getBytes(StandardCharsets.UTF_8);

        ByteArrayOutputStream cipherOut = new ByteArrayOutputStream();
        CryptoManager.encryptStream(
                new ByteArrayInputStream(payload),
                cipherOut,
                password,
                "telemetry.log",
                "text/plain",
                payload.length,
                null
        );

        byte[] cipherBytes = cipherOut.toByteArray();
        cipherBytes[cipherBytes.length - 20] ^= 0x55;

        ByteArrayOutputStream plainOut = new ByteArrayOutputStream();
        CryptoManager.decryptStream(
                new ByteArrayInputStream(cipherBytes),
                plainOut,
                password,
                null
        );
    }
}
