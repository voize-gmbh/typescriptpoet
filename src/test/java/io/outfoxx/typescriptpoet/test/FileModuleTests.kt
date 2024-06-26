/*
 * Copyright 2017 Outfox, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.outfoxx.typescriptpoet.test

import io.outfoxx.typescriptpoet.FileModules.importPath
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.nio.file.Paths

@DisplayName("FileModule Tests")
class FileModuleTests {

  @Test
  @DisplayName("Generates correct import path for relative paths")
  fun testRelativeImportPathGeneration() {
    val import = "!generated/src/main/api/Api"
    val importer = "generated/src/main/impl/Impl"

    val path = importPath(Paths.get("/"), importer, import)
    assertThat(path, equalTo("../api/Api"))
  }

  @Test
  @DisplayName("Generates correct import path for relative paths referencing the same dir")
  fun testRelativeImportPathGenerationSameDir() {
    val import = "!generated/src/main/api/Api"
    val importer = "generated/src/main/api/Api2"

    val path = importPath(Paths.get("/"), importer, import)
    assertThat(path, equalTo("./Api"))
  }

  @Test
  @DisplayName("Generates correct import path for sibling paths with no parent")
  fun testRelativeImportPathGenerationSiblingsNoParent() {
    val import = "!Api"
    val importer = "Api2"

    val path = importPath(Paths.get("/"), importer, import)
    assertThat(path, equalTo("./Api"))
  }

  @Test
  @DisplayName("Generates correct import path for implied modules")
  fun testImpliedImportPathGeneration() {
    val import = "rxjs/Observable"
    val importer = "generated/src/main/impl/Impl"

    val path = importPath(Paths.get("/"), importer, import)
    assertThat(path, equalTo("rxjs/Observable"))
  }
}
