#include <jni.h>
#include <string>
#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <unistd.h>
#include <android/log.h>
#define LOG_TAG "jin"

#define ALOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define ALOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define ALOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define ALOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


void signalHandler(int signum) {
    ALOGW("Signal received: %d\n", signum);
}

int test() {
    // 定义替代信号栈的结构体
    stack_t altStack;
    altStack.ss_sp = malloc(SIGSTKSZ);  // 分配替代信号栈的内存
    altStack.ss_size = SIGSTKSZ;
    altStack.ss_flags = 0;

    // 设置替代信号栈
    if (sigaltstack(&altStack, NULL) == -1) {
        ALOGW("sigaltstack");
        exit(EXIT_FAILURE);
    }

    // 注册信号处理程序
    struct sigaction sa;
    sa.sa_handler = signalHandler;
    sa.sa_flags = SA_ONSTACK;
    sigemptyset(&sa.sa_mask);
//    if (sigaction(SIGSEGV, &sa, NULL) == -1) {
//        ALOGW("sigaction");
//        exit(EXIT_FAILURE);
//    }
//
//    // 发送信号
//    ALOGW("Sending signal...\n");
//    kill(getpid(), SIGSEGV);
    return 0;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xjh_xinwo_module_MainActivity_registerSignal(JNIEnv *env, jobject thiz) {
     test();
}