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

import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(InternalKmpTorApi::class)
class OSInfoJvmUnitTest: OSInfoBaseTest() {

    @Test
    fun givenOSNameWindows_whenOSHost_thenIsWindows() {
        // Name only based checks
        assertTrue(OSInfo.get(hostName = "Windows XP").osHost is OSHost.Windows)
        assertTrue(OSInfo.get(hostName = "Windows 2000").osHost is OSHost.Windows)
        assertTrue(OSInfo.get(hostName = "Windows Vista").osHost is OSHost.Windows)
        assertTrue(OSInfo.get(hostName = "Windows 98").osHost is OSHost.Windows)
        assertTrue(OSInfo.get(hostName = "Windows 95").osHost is OSHost.Windows)
    }

    @Test
    fun givenOSNameMac_whenOSHost_thenIsMacOS() {
        assertTrue(OSInfo.get(hostName = "Mac OS").osHost is OSHost.MacOS)
        assertTrue(OSInfo.get(hostName = "Mac OS X").osHost is OSHost.MacOS)
    }

    @Test
    fun givenOSNameFreeBSD_whenOSHost_thenIsFreeBSD() {
        assertTrue(OSInfo.get(hostName = "FreeBSD").osHost is OSHost.FreeBSD)
    }

    @Test
    fun givenOSNameLinux_whenUnameOAndroid_thenIsLinuxAndroid() {
        // Termux
        var count = 0
        OSInfo.get(
            process = { commands, _ ->
                if (commands == listOf("uname", "-o")) {
                    count++
                    "Linux Android"
                } else {
                    throw AssertionError("")
                }
            },
            hostName = "Linux",
        ).let { osInfo ->
            assertTrue(osInfo.osHost is OSHost.Linux.Android)
            // Ensure isAndroidTermux executed uname -o
            assertEquals(1, count)
        }
    }

    @Test
    fun givenOSNameLinux_whenOSName_thenIsLinuxLibc() {
        listOf("Linux", "GNU/Linux").forEach { hostName ->
            val osInfo = OSInfo.get(
                pathMapFiles = TEST_MAP_FILES_NOT_MUSL,
                pathOSRelease = TEST_OS_RELEASE_NOT_MUSL,
                hostName = hostName,
            )

            assertTrue(osInfo.osHost is OSHost.Linux.Libc)
        }
    }

    @Test
    fun givenOSInfo_whenMapFilesMusl_thenIsLinuxMusl() {
        // Linux tests cannot be run on windows host machine
        // because symbolic links are not a thing.
        when (OSInfo.INSTANCE.osHost) {
            is OSHost.Unknown,
            is OSHost.Windows -> return
            else -> { /* run */ }
        }

        // Linux-Musl WITH map_files directory
        OSInfo.get(
            pathMapFiles = TEST_SUPPORT_DIR
                .resolve("msl")
                .resolve("map_files"),
            pathOSRelease = TEST_OS_RELEASE_NOT_MUSL,
            hostName = "Linux",
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
                .resolve("does_not_exist"),
            pathOSRelease = TEST_SUPPORT_DIR
                .resolve("msl")
                .resolve("os-release"), // alpine linux
            hostName = "Linux",
        ).let { osInfo ->
            assertTrue(osInfo.osHost is OSHost.Linux.Musl)
        }
    }
}
