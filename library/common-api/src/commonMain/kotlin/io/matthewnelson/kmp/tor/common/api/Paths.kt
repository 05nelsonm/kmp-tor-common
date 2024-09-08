/*
 * Copyright (c) 2024 Matthew Nelson
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
@file:Suppress("KotlinRedundantDiagnosticSuppress")

package io.matthewnelson.kmp.tor.common.api

import io.matthewnelson.kmp.file.File
import io.matthewnelson.kmp.file.absoluteFile
import io.matthewnelson.kmp.file.normalize
import kotlin.jvm.JvmField

public abstract class Paths private constructor() {

    public class Tor(
        @JvmField
        public val executable: File,
        @JvmField
        public val geoips: Geoips,
    ): Paths() {
        public override fun equals(other: Any?): Boolean = equalsPrivate(other)
        public override fun hashCode(): Int = hashCodePrivate()
        public override fun toString(): String = toStringPrivate()
    }

    public class Geoips(geoip: File, geoip6: File): Paths() {

        @JvmField
        public val geoip: File = geoip.absoluteFile.normalize()
        @JvmField
        public val geoip6: File = geoip6.absoluteFile.normalize()

        public override fun equals(other: Any?): Boolean = equalsPrivate(other)
        public override fun hashCode(): Int = hashCodePrivate()
        public override fun toString(): String = appendTo(null).append(']').toString()
    }
}

@Suppress("NOTHING_TO_INLINE")
private inline fun Paths.Tor.equalsPrivate(other: Any?): Boolean {
    return  other is Paths.Tor
            && other.executable == executable
            && other.geoips == geoips
}

@Suppress("NOTHING_TO_INLINE")
private inline fun Paths.Tor.hashCodePrivate(): Int {
    var result = 17
    result = result * 42 + executable.hashCode()
    result = result * 42 + geoips.hashCode()
    return result
}

@Suppress("NOTHING_TO_INLINE")
private inline fun Paths.Tor.toStringPrivate(): String {
    return StringBuilder("Paths.Tor: [").apply {
        appendLine()
        append("    executable: ")
        appendLine(executable)
        append("    geoips: [")
        geoips.appendTo(this)
        appendLine("    ]")
        append(']')
    }.toString()
}

@Suppress("NOTHING_TO_INLINE")
private inline fun Paths.Geoips.equalsPrivate(other: Any?): Boolean {
    return  other is Paths.Geoips
            && other.geoip == geoip
            && other.geoip6 == geoip6
}

@Suppress("NOTHING_TO_INLINE")
private inline fun Paths.Geoips.hashCodePrivate(): Int {
    var result = 17
    result = result * 42 + geoip.hashCode()
    result = result * 42 + geoip6.hashCode()
    return result
}

private fun Paths.Geoips.appendTo(sb: StringBuilder? = null): StringBuilder {
    val indent = if (sb == null) "    " else "        "

    return (sb ?: StringBuilder("Paths.Geoips: [")).apply {
        appendLine()
        append(indent)
        append("geoip: ")
        appendLine(geoip)
        append(indent)
        append("geoip6: ")
        appendLine(geoip6)
    }
}
