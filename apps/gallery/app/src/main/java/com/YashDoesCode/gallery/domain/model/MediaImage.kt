package com.YashDoesCode.gallery.domain.model

data class MediaImage(
    val id: Long,
    val uri: String,
    val displayName: String,
    val dateAdded: Long,
    val sizeBytes: Long,
    val width: Int,
    val height: Int,
    val bucketId: String,
    val bucketName: String
) {
    val formattedSize: String
        get() {
            val kb = sizeBytes / 1024.0
            val mb = kb / 1024.0
            return if (mb >= 1.0) {
                String.format(java.util.Locale.US, "%.2f MB", mb)
            } else {
                String.format(java.util.Locale.US, "%.1f KB", kb)
            }
        }

    val formattedResolution: String
        get() = if (width > 0 && height > 0) "${width} × ${height}" else "Unknown"
}

data class Album(
    val id: String,
    val name: String,
    val coverUri: String?,
    val imageCount: Int
)
