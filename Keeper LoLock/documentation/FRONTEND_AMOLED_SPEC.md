# Frontend AMOLED Design System & Overlay Specification

## 1. AMOLED Design Philosophy

Keeper LoLock implements a **Pure Jet Black AMOLED** visual architecture (`#000000`) engineered for high-end OLED displays:
- **Zero Pixel Bleed**: Black pixels on OLED screens are completely switched off (0.000 nits), saving battery and delivering infinite contrast.
- **Silver & Metallic Aesthetic**: Highlights, borders, buttons, and brand banners employ curated silver metallic gradients (`#C5C5C5`, `#E0E0E0`, `#888888`) derived directly from the official Keeper brand assets.
- **Minimalist Restraint**: No cluttered analytics, advertising banners, or decorative clutter. The UI focuses purely on security state and file operations.

---

## 2. Color Palette Tokens

| Token Name | Hex Code | Visual Role |
| :--- | :--- | :--- |
| `amoled_black` | `#000000` | True OLED pitch-black backdrop |
| `amoled_card` | `#0A0A0A` | Minimal elevated panel background |
| `amoled_surface` | `#121212` | Elevated surface for input elements |
| `amoled_stroke` | `#222222` | Subtle metallic boundary border |
| `amoled_stroke_active`| `#444444` | Focused/Active border state |
| `silver_white` | `#FFFFFF` | Primary headers and high-contrast typography |
| `silver_light` | `#E0E0E0` | Secondary headers and primary button gradients |
| `silver_main` | `#C5C5C5` | Metallic brand accents and icons |
| `silver_muted` | `#888888` | Metadata labels and timestamps |
| `silver_dark` | `#4A4A4A` | Placeholder text and inactive indicators |
| `status_green` | `#00E676` | Active master password state & integrity pass |
| `status_red` | `#FF5252` | Security alert / setup required state |

---

## 3. UI Component Hierarchy

### 3.1 Main Dashboard (`MainActivity`)
1. **Brand Header**: Scaled, high-resolution metallic silver ribbon banner (`keeper_silver_brand.png`).
2. **Master Security State Card**:
   - Vector silver lock indicator (`ic_lock_silver.xml`).
   - Dynamic 180-day rotation badge showing remaining days until the 6-month recommended master key renewal.
   - Master Password initialization / change action button.
3. **Cryptographic Vault Operations**:
   - `Encrypt File`: Launches Storage Access Framework (SAF) single file picker (`*/*`).
   - `Encrypt Directory`: Launches SAF directory tree picker (`OPEN_DOCUMENT_TREE`).
   - `Decrypt File`: Launches SAF file picker filtered to `.lolock` containers.
   - `Decrypt Directory`: Launches SAF directory tree picker for bulk in-place restoration.
4. **Streaming Progress Card**:
   - Deterministic horizontal progress bar (`progressTint="@color/silver_light"`).
   - Real-time filename, chunk index, and streamed bytes counter.

---

## 4. Native Overlay Window (`UnlockActivity`)

When a user taps an encrypted `.lolock` file from ANY Android file manager:
- Android intercepts the file click via intent filters and renders `UnlockActivity`.
- It displays as a **native floating AMOLED dialog** (`Theme.KeeperLoLock.Overlay`) directly over the caller application:
  - Displays container filename and formatted size (KB/MB).
  - Clean AMOLED password field with secure dot masking.
  - **Unlock and Open**: Decrypts directly into private app cache and dispatches `Intent.ACTION_VIEW` via `FileProvider` to the user's default app (e.g. PDF viewer, video player, image gallery).
  - **Decrypt to Storage**: Restores the decrypted file back to the original filesystem directory and removes the `.lolock` container.
  - **Dismiss**: Immediately closes the overlay window and returns the user to the file manager.
