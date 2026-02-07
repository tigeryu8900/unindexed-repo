plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.protobuf)
}

dependencies {
    compileOnly(project(":extensions:shared:library"))
    compileOnly(project(":extensions:shared:stub"))
    compileOnly(project(":extensions:spotify:stub"))
    compileOnly(libs.annotation)
    implementation(libs.hiddenapi)

    //noinspection UseTomlInstead
    implementation("com.github.ynab:J2V8:6.2.1-16kb.2@aar")
    implementation(libs.protobuf.javalite)
}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 23
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}
