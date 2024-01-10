# Android Print Document Framework

- Utilizes `PrintDocumentAdapter` to render multi-page vector graphics directly into PDF streams.
- Dispatches rendering to `onWrite` callback on background thread pool, preventing UI stutters.
- Generates standards-compliant ISO 32000-1 vector PDFs readable across all desktop and mobile readers.
- Supports dynamic page budgeting and automatic layout pagination based on print target dimensions.
- Eliminates heavy third-party PDF rendering libraries by leveraging Android native `PdfDocument` APIs.
- Integrates seamlessly with Android OS print spooler and wireless Google Cloud / Mopria printers.
