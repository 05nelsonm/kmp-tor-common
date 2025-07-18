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

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.matthewnelson.kmp.tor.common.core

import io.matthewnelson.kmp.file.ANDROID
import io.matthewnelson.kmp.file.File
import io.matthewnelson.kmp.file.canonicalPath2
import io.matthewnelson.kmp.file.exists2
import io.matthewnelson.kmp.file.toFile
import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import io.matthewnelson.kmp.tor.common.core.internal.ARCH_MAP
import io.matthewnelson.kmp.tor.common.core.internal.PATH_MAP_FILES
import io.matthewnelson.kmp.tor.common.core.internal.PATH_OS_RELEASE
import io.matthewnelson.kmp.tor.common.core.internal.ProcessRunner
import io.matthewnelson.kmp.tor.common.core.internal.ProcessRunner.Companion.forciblyDestroy
import io.matthewnelson.kmp.tor.common.core.internal.ProcessRunner.Companion.runAndWait
import io.matthewnelson.kmp.tor.common.core.internal.ProcessRunner.Companion.waitFor
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

/**
 * Implementation based off of:
 *
 * [sqlite-jdbc](https://github.com/xerial/sqlite-jdbc/blob/master/src/main/java/org/sqlite/util/OSInfo.java)
 * */
@InternalKmpTorApi
public actual class OSInfo private constructor(
    private val process: ProcessRunner,
    private val pathMapFiles: File,
    private val pathOSRelease: File,
    hostName: String?,
    archName: String?,
) {

    public actual companion object {

        @JvmField
        public actual val INSTANCE: OSInfo = get()

        @JvmSynthetic
        internal fun get(
            process: ProcessRunner = ProcessRunner.Default,
            pathMapFiles: File = PATH_MAP_FILES.toFile(),
            pathOSRelease: File = PATH_OS_RELEASE.toFile(),
            hostName: String? = System.getProperty("os.name"),
            archName: String? = System.getProperty("os.arch"),
        ): OSInfo = OSInfo(
            process = process,
            pathMapFiles = pathMapFiles,
            pathOSRelease = pathOSRelease,
            hostName = hostName,
            archName = archName,
        )
    }

    @get:JvmName("osHost")
    public actual val osHost: OSHost by lazy {
        val hostName = hostName?.ifBlank { null } ?: "unknown"
        val hostNameLC = hostName.lowercase(Locale.US)

        when {
            hostNameLC.contains("windows") -> OSHost.Windows
            hostNameLC.contains("mac") -> OSHost.MacOS
            hostNameLC.contains("darwin") -> OSHost.MacOS
            hostNameLC.contains("freebsd") -> OSHost.FreeBSD
            hostNameLC.contains("linux") -> when {
                ANDROID.SDK_INT != null -> OSHost.Linux.Android
                hasLibAndroid() -> OSHost.Linux.Android
                isAndroidTermux() -> OSHost.Linux.Android
                isLinuxMusl() -> OSHost.Linux.Musl
                else -> OSHost.Linux.Libc
            }
            else -> OSHost.Unknown(hostName.replace("\\W", "").lowercase(Locale.US))
        }
    }

    @get:JvmName("osArch")
    public actual val osArch: OSArch by lazy {
        val archName = archName?.ifBlank { null } ?: "unknown"
        val archNameLC = archName.lowercase(Locale.US)

        ARCH_MAP[archNameLC]?.let { return@lazy it }

        if (archNameLC.startsWith("arm")) {
            resolveArmArchTypeOrNull()?.let { return@lazy it }
        }

        OSArch.Unsupported(archName.replace("\\W", "").lowercase(Locale.US))
    }

    private fun isAndroidTermux(): Boolean = try {
        process.runAndWait(listOf("uname", "-o"))
            .contains("android", ignoreCase = true)
    } catch (_: Throwable) {
        false
    }

    private fun hasLibAndroid(): Boolean = try {
        "/system/lib/libandroid.so".toFile().exists2()
        || "/system/lib64/libandroid.so".toFile().exists2()
    } catch (_: SecurityException) {
        false
    }

    private fun isLinuxMusl(): Boolean {
        var fileCount = -1

        try {
            if (pathMapFiles.exists2()) {
                pathMapFiles
                    .walkTopDown()
                    .maxDepth(1)
                    .iterator()
                    .forEach { file ->

                        // first file is always "map_files"
                        fileCount++

                        // map_files directory contains symbolic links that must
                        // be resolved which canonicalPath will do for us.
                        val canonicalPath = file.canonicalPath2()

                        if (canonicalPath.contains("musl")) {
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
                pathOSRelease.inputStream().bufferedReader().use { reader ->
                    while (true) {
                        val line = reader.readLine() ?: break

                        // ID and ID_LIKE arguments
                        if (
                            line.startsWith("ID")
                            && line.contains("alpine", ignoreCase = true)
                        ) {
                            return true
                        }
                    }
                }
            } catch (_: Throwable) {
                // EOF or does not exist
                return false
            }
        }

        return false
    }

    private fun resolveArmArchTypeOrNull(): OSArch? {
        when (osHost) {
            is OSHost.Windows,
            is OSHost.Unknown -> return null
            else -> { /* run */ }
        }

        // aarch64, armv5t, armv5te, armv5tej, armv5tejl, armv6, armv7, armv7l
        val machineHardwareName = try {
            process.runAndWait(listOf("uname", "-m")).lowercase()
        } catch (_: Throwable) {
            return null
        }

        // Should never be the case because it's in archMap which
        // is always checked before calling this function.
        if (
            machineHardwareName.startsWith("aarch64")
            || machineHardwareName.startsWith("arm64")
        ) {
            return OSArch.Aarch64
        }

        if (osHost is OSHost.MacOS) {
            return null
        }

        // If android and NOT aarch64, return the only other
        // supported arm architecture.
        if (osHost is OSHost.Linux.Android) {
            return OSArch.Armv7
        }

        if (machineHardwareName.startsWith("armv7")) {
            return OSArch.Armv7
        }

        // Java 1.8 introduces a system property to determine armel or armhf
        // http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8005545
        System.getProperty("sun.arch.abi")?.let { abi ->
            if (abi.startsWith("gnueabihf")) {
                return OSArch.Armv7
            }
        }

        // For java7, still need to run some shell commands to determine ABI of JVM
        val javaHome = System.getProperty("java.home")?.ifBlank { null } ?: return null

        val processes = ArrayList<Process>(1)

        // determine if first JVM found uses ARM hard-float ABI
        try {
            Runtime.getRuntime().exec(arrayOf("which", "readelf")).let { process ->
                // If it did not finish before timeout
                if (!process.waitFor(250.milliseconds)) return null
                processes.add(process)
                if (process.exitValue() != 0) return null
            }

            val cmdArray = arrayOf(
                "/bin/sh",
                "-c",
                "find '"
                        + javaHome
                        + "' -name 'libjvm.so' | head -1 | xargs readelf -A | "
                        + "grep 'Tag_ABI_VFP_args: VFP registers'"
            )

            Runtime.getRuntime().exec(cmdArray).let { process ->
                // If it did not finish before timeout
                if (!process.waitFor(250.milliseconds)) return null
                processes.add(process)
                if (process.exitValue() == 0) return OSArch.Armv7
            }
        } catch (_: Throwable) {}
        finally {
            processes.forEach { p -> p.forciblyDestroy() }
        }

        // Unsupported
        return null
    }

    @Deprecated(
        message = "Ambiguity in function name.",
        replaceWith = ReplaceWith(
            "ANDROID.SDK_INT != null",
            "io.matthewnelson.kmp.file.ANDROID",
        ),
    )
    public fun isAndroidRuntime(): Boolean = ANDROID.SDK_INT != null
}
