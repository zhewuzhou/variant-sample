/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.build.gradle.internal.cxx.settings

import com.android.build.gradle.internal.core.Abi
import com.android.build.gradle.internal.cxx.model.BasicCmakeMock
import com.android.build.gradle.internal.cxx.model.createCxxAbiModel
import com.android.build.gradle.internal.cxx.model.createCxxVariantModel
import com.android.build.gradle.internal.cxx.model.tryCreateCxxModuleModel
import com.android.testutils.GoldenFile
import org.junit.Test

/**
 * Print CMakeSettings.json macros along with description and examples.
 *
 * The expected result file can be updated by running [CMakeSettingsJsonPropertiesGoldenFileUpdater.main]
 */
class CMakeSettingsJsonPropertiesGoldenFileTest {

    @Test
    fun validate() {
        goldenFile.assertUpToDate(updater = CMakeSettingsJsonPropertiesGoldenFileUpdater::class.java)
    }

    companion object {
        internal val goldenFile = GoldenFile(
            resourceRootWorkspacePath = "tools/base/build-system/gradle-core/src/test/resources",
            resourcePath = "com/android/build/gradle/internal/cxx/settings/CMakeSettingsJsonHostProperties.md",
            actualCallable = {
                val result = mutableListOf<String>()
                result += "This file generated by ${CMakeSettingsJsonPropertiesGoldenFileUpdater::class.java}"
                result += ""
                BasicCmakeMock().let {
                    // Walk all vals in the model and invoke them
                    val module = tryCreateCxxModuleModel(
                        it.global, it.cmakeFinder
                    )!!
                    val variant = createCxxVariantModel(
                        module,
                        it.variantScope
                    )
                    val abi = createCxxAbiModel(
                        variant,
                        Abi.X86_64,
                        it.global,
                        it.baseVariantData
                    )
                    Macro.values()
                        .toList()
                        .sortedBy { it.qualifiedName }
                        .forEach { macro ->
                            result += "## " + macro.ref
                            result += macro.description
                            result += "- example: ${macro.example.replace('\\', '/').replace(".exe", "")}"
                            result += "- environment: ${macro.environment.environment}"
                            result += ""
                    }
                }
                result
            })
    }
}
