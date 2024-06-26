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

import io.outfoxx.typescriptpoet.CodeWriter
import io.outfoxx.typescriptpoet.EnumSpec
import io.outfoxx.typescriptpoet.Modifier
import io.outfoxx.typescriptpoet.tag
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.StringWriter

@DisplayName("EnumSpec Tests")
class EnumSpecTests {

  @Test
  @DisplayName("Tags on builders can be retrieved on builders and built specs")
  fun testTags() {
    val testBuilder = EnumSpec.builder("Test")
      .tag(5)
    val testSpec = testBuilder.build()

    assertThat(testBuilder.tags[Integer::class] as? Int, equalTo(5))
    assertThat(testSpec.tag(), equalTo(5))
  }

  @Test
  @DisplayName("Generates TSDoc at before class definition")
  fun testGenTSDoc() {
    val testClass = EnumSpec.builder("Test")
      .addTSDoc("this is a comment\n")
      .build()

    val out = StringWriter()
    testClass.emit(CodeWriter(out))

    assertThat(
      out.toString(),
      equalTo(
        """
            /**
             * this is a comment
             */
            enum Test {
            }

        """.trimIndent(),
      ),
    )
  }

  @Test
  @DisplayName("Generates modifiers in order")
  fun testGenModifiersInOrder() {
    val testClass = EnumSpec.builder("Test")
      .addModifiers(Modifier.EXPORT)
      .build()

    val out = StringWriter()
    testClass.emit(CodeWriter(out))

    assertThat(
      out.toString(),
      equalTo(
        """
            export enum Test {
            }

        """.trimIndent(),
      ),
    )
  }

  @Test
  @DisplayName("Generates formatted constants")
  fun testGenConstants() {
    val testClass = EnumSpec.builder("Test")
      .addConstant("A", "10")
      .addConstant("B", "20")
      .addConstant("C", "30")
      .build()

    val out = StringWriter()
    testClass.emit(CodeWriter(out))

    assertThat(
      out.toString(),
      equalTo(
        """
            enum Test {
              A = 10,
              B = 20,
              C = 30
            }

        """.trimIndent(),
      ),
    )
  }

  @Test
  @DisplayName("toBuilder copies all fields")
  fun testToBuilder() {
    val testEnumBldr = EnumSpec.builder("Test")
      .addTSDoc("this is a comment\n")
      .addModifiers(Modifier.EXPORT)
      .addConstant("A", "10")
      .build()
      .toBuilder()

    assertThat(testEnumBldr.name, equalTo("Test"))
    assertThat(testEnumBldr.tsDoc.formatParts, hasItems("this is a comment\n"))
    assertThat(testEnumBldr.modifiers, hasItems(Modifier.EXPORT))
    assertThat(testEnumBldr.constants.keys, hasItems("A"))
  }
}
