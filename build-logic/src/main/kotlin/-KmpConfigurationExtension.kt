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
import io.matthewnelson.kmp.configuration.extension.KmpConfigurationExtension
import io.matthewnelson.kmp.configuration.extension.container.target.KmpConfigurationContainerDsl
import io.matthewnelson.kmp.configuration.extension.container.target.TargetAndroidContainer
import org.gradle.api.Action
import org.gradle.api.JavaVersion

fun KmpConfigurationExtension.configureShared(
    java9ModuleName: String? = null,
    publish: Boolean = false,
    action: Action<KmpConfigurationContainerDsl>,
) {
    if (publish) {
        require(!java9ModuleName.isNullOrBlank()) { "publications must specify a module-info name" }
    }

    configure {
        options {
            useUniqueModuleNames = true
        }

        jvm {
            kotlinJvmTarget = JavaVersion.VERSION_1_8
            compileSourceCompatibility = JavaVersion.VERSION_1_8
            compileTargetCompatibility = JavaVersion.VERSION_1_8

            java9ModuleInfoName = java9ModuleName
        }

        js {
            target {
                nodejs {
                    @Suppress("RedundantSamConstructor")
                    testTask(Action {
                        useMocha { timeout = "30s" }
                    })
                }
            }
        }

        androidNativeAll()
        iosAll()
        linuxAll()
        macosAll()
        mingwAll()
        tvosAll()
        watchosAll()

        common {
            if (publish) pluginIds("publication")

            sourceSetTest {
                dependencies {
                    implementation(kotlin("test"))
                }
            }
        }

        if (publish) kotlin { explicitApi() }

        action.execute(this)
    }
}

fun KmpConfigurationContainerDsl.androidLibrary(
    namespace: String,
    buildTools: String? = "35.0.1",
    compileSdk: Int = 35,
    minSdk: Int = 15,
    javaVersion: JavaVersion = JavaVersion.VERSION_1_8,
    action: (Action<TargetAndroidContainer.Library>)? = null,
) {
    androidLibrary {
        android {
            buildTools?.let { buildToolsVersion = it }
            this.compileSdk = compileSdk
            this.namespace = namespace

            defaultConfig {
                this.minSdk = minSdk

                testInstrumentationRunnerArguments["disableAnalytics"] = true.toString()
            }
        }

        kotlinJvmTarget = javaVersion
        compileSourceCompatibility = javaVersion
        compileTargetCompatibility = javaVersion

        action?.execute(this)
    }
}
