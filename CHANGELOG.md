# CHANGELOG

## Version 2.0.0-alpha09 (2024-03-04)
 - Updates `kmp-file` to `0.1.0-beta02` [[#36]][36]
 - Use flag `O_CLOEXEC` when opening `gzip` file on `UNIX` [[#35]][35]

## Version 2.0.0-alpha08 (2024-02-25)
 - Updates `kotlin` to `1.9.22` [[#30]][30]
 - Updates `atomicfu` to `0.23.2` [[#30]][30]
 - Updates `kmp-file` to `0.1.0-beta01` [[#30]][30]
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

[9]: https://github.com/05nelsonm/kmp-tor-core/pull/9
[13]: https://github.com/05nelsonm/kmp-tor-core/pull/13
[14]: https://github.com/05nelsonm/kmp-tor-core/pull/14
[16]: https://github.com/05nelsonm/kmp-tor-core/pull/16
[17]: https://github.com/05nelsonm/kmp-tor-core/pull/17
[23]: https://github.com/05nelsonm/kmp-tor-core/pull/23
[24]: https://github.com/05nelsonm/kmp-tor-core/pull/24
[26]: https://github.com/05nelsonm/kmp-tor-core/pull/26
[28]: https://github.com/05nelsonm/kmp-tor-core/pull/28
[30]: https://github.com/05nelsonm/kmp-tor-core/pull/30
[31]: https://github.com/05nelsonm/kmp-tor-core/pull/31
[33]: https://github.com/05nelsonm/kmp-tor-core/pull/33
[35]: https://github.com/05nelsonm/kmp-tor-core/pull/35
[36]: https://github.com/05nelsonm/kmp-tor-core/pull/36
