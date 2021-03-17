import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `java-library`
  jacoco
  `maven-publish`
  signing

  kotlin("jvm") version "1.4.30"
  id("org.jetbrains.dokka") version "1.4.20"

  id("net.minecrell.licenser") version "0.4.1"
  id("org.jmailen.kotlinter") version "3.3.0"
}


group = "io.outfoxx"
version = "1.1.1"
description = "A Kotlin/Java API for generating .ts source files."

val isSnapshot = "$version".endsWith("SNAPSHOT")


//
// DEPENDENCIES
//

// Versions

val guavaVersion = "22.0"
val junitJupiterVersion = "5.6.2"
val hamcrestVersion = "1.3"

repositories {
  mavenCentral()
  jcenter()
}

dependencies {

  //
  // LANGUAGES
  //

  // kotlin
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlin:kotlin-reflect")

  //
  // MISCELLANEOUS
  //

  implementation("com.google.guava:guava:$guavaVersion")

  //
  // TESTING
  //

  // junit
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
  testImplementation("org.hamcrest:hamcrest-all:$hamcrestVersion")

}


//
// COMPILE
//

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8

  withSourcesJar()
  withJavadocJar()
}

tasks {
  withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = "1.8"
    }
  }
}


//
// TEST
//

jacoco {
  toolVersion = "0.8.5"
}

tasks {
  test {
    useJUnitPlatform()

    finalizedBy(jacocoTestReport)
    jacoco {}
  }

  jacocoTestReport {
    dependsOn(test)
  }
}


//
// DOCS
//

tasks {
  dokkaHtml {
    outputDirectory.set(file("$buildDir/javadoc/${project.version}"))
  }

  javadoc {
    dependsOn(dokkaHtml)
  }
}


//
// CHECKS
//

kotlinter {
  indentSize = 2
}

license {
  header = file("HEADER.txt")
  include("**/*.kt")
}


//
// PUBLISHING
//

publishing {

  publications {

    create<MavenPublication>("mavenJava") {
      from(components["java"])

      pom {

        name.set("TypeScript Poet")
        description.set("TypeScriptPoet is a Kotlin and Java API for generating .ts source files.")
        url.set("https://github.com/outfoxx/typescriptpoet")

        organization {
          name.set("Outfox, Inc.")
          url.set("https://outfoxx.io")
        }

        issueManagement {
          system.set("GitHub")
          url.set("https://github.com/outfoxx/typescriptpoet/issues")
        }

        licenses {
          license {
            name.set("Apache License 2.0")
            url.set("https://raw.githubusercontent.com/outfoxx/typescriptpoet/master/LICENSE.txt")
            distribution.set("repo")
          }
        }

        scm {
          url.set("https://github.com/outfoxx/typescriptpoet")
          connection.set("scm:https://github.com/outfoxx/typescriptpoet.git")
          developerConnection.set("scm:git@github.com:outfoxx/typescriptpoet.git")
        }

        developers {
          developer {
            id.set("kdubb")
            name.set("Kevin Wooten")
            email.set("kevin@outfoxx.io")
          }
        }

      }
    }

  }

  repositories {

    maven {
      val snapshotUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
      val releaseUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
      url = uri(if (isSnapshot) snapshotUrl else releaseUrl)

      credentials {
        username = project.findProperty("ossrhUsername")?.toString()
        password = project.findProperty("ossrhPassword")?.toString()
      }
    }

  }

}


signing {
  gradle.taskGraph.whenReady {
    isRequired = hasTask("publishMavenJavaPublicationToMavenRepository")
  }
  sign(publishing.publications["mavenJava"])
}
