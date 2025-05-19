/*
 * Copyright (c) 2025 Matthew Nelson
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

kmpConfiguration {
    configure {
        jvm {
            kotlinJvmTarget = JavaVersion.VERSION_1_8
            compileSourceCompatibility = JavaVersion.VERSION_1_8
            compileTargetCompatibility = JavaVersion.VERSION_1_8
        }

        common {
            sourceSetMain {
                dependencies {
                    implementation(project(":library:common-core"))
                }
            }
            sourceSetTest {
                dependencies {
                    implementation(kotlin("test"))
                }
            }
        }

        kotlin {
            explicitApi()
            jvmToolchain(8)

            val configuration = targets
                .getByName("jvm")
                .compilations
                .getByName("main")
                .runtimeDependencyConfigurationName
                ?.let { project.configurations.getByName(it) }
                ?: return@kotlin

            project.tasks.register<Jar>("assembleFatJar") {
                manifest.attributes["Main-Class"] = "io.matthewnelson.kmp.tor.common.test.info.MainKt"
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE

                archiveFileName.set("test-info.jar")
                from(configuration.map { if (it.isDirectory) it else zipTree(it) })
                with(project.tasks.getByName("jvmJar") as CopySpec)
                dependsOn(":library:common-core:jvmJar")
            }
        }
    }
}
