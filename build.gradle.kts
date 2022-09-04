plugins {
    kotlin("multiplatform") version "1.7.10"
}

repositories {
    mavenCentral()
}

kotlin {
    linuxX64("linux") {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
    mingwX64("windows") {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                implementation("com.squareup.okio:okio:3.2.0")
            }
        }
    }
}
