/*
 * Copyright (c) 2023 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package io.matthewnelson.kmp.tor.common.core

import io.matthewnelson.kmp.file.path
import io.matthewnelson.kmp.file.resolve
import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(InternalKmpTorApi::class)
class OSInfoJsUnitTest: OSInfoBaseTest() {

    @Test
    fun givenOSNameWindows_whenOSHost_thenIsWindows() {
        assertTrue(OSInfo.get(hostName = "win32").osHost is OSHost.Windows)
    }

    @Test
    fun givenOSNameDarwin_whenOSHost_thenIsMacOS() {
        assertTrue(OSInfo.get(hostName = "darwin").osHost is OSHost.MacOS)
    }

    @Test
    fun givenOSNameFreeBSD_whenOSHost_thenIsFreeBSD() {
        assertTrue(OSInfo.get(hostName = "freebsd").osHost is OSHost.FreeBSD)
    }

    @Test
    fun givenOSNameAndroid_whenOSHost_thenIsAndroid() {
        assertTrue(OSInfo.get(hostName = "android").osHost is OSHost.Linux.Android)
    }

    @Test
    fun givenOSNameLinux_whenOSHost_thenIsLinuxLibc() {
        // Linux tests cannot be run on windows host machine
        // because symbolic links are not a thing.
        when (OSInfo.INSTANCE.osHost) {
            is OSHost.Unknown,
            is OSHost.Windows -> {
                println("Skipping...")
                return
            }
            else -> { /* run */ }
        }

        OSInfo.get(
            pathMapFiles = TEST_MAP_FILES_NOT_MUSL.path,
            pathOSRelease = TEST_OS_RELEASE_NOT_MUSL.path,
            hostName = "linux",
        ).let { osInfo ->
            assertTrue(osInfo.osHost is OSHost.Linux.Libc)
        }
    }

    @Test
    fun givenOSNameLinux_whenMapFilesMusl_thenIsLinuxMusl() {
        // Linux tests cannot be run on windows host machine
        // because symbolic links are not a thing.
        when (OSInfo.INSTANCE.osHost) {
            is OSHost.Unknown,
            is OSHost.Windows -> {
                println("Skipping...")
                return
            }
            else -> { /* run */ }
        }

        // Linux-Musl WITH map_files directory
        OSInfo.get(
            pathMapFiles = TEST_SUPPORT_DIR
                .resolve("msl")
                .resolve("map_files").path,
            pathOSRelease = TEST_OS_RELEASE_NOT_MUSL.path,
            hostName = "linux",
        ).let { osInfo ->
            assertTrue(osInfo.osHost is OSHost.Linux.Musl)
        }
    }

    @Test
    fun givenOSNameLinux_whenMapFilesNoExistAndOSReleaseAlpine_thenIsLinuxMusl() {
        // Linux-Musl w/o map_files directory
        // Will check os-release to see if it's alpine
        OSInfo.get(
            pathMapFiles = TEST_SUPPORT_DIR
                .resolve("msl")
                .resolve("does_not_exist").path,
            pathOSRelease = TEST_SUPPORT_DIR
                .resolve("msl")
                .resolve("os-release").path,
            hostName = "linux",
        ).let { osInfo ->
            assertTrue(osInfo.osHost is OSHost.Linux.Musl)
        }
    }
}
