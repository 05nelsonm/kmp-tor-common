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
plugins {
    id("configuration")
}

repositories { google() }

kmpConfiguration {
    configure {
        options {
            useUniqueModuleNames = true
        }

        androidLibrary(namespace = "io.matthewnelson.kmp.tor.common.lib.locator") {
            target { publishLibraryVariants("release") }

            android {
                lint {
                    // Linter does not like the subclass. Tests are performed
                    // via core-test to ensure everything is copacetic.
                    disable.add("EnsureInitializerMetadata")
                }
            }

            sourceSetMain {
                dependencies {
                    implementation(libs.androidx.startup.runtime)
                }
            }
        }

        common { pluginIds("publication") }

        kotlin { explicitApi() }
    }
}
