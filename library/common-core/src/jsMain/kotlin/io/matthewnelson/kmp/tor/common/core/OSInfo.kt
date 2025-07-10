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
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")

package io.matthewnelson.kmp.tor.common.core

import io.matthewnelson.kmp.file.File
import io.matthewnelson.kmp.file.canonicalPath2
import io.matthewnelson.kmp.file.exists2
import io.matthewnelson.kmp.file.path
import io.matthewnelson.kmp.file.readUtf8
import io.matthewnelson.kmp.file.resolve
import io.matthewnelson.kmp.file.toFile
import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import io.matthewnelson.kmp.tor.common.core.internal.ARCH_MAP
import io.matthewnelson.kmp.tor.common.core.internal.PATH_MAP_FILES
import io.matthewnelson.kmp.tor.common.core.internal.PATH_OS_RELEASE
import io.matthewnelson.kmp.tor.common.core.internal.fs_readdirSync
import io.matthewnelson.kmp.tor.common.core.internal.os_arch
import io.matthewnelson.kmp.tor.common.core.internal.os_machine
import io.matthewnelson.kmp.tor.common.core.internal.os_platform

@InternalKmpTorApi
public actual class OSInfo private constructor(
    private val pathMapFiles: File,
    private val pathOSRelease: File,
    private val machineName: String?,
    hostName: String?,
    archName: String?,
) {

    public actual companion object {

        public actual val INSTANCE: OSInfo = get()

        internal fun get(
            pathMapFiles: File = PATH_MAP_FILES.toFile(),
            pathOSRelease: File = PATH_OS_RELEASE.toFile(),
            machineName: String? = os_machine(),
            hostName: String? = os_platform(),
            archName: String? = os_arch(),
        ): OSInfo = OSInfo(
            pathMapFiles = pathMapFiles,
            pathOSRelease = pathOSRelease,
            machineName = machineName,
            hostName = hostName,
            archName = archName,
        )
    }

    public actual val osHost: OSHost by lazy {
        val hostNameLC = (hostName?.ifBlank { null } ?: "unknown").lowercase()

        when (hostNameLC) {
            "win32" -> OSHost.Windows
            "darwin" -> OSHost.MacOS
            "freebsd" -> OSHost.FreeBSD
            "android" -> OSHost.Linux.Android
            "linux" -> when {
                hasLibAndroid() -> OSHost.Linux.Android
                isLinuxMusl() -> OSHost.Linux.Musl
                else -> OSHost.Linux.Libc
            }
            else -> OSHost.Unknown(hostNameLC)
        }
    }

    public actual val osArch: OSArch by lazy {
        val archNameLC = (archName?.ifBlank { null } ?: "unknown").lowercase()

        ARCH_MAP[archNameLC]?.let { return@lazy it }

        resolveMachineArch()?.let { return@lazy it }

        OSArch.Unsupported(archNameLC)
    }

    private fun hasLibAndroid(): Boolean = try {
        "/system/lib/libandroid.so".toFile().exists2()
        || "/system/lib64/libandroid.so".toFile().exists2()
    } catch (_: Throwable) {
        false
    }

    private fun isLinuxMusl(): Boolean {
        var fileCount = 0

        try {
            if (pathMapFiles.exists2()) {
                val opts = js("{}")
                opts["encoding"] = "utf8"
                opts["withFileTypes"] = false
                opts["recursive"] = false

                fs_readdirSync(pathMapFiles.path, opts).forEach { entry ->
                    fileCount++

                    val canonical = pathMapFiles.resolve(entry).canonicalPath2()

                    if (canonical.contains("musl")) {
                        return true
                    }
                }
            }
        } catch (_: Throwable) {
            fileCount = 0
        }

        if (fileCount < 1) {
            // Fallback to checking for Alpine Linux in the event
            // it's an older kernel which may not have map_files
            // directory.
            try {
                pathOSRelease.readUtf8().lines().forEach { line ->
                    if (
                        line.startsWith("ID")
                        && line.contains("alpine", ignoreCase = true)
                    ) {
                        return true
                    }
                }
            } catch (_: Throwable) {}
        }

        return false
    }

    private fun resolveMachineArch(): OSArch? {
        val machineHardwareName = try {
            machineName?.lowercase() ?: return null
        } catch (_: Throwable) {
            return null
        }

        // Should resolve any possible x86/x86_64 values
        ARCH_MAP[machineHardwareName]?.let { return it }

        // Should never be the case because it's in archMap which
        // is always checked before calling this function.
        if (
            machineHardwareName.startsWith("aarch64")
            || machineHardwareName.startsWith("arm64")
        ) {
            return OSArch.Aarch64
        }

        // If android and NOT aarch64, return the only other
        // supported arm architecture.
        if (
            machineHardwareName.startsWith("arm")
            && osHost is OSHost.Linux.Android
        ) {
            return OSArch.Armv7
        }

        if (machineHardwareName.startsWith("armv7")) {
            return OSArch.Armv7
        }

        // Unsupported
        return null
    }
}
