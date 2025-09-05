plugins {
    alias(libs.plugins.android.library)
}

// 应用 Maven 包创建脚本
apply(from = "../create-maven-bundle.gradle")

android {
    namespace = "com.vlc.lib"
    compileSdk = 36
    defaultConfig {
        minSdk = 17
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
    if(libs.versions.useNdk.get()== "1"){
        externalNativeBuild {
            cmake {
                path = file("src/main/cpp/CMakeLists.txt")
                version = "3.22.1"
            }
        }
        packaging {
            // 排除本地的 .so 文件，避免与官方库冲突
            jniLibs {
                excludes += "**/libvlc.so"
                excludes += "**/libvlcjni.so"
            }
        }
    }else{
        sourceSets {
            getByName("main") {
                jniLibs {
                    srcDirs("jniLib")
                }
            }
        }
    }

}

dependencies {
    api(libs.libvlc.all)
}