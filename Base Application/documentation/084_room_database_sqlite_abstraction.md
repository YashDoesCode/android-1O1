# Room Database - SQLite Abstraction, Compile-Time Verification, and DAO

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// How Room bridges SQLite to Kotlin Data Classes:
@Entity(tableName = "threat_logs")
data class ThreatLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)

@Dao
interface ThreatDao {
    @Query("SELECT * FROM threat_logs ORDER BY timestamp DESC")
    fun getAllThreats(): Flow<List<ThreatLog>> // Emits updates automatically!

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreat(log: ThreatLog)
}
```

### Notes, Curious Questions & System Mechanics

- every Android device running since version 1.0 has had a full, native C-based relational database engine built directly into the OS: SQLite
- in early Android development, interacting with SQLite required extending `SQLiteOpenHelper` and writing raw SQL strings in Java
- working with raw SQLite was a nightmare: if you had a typo in your SQL query (`"SELEC * FROM users"`), the compiler wouldn't catch it!
- the bug would only explode as a fatal crash at runtime when a user opened that specific screen in production
- you also had to write 50 lines of tedious boilerplate code to manually read rows from a raw `Cursor` object and map them to Java fields
- to fix this, Google created the 'Room Persistence Library' as part of Android Jetpack
- Room is an abstraction layer over native SQLite that provides compile-time verification of SQL queries and object mapping
- Component 1: `@Entity`
- a standard Kotlin data class annotated with `@Entity(tableName = "...")` represents a physical SQLite table
- Room automatically maps fields to table columns and handles primary keys, auto-increment, and foreign key relationships
- Component 2: `@Dao` (Data Access Object)
- an interface annotated with `@Dao` defines the database operations (queries, inserts, updates, deletes)
- here is the superpower of Room: during compilation, Room's annotation processor (KSP) actually parses and validates your `@Query` SQL strings!
- if you misspell a column name or query a non-existent table, the Gradle build FAILS immediately at compile-time with a clear error!
- Component 3: Reactive Queries with Coroutines and Flow
- notice the DAO return type: `fun getAllThreats(): Flow<List<ThreatLog>>`
- Room automatically monitors the underlying SQLite database using table update triggers
- whenever a new row is inserted, Room queries the database on a background thread and emits the updated list to your Compose UI via the `Flow`!
- you never have to manually refresh screens or re-query databases after writing data
- Room also handles database migrations cleanly through `@Database(version = 2)` and `Migration` objects
- beginner fallacy: i thought Room was a completely new NoSQL database engine like MongoDB
- Room is pure SQLite under the hood; you can inspect the generated `.db` file using standard SQLite command-line tools or Android Studio's 'Database Inspector'
- Room turns relational database programming in Android from a dangerous chore into a type-safe, reactive dream
