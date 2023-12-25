# kmp-tor-core
[![badge-license]][url-license]
[![badge-latest-release]][url-latest-release]

[![badge-kotlin]][url-kotlin]
[![badge-atomicfu]][url-atomicfu]
[![badge-encoding]][url-encoding]
[![badge-kotlincrypto-hash]][url-kotlincrypto-hash]
[![badge-kmp-file]][url-kmp-file]
[![badge-androidx-startup]][url-androidx-startup]

![badge-platform-android]
![badge-platform-jvm]
![badge-platform-js-node]
![badge-platform-linux]
![badge-platform-ios]
![badge-platform-macos]
![badge-platform-tvos]
![badge-platform-watchos]
![badge-platform-windows]
![badge-support-apple-silicon]
![badge-support-js-ir]
![badge-support-linux-arm]

Core components for [kmp-tor][url-kmp-tor] and [kmp-tor-resource][url-kmp-tor-resource]

### Modules

- `:library:core-api` is the publicly exposed API for which consumers of [kmp-tor][url-kmp-tor] 
  and [kmp-tor-resource][url-kmp-tor-resource] interact.
- `:library:core-resource` is a module not meant for public consumption
    - All classes and functions are annotated with `@InternalKmpTorApi`.
    - This is the workhorse that enables packaging and extraction of Jvm/Js/Native resources
      for installation to the filesystem. See [kmp-tor-resource][url-kmp-tor-resource].  
- `:library:core-resource-initializer` is a module not meant for public consumption
    - [kmp-tor-resource][url-kmp-tor-resource] modules utilizes this for Android to retrieve 
      the native library path (which is extracted to the application's `nativeLibraryDir`) upon 
      resource configuration/installation.

<!-- TAG_VERSION -->
[badge-latest-release]: https://img.shields.io/badge/latest--release-2.0.0--alpha02-blue.svg?style=flat
[badge-license]: https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat

<!-- TAG_DEPENDENCIES -->
[badge-androidx-startup]: https://img.shields.io/badge/androidx.startup-1.1.1-6EDB8D.svg?logo=android
[badge-atomicfu]: https://img.shields.io/badge/kotlinx.atomicfu-0.23.1-blue.svg?logo=kotlin
[badge-encoding]: https://img.shields.io/badge/encoding-2.1.0-blue.svg?style=flat
[badge-kotlincrypto-hash]: https://img.shields.io/badge/KotlinCrypto.hash-0.4.0-blue.svg?style=flat
[badge-kotlin]: https://img.shields.io/badge/kotlin-1.9.21-blue.svg?logo=kotlin
[badge-kmp-file]: https://img.shields.io/badge/kmp--file-0.1.0--alpha02-blue.svg?style=flat

<!-- TAG_PLATFORMS -->
[badge-platform-android]: http://img.shields.io/badge/-android-6EDB8D.svg?style=flat
[badge-platform-jvm]: http://img.shields.io/badge/-jvm-DB413D.svg?style=flat
[badge-platform-js]: http://img.shields.io/badge/-js-F8DB5D.svg?style=flat
[badge-platform-js-node]: https://img.shields.io/badge/-nodejs-68a063.svg?style=flat
[badge-platform-linux]: http://img.shields.io/badge/-linux-2D3F6C.svg?style=flat
[badge-platform-macos]: http://img.shields.io/badge/-macos-111111.svg?style=flat
[badge-platform-ios]: http://img.shields.io/badge/-ios-CDCDCD.svg?style=flat
[badge-platform-tvos]: http://img.shields.io/badge/-tvos-808080.svg?style=flat
[badge-platform-watchos]: http://img.shields.io/badge/-watchos-C0C0C0.svg?style=flat
[badge-platform-wasm]: https://img.shields.io/badge/-wasm-624FE8.svg?style=flat
[badge-platform-windows]: http://img.shields.io/badge/-windows-4D76CD.svg?style=flat
[badge-support-android-native]: http://img.shields.io/badge/support-[AndroidNative]-6EDB8D.svg?style=flat
[badge-support-apple-silicon]: http://img.shields.io/badge/support-[AppleSilicon]-43BBFF.svg?style=flat
[badge-support-js-ir]: https://img.shields.io/badge/support-[js--IR]-AAC4E0.svg?style=flat
[badge-support-linux-arm]: http://img.shields.io/badge/support-[LinuxArm]-2D3F6C.svg?style=flat

[url-androidx-startup]: https://developer.android.com/jetpack/androidx/releases/startup
[url-atomicfu]: https://github.com/Kotlin/kotlinx-atomicfu
[url-encoding]: https://github.com/05nelsonm/encoding
[url-kotlincrypto-hash]: https://github.com/KotlinCrypto/hash
[url-latest-release]: https://github.com/05nelsonm/kmp-tor-core/releases/latest
[url-license]: https://www.apache.org/licenses/LICENSE-2.0
[url-kotlin]: https://kotlinlang.org
[url-kmp-file]: https://github.com/05nelsonm/kmp-file
[url-kmp-tor]: https://github.com/05nelsonm/kmp-tor
[url-kmp-tor-resource]: https://github.com/05nelsonm/kmp-tor-resource
