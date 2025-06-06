/*
 * Copyright (c) 2023 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
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
        androidLibrary(namespace = "io.matthewnelson.kmp.tor.common.test") {
            android {
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
            }

            sourceSetMain {
                dependencies {
                    implementation(project(":library:common-core"))
                }
            }

            sourceSetTestInstrumented {
                dependencies {
                    implementation(libs.androidx.test.runner)
                }
            }
        }

        common {
            sourceSetTest {
                dependencies {
                    implementation(kotlin("test"))
                }
            }
        }
    }
}
