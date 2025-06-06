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
@file:Suppress("ClassName")

package io.matthewnelson.kmp.tor.common.core

import io.matthewnelson.kmp.tor.common.api.InternalKmpTorApi
import kotlin.jvm.JvmField

/**
 * All supported Architectures for jvm/node-js
 *
 * [path] is directly correlated to the path in which
 * resources will be located.
 * */
@InternalKmpTorApi
public sealed class OSArch private constructor(
    @JvmField
    public val path: String
) {

    @InternalKmpTorApi
    public object Aarch64: OSArch("aarch64")
    @InternalKmpTorApi
    public object Armv7: OSArch("armv7")
    @InternalKmpTorApi
    public object Ppc64: OSArch("ppc64")
    @InternalKmpTorApi
    public object Riscv64: OSArch("riscv64")
    @InternalKmpTorApi
    public object X86: OSArch("x86")
    @InternalKmpTorApi
    public object X86_64: OSArch("x86_64")

    @InternalKmpTorApi
    public class Unsupported(
        @JvmField
        public val arch: String
    ): OSArch("") {
        public override fun equals(other: Any?): Boolean = other is Unsupported && other.arch == arch
        public override fun hashCode(): Int = 16 * 31 + arch.hashCode()
        public override fun toString(): String = arch
    }

    public override fun equals(other: Any?): Boolean = other is OSArch && other.path == path
    public override fun hashCode(): Int = 17 * 31 + path.hashCode()
    public override fun toString(): String = path
}
