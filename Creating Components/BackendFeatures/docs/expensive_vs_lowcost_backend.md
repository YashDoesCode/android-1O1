# Expensive vs Low-Cost Android Backend Architectures

- WorkManager delivers persistent, guaranteed background execution with constraint enforcement (battery, unmetered network).
- JobScheduler provides low-cost OS-native task batching with zero extra dependency footprint.
- Raw SQLite with compiled `SQLiteStatement` is 4x faster than high-overhead ORM libraries on bulk data insertion.
- Room abstraction introduces compile-time SQL verification and LiveData/Flow reactive observation.
- Zero-database binary streaming eliminates memory caching overhead for multi-gigabyte continuous payloads.
- Memory-mapped file I/O (`MappedByteBuffer`) enables zero-copy OS paging directly from kernel disk buffers.
- Periodic synchronization intervals must respect Android Doze mode and App Standby Buckets.
