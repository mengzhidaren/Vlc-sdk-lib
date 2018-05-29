//
// Created by yyl on 2018/5/23.
//

#include <jni.h>
#include "yyl_log.h"
#include "vlc_common.h"
#include "libvlcjni-vlcobject.h"
#include "media_player_internal.h"
//#include "vlc_plugin.h"
//#undef INPUT_RECORD_PREFIX
//
//#define INPUT_RECORD_PREFIX="long.mp4"

input_thread_t *getInputThread(JNIEnv *env, jobject mediaPlayer) {
    vlcjni_object *vlcjniObject = VLCJniObject_getInstance(env, mediaPlayer);
    libvlc_media_player_t *player = vlcjniObject->u.p_mp;
    vlc_mutex_lock(&player->input.lock);
    input_thread_t *input_thread = player->input.p_thread;
    if (input_thread) {
        vlc_object_hold(input_thread);
    } else {
        LOGII("getInputThread   error");
    }
    vlc_mutex_unlock(&player->input.lock);
    return input_thread;
}

JNIEXPORT jboolean
JNICALL
Java_org_videolan_vlc_RecordEvent_startRecord(JNIEnv *env, jobject instance, jobject mediaPlayer,
                                              jstring fileDirectory_, jstring fileName_) {
    const char *fileDirectory = (*env)->GetStringUTFChars(env, fileDirectory_, 0);
    const char *fileName = (*env)->GetStringUTFChars(env, fileName_, 0);
    jboolean recordStart;
    LOGI("录像目录=%s  文件名=%s", fileDirectory, fileName);
    input_thread_t *input_thread = getInputThread(env, mediaPlayer);
    if (input_thread) {
        //std{access=file{no-append,no-format,no-overwrite},mux='avi',dst='/tmp/vlcJJKum1905'}

        var_Create(input_thread, "input-record-path", VLC_VAR_STRING | VLC_VAR_DOINHERIT);
        var_Create(input_thread, "sout-record-dst-prefix", VLC_VAR_STRING | VLC_VAR_DOINHERIT);
        var_SetString(input_thread, "input-record-path", fileDirectory);
        var_SetString(input_thread, "sout-record-dst-prefix", fileName);
        var_SetBool(input_thread, "record", true);

//        var_SetString(input_thread, "sout-standard-mux", "avi");

        vlc_object_release(input_thread);
        recordStart = JNI_TRUE;
        LOGII("Record   开始录制");
    } else {
        LOGII("Record   录制失败");
        recordStart = JNI_FALSE;
    }
    (*env)->ReleaseStringUTFChars(env, fileDirectory_, fileDirectory);
    (*env)->ReleaseStringUTFChars(env, fileName_, fileName);
    return recordStart;
}

JNIEXPORT jboolean
JNICALL
Java_org_videolan_vlc_RecordEvent_stopRecord(JNIEnv *env, jobject instance, jobject mediaPlayer) {
    input_thread_t *input_thread = getInputThread(env, mediaPlayer);
    if (input_thread) {
        var_SetBool(input_thread, "record", false);
        vlc_object_release(input_thread);
        LOGII("Record   录制停止");
        return JNI_TRUE;
    }
    LOGII("stopRecord  停止失败");
    return JNI_FALSE;
}

JNIEXPORT jboolean
JNICALL
Java_org_videolan_vlc_RecordEvent_isRecording(JNIEnv *env, jobject instance, jobject mediaPlayer) {
    bool b_record = false;
    input_thread_t *input_thread = getInputThread(env, mediaPlayer);
    if (input_thread) {
        b_record = var_GetBool(input_thread, "record");
        vlc_object_release(input_thread);
    }
    LOGII("isRecording   %i", b_record);
    return (jboolean) b_record;
}

JNIEXPORT jboolean
JNICALL
Java_org_videolan_vlc_RecordEvent_isSuportRecord(JNIEnv *env, jobject instance,
                                                 jobject mediaPlayer) {
    bool b_can_record = false;
    input_thread_t *input_thread = getInputThread(env, mediaPlayer);
    if (input_thread) {
        b_can_record = var_GetBool(input_thread, "can-record");
        vlc_object_release(input_thread);
    }
    LOGII("b_can_record   %i", b_can_record);
    return (jboolean) b_can_record;

}


/*
 * Remember to release the returned vout_thread_t.
 */
static vout_thread_t **
GetVoutsThread(JNIEnv *env, jobject mediaPlayer, libvlc_media_player_t *p_mi, size_t *n) {
    input_thread_t *p_input = getInputThread(env, mediaPlayer);
    if (!p_input) {
        *n = 0;
        return NULL;
    }

    vout_thread_t **pp_vouts;
    if (input_Control(p_input, INPUT_GET_VOUTS, &pp_vouts, n)) {
        *n = 0;
        pp_vouts = NULL;
    }
    vlc_object_release (p_input);
    return pp_vouts;
}

static vout_thread_t *
GetVoutThread(JNIEnv *env, jobject mediaPlayer, libvlc_media_player_t *mp, size_t num) {
    vout_thread_t *p_vout = NULL;
    size_t n;
    vout_thread_t **pp_vouts = GetVoutsThread(env, mediaPlayer, mp, &n);
    if (pp_vouts == NULL)
        goto err;

    if (num < n)
        p_vout = pp_vouts[num];

    for (size_t i = 0; i < n; i++)
        if (i != num)
            vlc_object_release (pp_vouts[i]);
    free(pp_vouts);

    if (p_vout == NULL)
        err:
        libvlc_printerr("Video output not active");
    return p_vout;
}

int
libvlc_video_take_snapshot2(JNIEnv *env, jobject mediaPlayer, libvlc_media_player_t *p_mi,
                            unsigned num,
                            const char *psz_filepath,
                            unsigned int i_width, unsigned int i_height) {
    //assert( psz_filepath );
    vout_thread_t *p_vout = GetVoutThread(env, mediaPlayer, p_mi, num);
    if (p_vout == NULL)
        return -1;

    /* FIXME: This is not atomic. All parameters should be passed at once
     * (obviously _not_ with var_*()). Also, the libvlc object should not be
     * used for the callbacks: that breaks badly if there are concurrent
     * media players in the instance. */
    var_Create(p_vout, "snapshot-width", VLC_VAR_INTEGER);
    var_SetInteger(p_vout, "snapshot-width", i_width);
    var_Create(p_vout, "snapshot-height", VLC_VAR_INTEGER);
    var_SetInteger(p_vout, "snapshot-height", i_height);
    var_Create(p_vout, "snapshot-path", VLC_VAR_STRING);
    var_SetString(p_vout, "snapshot-path", psz_filepath);
    var_Create(p_vout, "snapshot-format", VLC_VAR_STRING);
    var_SetString(p_vout, "snapshot-format", "png");
    var_TriggerCallback(p_vout, "video-snapshot");
    vlc_object_release(p_vout);
    return 0;
}

JNIEXPORT jint JNICALL
Java_org_videolan_vlc_RecordEvent_takeSnapshot(JNIEnv *env, jobject instance, jobject mediaPlayer,
                                               jstring path_, jint width, jint height) {
    const char *path = (*env)->GetStringUTFChars(env, path_, 0);
    vlcjni_object *vlcjniObject = VLCJniObject_getInstance(env, mediaPlayer);

    int result = libvlc_video_take_snapshot2(env,
                                             mediaPlayer,
                                             vlcjniObject->u.p_mp,
                                             0,
                                             path,
                                             (unsigned int) width,
                                             (unsigned int) height);
    (*env)->ReleaseStringUTFChars(env, path_, path);
    return result;
}