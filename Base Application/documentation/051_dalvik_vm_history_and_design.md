# The Dalvik Virtual Machine - Register-Based Architecture vs Java Stack

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// Standard JVM (Stack-Based):
// iload_1       (Push local var 1 onto operand stack)
// iload_2       (Push local var 2 onto operand stack)
// iadd          (Pop two values, add them, push result)
// istore_3      (Pop result and store in local var 3)

// Dalvik / ART (Register-Based):
// add-int v3, v1, v2  (Add v1 and v2, store directly in register v3! 1 instruction!)
```

### Notes, Curious Questions & System Mechanics

- to understand how modern Android code compiles and runs, you have to understand the birth of the Dalvik Virtual Machine
- Dalvik was created by Google engineer Dan Bornstein, who named it after the fishing village of Dalvík in Iceland where his ancestors lived
- when Android was designed in 2005-2007, running standard Java on mobile phones was notoriously slow and resource-heavy
- Oracle/Sun's standard Java Virtual Machine (JVM) is a 'Stack-Based' virtual machine
- in a stack-based VM, every arithmetic operation requires pushing values onto an operand stack, popping them to calculate, and pushing results back
- to add two numbers in standard Java bytecode takes 4 distinct instructions (`iload`, `iload`, `iadd`, `istore`)
- Dan Bornstein realized that stack-based execution requires massive amounts of instruction dispatch overhead that choked slow 200 MHz ARM processors
- he designed Dalvik from the ground up as a 'Register-Based' virtual machine
- in Dalvik, operations take virtual registers directly as operands: `add-int v3, v1, v2` performs the entire addition in ONE single instruction!
- Dalvik instruction streams are about 30% shorter in total instruction count compared to standard Java bytecode
- fewer instructions meant fewer CPU clock cycles, less memory bus traffic, and significantly lower battery consumption
- furthermore, Dalvik was designed to run multiple VM instances concurrently in separate Linux processes using minimal RAM
- standard Java compiled into individual `.class` files for every single class, each having its own constant pool full of duplicate string literals
- Dalvik introduced the `.dex` (Dalvik Executable) file format, which merges ALL class files in an entire app into a single unified binary
- in early Android versions (1.0 to 2.1), Dalvik was purely an interpreter, executing bytecode line-by-line
- in Android 2.2 Froyo, Google added a JIT (Just-In-Time) compiler to Dalvik, which compiled frequently executed loops into native machine code
- Dalvik powered Android from version 1.0 all the way through Android 4.4 KitKat
- beginner fallacy: i thought Dalvik was an official Java VM certified by Sun/Oracle
- Dalvik did not conform to the Java SE specification at all; it executed `.dex` bytecode, not `.class` bytecode, which led to famous legal battles
- although Dalvik was replaced by ART in Android 5.0, the `.dex` bytecode instruction set designed for Dalvik remains the standard bytecode format of Android today
- it was one of the boldest and most consequential architecture decisions in computer science history
