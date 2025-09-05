plugins {
    alias(libs.plugins.android.library)
}
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
    api("org.videolan.android:libvlc-all:3.6.3")
}




//tasks.withType(JavaCompile) {
//    options.encoding = "UTF-8"
//}
//tasks.withType(Javadoc) {
//    options.encoding = "UTF-8"
//}
//
//buildscript {
//    repositories {
//        jcenter()
//    }
//    dependencies {
////        classpath 'com.novoda:bintray-release:0.9.2'
//        classpath 'com.github.panpf.bintray-publish:bintray-publish:1.0.0'
//    }
//}
//apply plugin: 'com.github.panpf.bintray-publish'
////apply plugin: 'com.novoda.bintray-release'
//publish {
//    userOrg = 'yuyunlongyyl' //用户所在组织
//    groupId = 'com.yyl.vlc'// 包名
//    artifactId = 'vlc-android-sdk' // library的名字
//    publishVersion = project.properties.get("versionJcenter") // 版本
//    desc = 'vlc-android-lib  suport x86_64   x86  armeabi-v7a  arm64-v8a'
//    // 描述
//    website = 'https://github.com/mengzhidaren/Vlc-sdk-lib'   // 项目的主页
//}
//
