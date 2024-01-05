package com.keeper.components.fusion

@JvmInline
value class ContainerKey(val rawKey: String) {
    init {
        require(rawKey.length >= 16) { "Container key must be at least 16 characters" }
    }
}

@JvmInline
value class FileDigest(val hashBytes: ByteArray)
