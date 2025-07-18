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
package io.matthewnelson.kmp.tor.common.api

import io.matthewnelson.kmp.file.File
import io.matthewnelson.kmp.file.IOException
import io.matthewnelson.kmp.file.absoluteFile2
import io.matthewnelson.kmp.file.normalize
import kotlin.jvm.JvmField

/**
 * Holder for tor's geoip and geoip6 resource files.
 *
 * @throws [IOException] If [absoluteFile2] has to reference the filesystem to construct
 *   an absolute path and fails due to a filesystem security exception.
 * @throws [UnsupportedOperationException] On Kotlin/JS-Browser if [absoluteFile2]
 *   references the filesystem to construct an absolute path.
 * */
public class GeoipFiles(geoip: File, geoip6: File) {

    @JvmField
    public val geoip: File = geoip.absoluteFile2().normalize()
    @JvmField
    public val geoip6: File = geoip6.absoluteFile2().normalize()

    /** @suppress */
    public override fun equals(other: Any?): Boolean {
        return  other is GeoipFiles
                && other.geoip == geoip
                && other.geoip6 == geoip6
    }

    /** @suppress */
    public override fun hashCode(): Int {
        var result = 17
        result = result * 42 + geoip.hashCode()
        result = result * 42 + geoip6.hashCode()
        return result
    }

    /** @suppress */
    public override fun toString(): String = buildString {
        appendLine("GeoipFiles: [")
        append("    geoip: ")
        appendLine(geoip)
        append("    geoip6: ")
        appendLine(geoip6)
        append(']')
    }
}
