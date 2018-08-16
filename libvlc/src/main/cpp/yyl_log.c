//
// Created by Administrator on 2017/9/28/028.
//

#include "yyl_log.h"

int YYL_DEBUG_ = 1;
int YYL_LOGCAT = 1;
char *JSON_RESULT;
typedef uint8_t u1;
/**
 *非法字符会闪退
 * @param fmt
 */
static u1 checkUtfBytes(const char* bytes, const char** errorKind) {
    while (*bytes != '\0') {
        u1 utf8 = *(bytes++);
        // Switch on the high four bits.
        switch (utf8 >> 4) {
            case 0x00:
            case 0x01:
            case 0x02:
            case 0x03:
            case 0x04:
            case 0x05:
            case 0x06:
            case 0x07:
                // Bit pattern 0xxx. No need for any extra bytes.
                break;
            case 0x08:
            case 0x09:
            case 0x0a:
            case 0x0b:
            case 0x0f:
                /*
                 * Bit pattern 10xx or 1111, which are illegal start bytes.
                 * Note: 1111 is valid for normal UTF-8, but not the
                 * modified UTF-8 used here.
                 */
                *errorKind = "start";
                return utf8;
            case 0x0e:
                // Bit pattern 1110, so there are two additional bytes.
                utf8 = *(bytes++);
                if ((utf8 & 0xc0) != 0x80) {
                    *errorKind = "continuation";
                    return utf8;
                }
                // Fall through to take care of the final byte.
            case 0x0c:
            case 0x0d:
                // Bit pattern 110x, so there is one additional byte.
                utf8 = *(bytes++);
                if ((utf8 & 0xc0) != 0x80) {
                    *errorKind = "continuation";
                    return utf8;
                }
                break;
        }
    }
    return 0;
}
char *string_add(char *s1, char *s2) {
    char *result = malloc(strlen(s1) + strlen(s2) + 1);//+1 for the zero-terminator
    if (result == NULL) {
        return NULL;
    }
    strcpy(result, s1);
    strcat(result, s2);
    return result;
}

/**
 * 保存控制台json信息
 * @param fmt
 */
void yyl_print_callback_json(const char *fmt, ...) {
    int n, size = 64;
    char *p;
    va_list ap;
    while (1) {
        if ((p = (char *) malloc(size * sizeof(char))) == NULL)
            break;
        /* 尝试在申请的空间中进行打印操作 */
        va_start(ap, fmt);
        n = vsnprintf(p, size, fmt,
                      ap);/* vsnprintf调用失败(n<0)，或者p的空间不足够容纳size大小的字符串(n>=size)，尝试申请更大的空间*/
        va_end(ap);
        /* 如果vsnprintf调用成功，返回该字符串长度 */
        if (n > -1 && n < size) {
            break;
        } else {
            while (size <= n) {
                size *= 2;/* 两倍原来大小的空间 */
            }
        }
    }
    if (p) {
        JSON_RESULT = string_add(JSON_RESULT, p);
        free(p);
        p = NULL;
    }
}

/**
 * 只是打印信息
 * @param fmt
 */
void yyl_print_log(const char *fmt, ...) {
    int n, size = 64;
    char *p;
    va_list ap;
    while (1) {
        if ((p = (char *) malloc(size * sizeof(char))) == NULL)
            break;
        va_start(ap, fmt);
        n = vsnprintf(p, size, fmt,
                      ap);
        va_end(ap);
        if (n > -1 && n < size) {
            break;
        } else {
            while (size <= n) {
                size *= 2;
            }
        }
    }
    if (p) {
      //  onCallbackPrint(p);
        free(p);
        p = NULL;
    }
}

/**
 * 只是log信息
 * @param fmt
 */
void yyl_callback_log(void *ptr, int level, const char *fmt, va_list vl) {
    char *p;
    int n, size = 64;
    while (1) {
        if ((p = (char *) malloc(size * sizeof(char))) == NULL)
            break;
        n = vsnprintf(p, size, fmt,
                      vl);
        if (n > -1 && n < size) {
            break;
        } else {
            while (size <= n) {
                size *= 2;
            }
        }
    }
    if (p) {
        const char* errorKind = NULL;
        checkUtfBytes(p, &errorKind);
        if (errorKind == NULL) {
         //   onCallbackLog(p);
        }else{
          //  LOGII("非法字符=%s",p);
        }
        free(p);
        p = NULL;
    }
}



