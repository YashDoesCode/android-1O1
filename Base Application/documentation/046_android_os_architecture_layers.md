# Android OS Architecture Layers - From Linux Kernel to Compose UI

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// The 5 Classic Architectural Layers of the Android Operating System:
// 1. System Apps & User Apps (Base Application, Settings, Phone, Camera)
// 2. Java API Framework (ActivityManager, WindowManager, ContentProviders)
// 3. Android Runtime (ART) & Native C/C++ Libraries (SQLite, Skia, WebKit, OpenGL)
// 4. Hardware Abstraction Layer (HAL) (Camera HAL, Audio HAL, Sensors)
// 5. Linux Kernel (Process scheduling, Memory, Drivers, Binder IPC, Security)
```

### Notes, Curious Questions & System Mechanics

- to truly understand how `Base Application` executes, you have to visualize the full 5-layer Android operating system stack
- Layer 1 (The Foundation): The modified Linux Kernel
- Android does not use a proprietary microkernel; it is built directly on top of the open-source Linux kernel (LTS 5.x / 6.x)
- the Linux kernel handles low-level hardware drivers: display, camera, Wi-Fi, Bluetooth, audio, power management, and memory paging
- Google added custom kernel patches that define Android: the Binder IPC driver (`/dev/binder`), Ashmem (Anonymous Shared Memory), and wakelocks
- Layer 2: The Hardware Abstraction Layer (HAL)
- HAL provides standard C/C++ interface contracts that hardware vendors (Qualcomm, MediaTek, Samsung Exynos) implement
- when Android wants to take a photo, the camera framework calls standard HAL APIs without caring what physical sensor is attached
- Layer 3: Native C/C++ Libraries & Android Runtime (ART)
- this layer contains native engines compiled directly to ARM/x86 machine code: Skia (2D graphics engine used by Compose), SQLite, OpenGL ES, Vulkan
- it also contains ART (Android Runtime), the virtual machine and JIT/AOT compiler that executes our application DEX bytecode
- Layer 4: The Java API Framework
- this is the rich toolkit of Java/Kotlin classes exposed to application developers: `ActivityManager`, `NotificationManager`, `Resources`
- it acts as the user-space orchestrator, translating high-level app requests into low-level Binder IPC calls to system daemons
- Layer 5 (The Top): System Apps and Third-Party Applications
- this is where `Base Application` lives! Alongside native system apps like Phone, Contacts, Settings, and Chrome
- crucially, third-party apps live in the EXACT SAME execution layer as built-in system apps
- third-party apps are not second-class citizens; they use the same Java API Framework and run under the same ART runtime
- the only difference between system apps and third-party apps is privilege levels and system signature permissions
- beginner fallacy: i thought our Kotlin code was talking directly to the phone's screen and Wi-Fi chip
- our code calls the Java API Framework, which makes Binder IPC calls to System Server, which calls HAL, which talks to the Linux kernel drivers
- this strict layering allows Android to run reliably across thousands of wildly different hardware configurations
- it is one of the most successful software engineering architectures ever built by human civilization
