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
import io.matthewnelson.kmp.tor.common.core.internal.node.nodeOptionsReadDir
import io.matthewnelson.kmp.tor.common.core.internal.node.node_fs
import io.matthewnelson.kmp.tor.common.core.internal.node.node_os
import io.matthewnelson.kmp.tor.common.core.internal.node.platformReadDirSync

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
            machineName: String? = try { node_os.machine() } catch (_: UnsupportedOperationException) { null },
            hostName: String? = try { node_os.platform() } catch (_: UnsupportedOperationException) { null },
            archName: String? = try { node_os.arch() } catch (_: UnsupportedOperationException) { null },
        ): OSInfo = OSInfo(
            pathMapFiles = pathMapFiles,
            pathOSRelease = pathOSRelease,
            machineName = machineName,
            hostName = hostName,
            archName = archName,
        )
    }

    public actual val osHost: OSHost by lazy {
        val hostNameLC = hostName
            ?.ifBlank { null }
            ?.lowercase()
            ?: return@lazy OSHost.Unknown("unknown")

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
        val archNameLC = archName
            ?.ifBlank { null }
            ?.lowercase()
            ?: return@lazy OSArch.Unsupported("unknown")

        ARCH_MAP[archNameLC]?.let { return@lazy it }

        resolveMachineArchOrNull()?.let { return@lazy it }

        when (archNameLC) {
            "aarch64", "arm64" -> OSArch.Aarch64
            else -> OSArch.Unsupported(archNameLC)
        }
    }

    private fun hasLibAndroid(): Boolean {
        listOf("lib", "lib64").forEach { dir ->
            try {
                if ("/system/$dir/libandroid.so".toFile().exists2()) return true
            } catch (_: Throwable) {}
        }
        return false
    }

    private fun isLinuxMusl(): Boolean {
        var fileCount = 0

        try {
            if (pathMapFiles.exists2()) {
                val options = nodeOptionsReadDir(encoding = "utf8", withFileTypes = false, recursive = false)
                node_fs.platformReadDirSync(pathMapFiles.path, options)?.forEach { entry ->
                    if (entry.isNullOrBlank()) return@forEach

                    fileCount++
                    val canonicalPath = pathMapFiles.resolve(entry).canonicalPath2()
                    if (canonicalPath.contains("musl")) return true
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

    private fun resolveMachineArchOrNull(): OSArch? {
        val machineHWName = try {
            machineName?.lowercase()
        } catch (_: Throwable) {
            null
        }?.ifBlank { null } ?: return null

        if (
            machineHWName.startsWith("aarch64")
            || machineHWName.startsWith("arm64")
        ) {
            return when (osHost) {
                is OSHost.Linux.Android -> OSArch.Aarch64
                is OSHost.Linux -> {
                    // TODO: Check for 32-bit
                    OSArch.Aarch64
                }
                else -> OSArch.Aarch64
            }
        }

        ARCH_MAP[machineHWName]?.let { return it }

        // Only other supported architecture for Android is Armv7
        if (osHost is OSHost.Linux.Android) return OSArch.Armv7

        if (machineHWName.startsWith("armv7")) return OSArch.Armv7

        return null
    }
}
