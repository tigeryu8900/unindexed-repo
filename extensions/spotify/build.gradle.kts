dependencies {
    compileOnly(project(":extensions:shared:library"))
    compileOnly(project(":extensions:shared:stub"))
    compileOnly(project(":extensions:spotify:stub"))
    compileOnly(libs.annotation)
    implementation(libs.hiddenapi)
}

android {
    defaultConfig {
        minSdk = 28
    }
}
