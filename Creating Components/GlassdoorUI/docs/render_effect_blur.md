# RenderEffect Blur on Android 12+

- `RenderEffect.createBlurEffect` utilizes GPU fragment shaders for zero-copy viewport blurring.
- Bypasses legacy RenderScript CPU-GPU buffer copying and deprecated Bitmap allocations.
- Achieves sustained 60–120 FPS rendering during continuous list scrolling.
- Pairs with pure AMOLED dark backgrounds (`#000000`) for glassmorphic depth perception.
- Fallback for Android 9–11 utilizes high-contrast translucent tint overlays (`#121212` with 80% opacity).
- View-level `setRenderEffect` integrates cleanly into window compositing pipelines.
