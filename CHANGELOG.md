# CHANGELOG

## Version 2.4.2 (2025-12-16)
 - Updates `kotlin` to `2.2.21` [[#115]][115]
 - Updates `encoding` to `2.6.0` [[#115]][115]
 - Updates `kmp-file` to `0.6.0` [[#115]][115]
 - Use `OpenExcl.checkMode` when validating `Resource.Builder.mode` input [[#116]][116]

## Version 2.4.1 (2025-10-26)
 - Fixes issue with Js/WasmJs Browser due to direct use of `require`, instead of retrieving
   the function via `eval` [[#114]][114]

## Version 2.4.0 (2025-09-19)
 - Updates `kotlin` to `2.2.20` [[#111]][111]
 - Updates `encoding` to `2.5.0` [[#112]][112]
 - Updates `immutable` to `0.3.0` [[#112]][112]
 - Updates `kotlinx.atomicfu` to `0.29.0` [[#111]][111]
 - Updates `kmp-file` to `0.5.0` [[#112]][112]
 - Updates `kotlincrypto.hash` to `0.8.0` [[#112]][112]
 - Adds check for `32-bit` Linux when hardware reporst `aarch64` [[#108]][108]
 - Fixes detection of `linux-musl` host on Java8 [[#110]][110]
 - Lower supported `KotlinVersion` to `1.9` [[#112]][112]
     - Source sets `js`, `wasmJs`, & `jsWasmJs` require a minimum `KotlinVersion` of `2.0`

## Version 2.3.1 (2025-08-20)
 - Updates `kotlinx.atomicfu` to `0.28.0` [[#100]][100]
 - Updates `kmp-file` to `0.4.0` [[#100]][100]
 - Adds support for `wasmJs` [[#102]][102]
 - Deprecates `Resource.Builder.isExecutable` in favor of passing a mode [[#105]][105]
 - Fixes potential double-close on Kotlin/Native [[#106]][106]

## Version 2.3.0 (2025-06-11)
 - Updates `kotlin` to `2.1.21` [[#85]][85]
 - Updates `kmp-file` to `0.3.0` [[#85]][85]
 - Adds support for the following targets [[#85]][85]:
     - `androidNativeArm32`
     - `androidNativeArm64`
     - `androidNativeX64`
     - `androidNativeX86`
 - Adds `OSArch.Riscv64` for Jvm/Js [[#87]][87]
 - Deprecates module `:library:common-lib-locator` [[#97]][97]
 - Improves `OSInfo.osHost` detection of android by checking for presence of `libandroid.so` [[#99]][99]

## Version 2.2.0 (2025-02-26)
 - Updates `kotlin` to `2.1.10` [[#83]][83]
 - Updates `encoding` to `2.4.0` [[#83]][83]
 - Updates `immutable` to `0.2.0` [[#83]][83]
 - Updates `kmp-file` to `0.2.0` [[#83]][83]
 - Updates `kotlinx.atomicfu` to `0.27.0` [[#83]][83]
 - Updates `kotlincrypto.hash` to `0.7.0` [[#83]][83]
 - Performance improvements to internal lock/synchronization usage [[#79]][79] [[#82]][82]

## Version 2.1.2 (2025-02-13)
 - Updates `kotlincrypto.hash` to `0.6.1` [[#77]][77]

## Version 2.1.1 (2025-01-15)
 - Adds Dokka documentation at `https://kmp-tor-common.matthewnelson.io` [[#73]][73]
 - Updates `encoding` to `2.3.1` [[#75]][75]
 - Updates `kotlincrypto.hash` to `0.6.0` [[#76]][76]

## Version 2.1.0 (2024-11-30)
 - Rename repository to `kmp-tor-common` [[#45]][45]
 - Rename modules/packages to use `io.matthewnelson.kmp.tor.common...` prefix [[#45]][45]
 - Replace `ResourceInstaller` with `ResourceLoader.Tor.Exec` and `ResourceLoader.Tor.NoExec` 
   abstractions [[#49]][49], [[#53]][53], [[#54]][54], [[#55]][55], [[#57]][57], [[#59]][59], 
   [[#61]][61], [[#63]][63], [[#66]][66], [[#67]][67], [[#69]][69], [[#71]][71]

## Version 2.0.1 (2024-09-01)
 - Updates `encoding` to `2.2.2` [[#42]][42]
 - Updates `immutable` to `0.1.4` [[#42]][42]
 - Updates `kmp-file` to `0.1.1` [[#42]][42]
 - Updates `kotlincrypto.hash` to `0.5.3` [[#42]][42]
 - Fixes multiplatform metadata manifest `unique_name` parameter for
   all source sets to be truly unique. [[#42]][42]
 - Updates jvm `.kotlin_module` with truly unique file name. [[#42]][42]

## Version 2.0.0 (2024-06-15)
 - Updates `kotlin` to `1.9.24` [[#39]][39]
 - Updates `immutable` to `0.1.3` [[#39]][39]
 - Updates `kmp-file` to `0.1.0` [[#39]][39]
 - Updates `kotlinx.atomicfu` to `0.24.0` [[#39]][39]

## Version 2.0.0-alpha10 (2024-03-19)
 - Updates `kotlin` to `1.9.23` [[#38]][38]
 - Updates `encoding` to `2.2.1` [[#38]][38]
 - Updates `immutable` to `0.1.2` [[#38]][38]
 - Updates `kmp-file` to `0.1.0-beta03` [[#38]][38]
 - Updates `kotlincrypto.hash` to `0.5.1` [[#38]][38]
 - Adds support for `JPMS` via Multi-Release Jar [[#38]][38]

## Version 2.0.0-alpha09 (2024-03-04)
 - Updates `kmp-file` to `0.1.0-beta02` [[#36]][36]
 - Use flag `O_CLOEXEC` when opening `gzip` file on `UNIX` [[#35]][35]

## Version 2.0.0-alpha08 (2024-02-25)
 - Updates `kotlin` to `1.9.22` [[#30]][30]
 - Updates `kmp-file` to `0.1.0-beta01` [[#30]][30]
 - Updates `kotlinx.atomicfu` to `0.23.2` [[#30]][30]
 - Removes `ProcessRunner` and associated `Process` extension functions 
   for jvm from the "public" API [[#31]][31]
 - Reduces `minSdk` for Android to `15` [[#33]][33]

## Version 2.0.0-alpha07 (2024-02-08)
 - Adds `ProcessRunner` to the "public" API for jvm [[#28]][28]
 - Updates `kmp-file` to `0.1.0-alpha06`

## Version 2.0.0-alpha06 (2024-01-22)
 - Updates `immutable` to `0.1.0` [[#24]][24]
 - Refactor `core-resource-initializer` [[#26]][26]
     - Renamed module to `core-lib-locator`
     - Renamed class to `io.matthewnelson.kmp.tor.core.lib.locator.KmpTorLibLocator`

## Version 2.0.0-alpha05 (2024-01-15)
 - Migrates `Immutable` implementations to `immutable` library [[#23]][23]

## Version 2.0.0-alpha04 (2024-01-03)
 - Updates `kmp-file` to `0.1.0-alpha05` [[#17]][17]

## Version 2.0.0-alpha03 (2024-01-01)
 - Fixes `Immutable` class implementations [[#14]][14]
 - Adds `ExperimentalKmpTorApi` annotation [[#16]][16]
 - Updates `kmp-file` to `0.1.0-alpha03` [[#13]][13]

## Version 2.0.0-alpha02 (2023-12-24)
 - Adds `core-resource-initializer` module [[#9]][9]

## Version 2.0.0-alpha01 (2023-12-23)
 - Initial `alpha01` release

[9]: https://github.com/05nelsonm/kmp-tor-common/pull/9
[13]: https://github.com/05nelsonm/kmp-tor-common/pull/13
[14]: https://github.com/05nelsonm/kmp-tor-common/pull/14
[16]: https://github.com/05nelsonm/kmp-tor-common/pull/16
[17]: https://github.com/05nelsonm/kmp-tor-common/pull/17
[23]: https://github.com/05nelsonm/kmp-tor-common/pull/23
[24]: https://github.com/05nelsonm/kmp-tor-common/pull/24
[26]: https://github.com/05nelsonm/kmp-tor-common/pull/26
[28]: https://github.com/05nelsonm/kmp-tor-common/pull/28
[30]: https://github.com/05nelsonm/kmp-tor-common/pull/30
[31]: https://github.com/05nelsonm/kmp-tor-common/pull/31
[33]: https://github.com/05nelsonm/kmp-tor-common/pull/33
[35]: https://github.com/05nelsonm/kmp-tor-common/pull/35
[36]: https://github.com/05nelsonm/kmp-tor-common/pull/36
[38]: https://github.com/05nelsonm/kmp-tor-common/pull/38
[39]: https://github.com/05nelsonm/kmp-tor-common/pull/39
[42]: https://github.com/05nelsonm/kmp-tor-common/pull/42
[45]: https://github.com/05nelsonm/kmp-tor-common/pull/45
[49]: https://github.com/05nelsonm/kmp-tor-common/pull/49
[53]: https://github.com/05nelsonm/kmp-tor-common/pull/53
[54]: https://github.com/05nelsonm/kmp-tor-common/pull/54
[55]: https://github.com/05nelsonm/kmp-tor-common/pull/55
[57]: https://github.com/05nelsonm/kmp-tor-common/pull/57
[59]: https://github.com/05nelsonm/kmp-tor-common/pull/59
[61]: https://github.com/05nelsonm/kmp-tor-common/pull/61
[63]: https://github.com/05nelsonm/kmp-tor-common/pull/63
[66]: https://github.com/05nelsonm/kmp-tor-common/pull/66
[67]: https://github.com/05nelsonm/kmp-tor-common/pull/67
[69]: https://github.com/05nelsonm/kmp-tor-common/pull/69
[71]: https://github.com/05nelsonm/kmp-tor-common/pull/71
[73]: https://github.com/05nelsonm/kmp-tor-common/pull/73
[75]: https://github.com/05nelsonm/kmp-tor-common/pull/75
[76]: https://github.com/05nelsonm/kmp-tor-common/pull/76
[77]: https://github.com/05nelsonm/kmp-tor-common/pull/77
[79]: https://github.com/05nelsonm/kmp-tor-common/pull/79
[82]: https://github.com/05nelsonm/kmp-tor-common/pull/82
[83]: https://github.com/05nelsonm/kmp-tor-common/pull/83
[85]: https://github.com/05nelsonm/kmp-tor-common/pull/85
[87]: https://github.com/05nelsonm/kmp-tor-common/pull/87
[97]: https://github.com/05nelsonm/kmp-tor-common/pull/97
[99]: https://github.com/05nelsonm/kmp-tor-common/pull/99
[100]: https://github.com/05nelsonm/kmp-tor-common/pull/100
[102]: https://github.com/05nelsonm/kmp-tor-common/pull/102
[105]: https://github.com/05nelsonm/kmp-tor-common/pull/105
[106]: https://github.com/05nelsonm/kmp-tor-common/pull/106
[108]: https://github.com/05nelsonm/kmp-tor-common/pull/108
[110]: https://github.com/05nelsonm/kmp-tor-common/pull/110
[111]: https://github.com/05nelsonm/kmp-tor-common/pull/111
[112]: https://github.com/05nelsonm/kmp-tor-common/pull/112
[114]: https://github.com/05nelsonm/kmp-tor-common/pull/114
[115]: https://github.com/05nelsonm/kmp-tor-common/pull/115
[116]: https://github.com/05nelsonm/kmp-tor-common/pull/116
