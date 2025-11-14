plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.6.0")
    implementation(gradleApi())
    implementation(localGroovy())
}
