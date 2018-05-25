//
// Created by Administrator on 2017/9/28/028.
//

#ifndef HELLO_JNI_YYL_LOG_H
#define HELLO_JNI_YYL_LOG_H

#include <jni.h>
#include <android/log.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
extern int YYL_DEBUG_;
extern int YYL_LOGCAT;
extern char *JSON_RESULT;

#define LOGII(format, ...) if(YYL_DEBUG_){__android_log_print(ANDROID_LOG_INFO, "YYL", format, ##__VA_ARGS__);}

void yyl_print_callback_json(const char *fmt, ...);
void yyl_print_log(const char *fmt, ...);
void yyl_callback_log(void *ptr, int level, const char *fmt, va_list vl);
#endif //HELLO_JNI_YYL_LOG_H
