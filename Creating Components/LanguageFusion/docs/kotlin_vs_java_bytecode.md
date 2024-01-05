# Kotlin vs Java Bytecode Compilation Analysis

- Kotlin's `@JvmInline` value classes compile down to raw primitive types with zero heap allocation overhead.
- Kotlin Sequences (`asSequence()`) prevent intermediate collection allocations during multi-step transforms.
- Java imperative `for` loops compile to simple bytecode jumps (`goto`, `if_icmpge`), avoiding lambda instance instantiation.
- Kotlin nullability metadata is embedded in `@Metadata` annotations for compile-time enforcement across modules.
- SAM (Single Abstract Method) conversion in Kotlin generates lightweight invokeDynamic call-sites on modern D8 dexer.
- Interoperability guarantees seamless two-way invocation between Java static utilities and Kotlin companion objects.
