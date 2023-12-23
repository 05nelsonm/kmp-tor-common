/*
 * Copyright (c) 2021 Matthew Nelson
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

kmpConfiguration {
    configure {
        androidLibrary {
            android {
                buildToolsVersion = "33.0.2"
                compileSdk = 33
                namespace = "io.matthewnelson.kmp.tor.core.test"

                defaultConfig {
                    minSdk = 17

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    testInstrumentationRunnerArguments["disableAnalytics"] = true.toString()
                }
            }

            sourceSetMain {
                dependencies {
                    implementation(project(":library:core-resource"))
                }
            }

            sourceSetTestInstrumented {
                dependencies {
                    implementation(libs.androidx.test.runner)
                }
            }

            kotlinJvmTarget = JavaVersion.VERSION_1_8
            compileSourceCompatibility = JavaVersion.VERSION_1_8
            compileTargetCompatibility = JavaVersion.VERSION_1_8
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
