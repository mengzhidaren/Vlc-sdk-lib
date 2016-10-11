#include <jni.h>
#include <string>

extern "C"
jstring
Java_videolist_yyl_com_vlc_1sdk_1lib_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
