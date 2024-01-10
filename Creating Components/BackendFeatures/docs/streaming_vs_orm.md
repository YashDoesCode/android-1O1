# Streaming I/O vs ORM Database Architecture

- Object-Relational Mappers (Room/Realm) force cursor-to-entity memory allocations causing GC thrashing on large datasets.
- Direct binary file streaming consumes constant $O(1)$ 64KB RAM buffers regardless of payload scale.
- SQLite row size limit (2 MB CursorWindow) causes crashes when storing large encrypted blobs.
- Direct file pipelines allow seamless resume, random seek, and block-chain integrity verification.
- Eliminates centralized SQLite database honey-pot vulnerabilities targeted by offline GPU crackers.
- Recommended architecture: lightweight key-value indices in memory, payload data streamed to disk containers.
