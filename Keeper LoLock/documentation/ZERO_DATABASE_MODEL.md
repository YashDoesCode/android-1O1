# Zero-Database Architecture: Eliminating Centralized Brute-Force Targets

## 1. Problem Statement: The Database Vulnerability

Most existing mobile lock applications store metadata (file paths, hashes, file names, decryption keys, or master hash tables) inside a centralized database, commonly SQLite, Room, or Realm:

```
[Traditional Flawed Design]
Locked Files on Disk <----> [ Central SQLite / Room Database ] <----> Attacker Dumps DB
                               - Contains master hash
                               - Contains index of all hidden files
                               - Allows mass offline GPU attack
```

### Attack Vectors of Centralized Databases:
1. **Single Point of Compromise**: If an attacker gains device root, adb backup, or forensic extraction access, they simply extract the single `.db` file.
2. **Offline Dictionary Aggregation**: With the master hash in the database, the attacker can run millions of hashes per second across dictionary lists. Once broken, every single file is unlocked simultaneously.
3. **Forensic Metadata Leakage**: Even if file contents are encrypted, SQLite databases leak filenames, directory paths, file sizes, and timestamps.

---

## 2. The Keeper LoLock Zero-Database Model

Keeper LoLock operates with **ZERO central database**. No SQLite, Room, Realm, or index file is ever created.

```
[Keeper LoLock Zero-Database Design]
[file1.pdf.lolock]   --> Independent Salt (16B) + Argon2id (16MB) + Block Chain Hash
[photo.jpg.lolock]   --> Independent Salt (16B) + Argon2id (16MB) + Block Chain Hash
[archive.zip.lolock] --> Independent Salt (16B) + Argon2id (16MB) + Block Chain Hash
```

### Cryptographic Security Advantages:
1. **No Centralized Target**: There is no database file to dump. To discover what files exist, an attacker must inspect each individual `.lolock` file.
2. **Independent Per-File Salts**: Each file is encrypted with a distinct 16-byte random salt. Even if two files share the exact same content and master password, their derived keys, IVs, block hashes, and ciphertexts are completely distinct.
3. **Massive Attack Cost Multiplication**:
   - In traditional apps: Attacker runs 1 brute-force attack to break $N$ files.
   - In Keeper LoLock: Attacker must execute an independent 16MB Argon2id trial for *each* file. Breaking 100 files requires $100 \times$ the computational work.

---

## 3. Self-Contained `.lolock` Binary Container Format

Every `.lolock` file contains its complete metadata encapsulated within a cryptographically signed header:

| Offset | Field | Length | Description |
| :--- | :--- | :--- | :--- |
| `0x00` | Magic Header | 6 Bytes | ASCII bytes `"LOLOCK"` |
| `0x06` | Format Version | 1 Byte | Byte `0x01` |
| `0x07` | Salt | 16 Bytes | Random salt for Argon2id key derivation |
| `0x17` | Initialization Vector | 16 Bytes | Random IV for AES-256-CBC cipher |
| `0x27` | Key Verification Tag | 32 Bytes | HMAC-SHA256 of salt using derived HMAC key |
| `0x47` | Filename Length | 2 Bytes | Big-endian unsigned short ($L_f$) |
| `0x49` | Original Filename | $L_f$ Bytes | Original UTF-8 filename (e.g. `report.pdf`) |
| `0x49 + L_f` | MIME Length | 2 Bytes | Big-endian unsigned short ($L_m$) |
| `0x4B + L_f` | Original MIME Type | $L_m$ Bytes | UTF-8 MIME string (e.g. `application/pdf`) |
| `0x4B + L_f + L_m` | Original File Length| 8 Bytes | Big-endian 64-bit long (file size in bytes) |
| Dynamic | Genesis Anchor | 32 Bytes | SHA-256 of header fields |
| Dynamic | Block Stream | Dynamic | Chained chunks: `[Len (4B)] [Hash (32B)] [Ciphertext]` |
| EOF | Terminal Anchor | 36 Bytes | `[Len = 0 (4B)] [Final Chain Hash (32B)]` |

This format ensures that every container is fully self-describing, self-verifying, and completely decoupled from any centralized database.
