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
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

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

        js {
            target {
                browser {
                    testTask {
                        isEnabled = false
                    }
                }
                nodejs {
                    testTask {
                        useMocha { timeout = "30s" }
                    }
                }
            }
        }

        @OptIn(ExperimentalWasmDsl::class)
        wasmJs {
            target {
                browser {
                    testTask {
                        isEnabled = false
                    }
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
                    main to getByName(name + "Test")
                }
                if (sets.isEmpty()) return@kotlin

                val main = maybeCreate("jsWasmJsMain").apply { dependsOn(getByName("nonJvmMain")) }
                val test = maybeCreate("jsWasmJsTest").apply { dependsOn(getByName("nonJvmTest")) }
                sets.forEach { (m, t) -> m.dependsOn(main); t.dependsOn(test) }
            }
        }

        if (publish) kotlin {
            @Suppress("DEPRECATION")
            compilerOptions {
                freeCompilerArgs.add("-Xsuppress-version-warnings")
                // kmp-file uses 1.9
                apiVersion.set(KotlinVersion.KOTLIN_1_9)
                languageVersion.set(KotlinVersion.KOTLIN_1_9)
            }
            sourceSets.configureEach {
                if (name.startsWith("js") || name.startsWith("wasm")) {
                    languageSettings {
                        // kmp-file uses 2.0 for js/wasmJs/jsWasmJs source sets
                        apiVersion = KotlinVersion.KOTLIN_2_0.version
                        languageVersion = KotlinVersion.KOTLIN_2_0.version
                    }
                }
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
