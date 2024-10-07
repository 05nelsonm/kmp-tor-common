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

import io.matthewnelson.kmp.file.toFile
import kotlin.test.Test

class ResourceLoaderTorNoExecUnitTest: AbstractResourceLoaderTorUnitTest() {

    private class TestNoExec private constructor(): ResourceLoader.Tor.NoExec() {

        companion object {

            fun get(): Tor = getOrCreate(
                resourceDir = "".toFile(),
                extract = { GeoipFiles(it.resolve("geoip"), it.resolve("geoip6")) },
                load = { object : TorApi() {
                    override fun torRunMainProtected(args: Array<String>, log: Logger): Int {
                        TODO("Not yet implemented")
                    }
                } },
                toString = { "" }
            )
        }
    }

    override fun loader(): ResourceLoader.Tor = TestNoExec.get()

    @Test
    fun stub() {}
}
