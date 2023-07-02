#pragma once

#include <android/log.h>
#include <errno.h>
#include <log/log.h>

#ifndef LOG_TAG
#define LOG_TAG "default"
#endif

#ifndef KCHECK
#define KCHECK(assertion)                                                                          \
  if (!(assertion)) {                                                                              \
    ALOGE("CHECK failed at %s (line: %d) - <%s>: %s: %s", __FILE__, __LINE__, __FUNCTION__,        \
          #assertion, strerror(errno));                                                            \
  }
#endif

#ifndef KCHECKV
#define KCHECKV(assertion)                                                                         \
  if (!(assertion)) {                                                                              \
    ALOGE("CHECK failed at %s (line: %d) - <%s>: %s: %s", __FILE__, __LINE__, __FUNCTION__,        \
          #assertion, strerror(errno));                                                            \
    return;                                                                                        \
  }
#endif

#ifndef KCHECKI
#define KCHECKI(assertion)                                                                         \
  if (!(assertion)) {                                                                              \
    ALOGE("CHECK failed at %s (line: %d) - <%s>: %s: %s", __FILE__, __LINE__, __FUNCTION__,        \
          #assertion, strerror(errno));                                                            \
    return -1;                                                                                     \
  }
#endif

#ifndef KCHECKP
#define KCHECKP(assertion)                                                                         \
  if (!(assertion)) {                                                                              \
    ALOGE("CHECK failed at %s (line: %d) - <%s>: %s: %s", __FILE__, __LINE__, __FUNCTION__,        \
          #assertion, strerror(errno));                                                            \
    return nullptr;                                                                                \
  }
#endif

#ifndef KCHECKB
#define KCHECKB(assertion)                                                                         \
  if (!(assertion)) {                                                                              \
    ALOGE("CHECK failed at %s (line: %d) - <%s>: %s: %s", __FILE__, __LINE__, __FUNCTION__,        \
          #assertion, strerror(errno));                                                            \
    return false;                                                                                  \
  }
#endif

#ifndef KFINISHP_FUC
#define KFINISHP_FUC(assertion, func)                                                              \
  if (!(assertion)) {                                                                              \
    ALOGE("CHECK failed at %s (line: %d) - <%s>: %s: %s", __FILE__, __LINE__, __FUNCTION__,        \
          #assertion, strerror(errno));                                                            \
    func();                                                                                        \
    return nullptr;                                                                                \
  }
#endif

#ifndef KFINISHV_FUC
#define KFINISHV_FUC(assertion, func)                                                              \
  if (!(assertion)) {                                                                              \
    ALOGE("CHECK failed at %s (line: %d) - <%s>: %s: %s", __FILE__, __LINE__, __FUNCTION__,        \
          #assertion, strerror(errno));                                                            \
    func();                                                                                        \
    return;                                                                                        \
  }
#endif