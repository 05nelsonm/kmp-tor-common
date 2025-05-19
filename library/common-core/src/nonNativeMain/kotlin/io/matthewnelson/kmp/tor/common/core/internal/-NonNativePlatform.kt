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
package io.matthewnelson.kmp.tor.common.core.internal

import io.matthewnelson.immutable.collections.immutableMapOf
import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import io.matthewnelson.kmp.tor.common.core.OSArch

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

        Pair("aarch64", OSArch.Aarch64),
        Pair("arm64", OSArch.Aarch64),

        Pair("riscv64", OSArch.Riscv64),
    )
}
