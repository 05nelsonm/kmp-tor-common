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
@file:Suppress("UnusedReceiverParameter")

package io.matthewnelson.kmp.tor.common.core.internal

import io.matthewnelson.immutable.collections.immutableMapOf
import io.matthewnelson.kmp.file.exists2
import io.matthewnelson.kmp.file.readUtf8
import io.matthewnelson.kmp.file.toFile
import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import io.matthewnelson.kmp.tor.common.core.OSArch
import io.matthewnelson.kmp.tor.common.core.OSInfo
import kotlin.time.Duration.Companion.milliseconds

internal const val PATH_MAP_FILES = "/proc/self/map_files"
internal const val PATH_OS_RELEASE = "/etc/os-release"

@InternalKmpTorApi
internal val ARCH_MAP: Map<String, OSArch> by lazy {
    immutableMapOf(
        Pair("x86", OSArch.X86),
        Pair("i386", OSArch.X86),
        Pair("i486", OSArch.X86),
        Pair("i586", OSArch.X86),
        Pair("i686", OSArch.X86),
        Pair("pentium", OSArch.X86),

        Pair("x64", OSArch.X86_64),
        Pair("x86_64", OSArch.X86_64),
        Pair("amd64", OSArch.X86_64),
        Pair("em64t", OSArch.X86_64),
        Pair("universal", OSArch.X86_64),

        Pair("ppc64", OSArch.Ppc64),
        Pair("power64", OSArch.Ppc64),
        Pair("powerpc64", OSArch.Ppc64),
        Pair("power_pc64", OSArch.Ppc64),
        Pair("power_rs64", OSArch.Ppc64),
        Pair("ppc64el", OSArch.Ppc64),
        Pair("ppc64le", OSArch.Ppc64),

        Pair("riscv64", OSArch.Riscv64),
    )
}

@OptIn(InternalKmpTorApi::class)
internal fun OSInfo.hasLibAndroid(): Boolean {
    listOf("lib", "lib64").forEach { dir ->
        try {
            if ("/system/$dir/libandroid.so".toFile().exists2()) return true
        } catch (_: Throwable) {}
    }
    return false
}

@OptIn(InternalKmpTorApi::class)
internal fun OSInfo.isLinuxMusl(
    process: ProcessRunner,
    pathMapFiles: String,
    pathOSRelease: String,
): Boolean {
    try {
        if (pathMapFiles.toFile().exists2()) {
            // File.canonicalPath, or Paths.get("...").toRealPath()
            // will not resolve the links appropriately for older versions
            // of Java, but instead resolve to /proc/{pid}/map_files/{uuid}
            //
            // ls -lnr gets the job done and looks something like the following
            // ... 7ba22b02e000-7ba22b02f000 -> /lib/ld-musl-x86_64.so.1
            // ... 7ba22b02d000-7ba22b02e000 -> /lib/ld-musl-x86_64.so.1
            // ... 7ba22affb000-7ba22b02d000 -> /lib/ld-musl-x86_64.so.1
            // ... 7ba22afb6000-7ba22affb000 -> /lib/ld-musl-x86_64.so.1
            // ... 7ba22af9f000-7ba22afb6000 -> /lib/ld-musl-x86_64.so.1
            // ... 5999901d1000-5999901d2000 -> /bin/busybox
            // ... 5999901cd000-5999901d1000 -> /bin/busybox
            // ... 5999901ad000-5999901cc000 -> /bin/busybox
            // ... 59999011a000-5999901ad000 -> /bin/busybox
            // ... 59999010e000-59999011a000 -> /bin/busybox
            process
                .runAndWait(listOf("ls", "-lnr", pathMapFiles), 250.milliseconds)
                .lines()
                .forEach { line ->
                    val iLink = line.indexOf("->")
                    if (iLink == -1) return@forEach
                    val iMusl = line.indexOf("musl", iLink)
                    if (iMusl != -1) return true
                }

            return false
        }
    } catch (_: Throwable) {
        // Fallthrough to check for ID=alpine Linux in the event
        // it's an older kernel which may not have map_files dir.
    }

    try {
        pathOSRelease.toFile().readUtf8().lines().forEach { line ->
            if (!line.startsWith("ID")) return@forEach
            return line.contains("alpine", ignoreCase = true)
        }
    } catch (_: Throwable) {}

    return false
}
