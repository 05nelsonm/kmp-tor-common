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
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

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
            java9ModuleInfoName = java9ModuleName
        }

        @Suppress("RedundantSamConstructor")
        js {
            target {
                browser {
                    testTask(Action {
                        isEnabled = false
                    })
                }
                nodejs {
                    testTask(Action {
                        useMocha { timeout = "30s" }
                    })
                }
            }
        }

        @OptIn(ExperimentalWasmDsl::class)
        @Suppress("RedundantSamConstructor")
        wasmJs {
            target {
                browser {
                    testTask(Action {
                        isEnabled = false
                    })
                }
                nodejs()
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

        kotlin {
            with(sourceSets) {
                val sets = arrayOf("js", "wasmJs").mapNotNull { name ->
                    val main = findByName(name + "Main") ?: return@mapNotNull null
                    val test = getByName(name + "Test")
                    main to test
                }
                if (sets.isEmpty()) return@kotlin

                val main = maybeCreate("jsWasmJsMain")
                val test = maybeCreate("jsWasmJsTest")
                main.dependsOn(getByName("nonJvmMain"))
                test.dependsOn(getByName("nonJvmTest"))

                sets.forEach { (m, t) -> m.dependsOn(main); t.dependsOn(test) }
            }
        }

        action.execute(this)
    }
}

fun KmpConfigurationContainerDsl.androidLibrary(
    namespace: String,
    buildTools: String? = "35.0.1",
    compileSdk: Int = 35,
    minSdk: Int = 15,
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

        action?.execute(this)
    }
}
