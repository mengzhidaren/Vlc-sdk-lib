plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.yyl.vlc"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.yyl.vlc"
        minSdk = 24
        targetSdk = 36
        versionCode = 5
        versionName = "3.3.6"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
    if(libs.versions.useNdk.get()== "1"){
        packaging {
            jniLibs {
                pickFirsts += "**/libvlc.so"
                pickFirsts += "**/libvlcjni.so"
            }
        }
    }

}

dependencies {
//    官方仓库
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
//新仓库
    implementation("io.github.mengzhidaren:vlc-android-sdk:3.6.3")
//    implementation(project(":libvlc"))
//其它库
    implementation("tv.danmaku.ijk.media:ijkplayer-java:0.8.8")
    implementation("tv.danmaku.ijk.media:ijkplayer-armv7a:0.8.8")
    implementation("tv.danmaku.ijk.media:ijkplayer-arm64:0.8.8")
    implementation("com.danikula:videocache:2.7.1")

}
