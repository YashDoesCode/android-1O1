# Hardware-Accelerated Custom Canvas Drawing

- Overrides `onDraw` utilizing hardware-accelerated Skia rendering pipelines on Android 9+.
- Avoids allocating `Paint` and `Path` objects inside `onDraw` to guarantee zero memory churn.
- Employs cubic bezier curves (`Path.cubicTo`) for antialiased latency curves and throughput graphs.
- Disables software layer rendering unless clipping complex non-convex path geometries.
- Leverages `setWillNotDraw(false)` to enable drawing on custom FrameLayout container subclasses.
