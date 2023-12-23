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
package io.matthewnelson.kmp.tor.core.resource

import io.matthewnelson.kmp.tor.core.api.annotation.InternalKmpTorApi
import kotlin.jvm.JvmField

/**
 * All supported Operating Systems for jvm/node-js
 *
 * [path] is directly correlated to the resource
 * path in which resources will be located.
 * */
@InternalKmpTorApi
public sealed class OSHost private constructor(
    @JvmField
    public val path: String,
) {

    @InternalKmpTorApi
    public object FreeBSD: OSHost("freebsd")

    @InternalKmpTorApi
    public sealed class Linux private constructor(
        subtype: String,
    ): OSHost("linux-$subtype") {

        @InternalKmpTorApi
        public object Android: Linux("android")
        @InternalKmpTorApi
        public object Libc: Linux("libc")
        @InternalKmpTorApi
        public object Musl: Linux("musl")

    }

    @InternalKmpTorApi
    public object MacOS: OSHost("macos")
    @InternalKmpTorApi
    public object Windows: OSHost("mingw")

    @InternalKmpTorApi
    public class Unknown(
        @JvmField
        public val name: String
    ): OSHost("") {
        override fun equals(other: Any?): Boolean = other is Unknown && other.name == name
        override fun hashCode(): Int = 16 * 31 + name.hashCode()
        override fun toString(): String = name
    }

    override fun equals(other: Any?): Boolean = other is OSHost && other.path == path
    override fun hashCode(): Int = 17 * 31 + path.hashCode()
    override fun toString(): String = path
}
