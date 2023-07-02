#define _GNU_SOURCE 1
#define LOG_TAG "NDK_PORT"

#include <bionic/ndk_port.h>
#include <cstdlib>
#include <log/log_main.h>
#include <string.h>
#include <sys/system_properties.h>
#include <sys/uio.h>
#include <syscall.h>
#include <unistd.h>

extern "C" const char *__gnu_basename(const char *path) {
  const char *last_slash = strrchr(path, '/');
  return (last_slash != nullptr) ? last_slash + 1 : path;
}

extern "C" ssize_t process_vm_readv(pid_t __pid, const struct iovec *__local_iov,
                                    unsigned long __local_iov_count,
                                    const struct iovec *__remote_iov,
                                    unsigned long __remote_iov_count, unsigned long __flags)
    __attribute__((weak));
extern "C" ssize_t process_vm_writev(pid_t __pid, const struct iovec *__local_iov,
                                     unsigned long __local_iov_count,
                                     const struct iovec *__remote_iov,
                                     unsigned long __remote_iov_count, unsigned long __flags)
    __attribute__((weak));

extern "C" void android_set_abort_message(const char *__msg) __attribute__((weak));

extern "C" const char *getprogname(void) __attribute__((weak));

extern "C" void __system_property_read_callback(const prop_info *info,
                                                void (*callback)(void *cookie, const char *name,
                                                                 const char *value,
                                                                 uint32_t serial),
                                                void *cookie) __attribute__((weak));
extern "C" bool __system_property_wait(const prop_info *__pi, uint32_t __old_serial,
                                       uint32_t *__new_serial_ptr,
                                       const struct timespec *__relative_timeout)
    __attribute__((weak));

extern "C" uint32_t __system_property_area_serial(void) __attribute__((weak));
extern "C" uint32_t __system_property_serial(const prop_info *__pi) __attribute__((weak));

/*
 * Need to do this since process_vm_readv() is __INTRODUCED_IN __ANDROID_API__ 23.
 */
extern "C" ssize_t xin_process_vm_readv(pid_t pid, const struct iovec *lvec, unsigned long liovcnt,
                                         const struct iovec *rvec, unsigned long riovcnt,
                                         unsigned long flags) {
  if (process_vm_readv) {
    return process_vm_readv(pid, lvec, liovcnt, rvec, riovcnt, flags);
  }
  return syscall(__NR_process_vm_readv, (long)pid, lvec, liovcnt, rvec, riovcnt, flags);
}

/*
 * Need to do this since process_vm_readv() is __INTRODUCED_IN __ANDROID_API__ 23.
 */
extern "C" ssize_t xin_process_vm_writev(pid_t pid, const struct iovec *lvec,
                                          unsigned long liovcnt, const struct iovec *rvec,
                                          unsigned long riovcnt, unsigned long flags) {
  if (process_vm_writev) {
    return process_vm_writev(pid, lvec, liovcnt, rvec, riovcnt, flags);
  }
  return syscall(__NR_process_vm_writev, (long)pid, lvec, liovcnt, rvec, riovcnt, flags);
}

extern "C" void xin_set_abort_message(const char *__msg) {
  if (android_set_abort_message) {
    return android_set_abort_message(__msg);
  }
}

extern "C" const char *xin_getprogname(void) {
  if (getprogname) {
    return getprogname();
  }
  return "getprogname not support below API 21";
}

extern "C" void xin__system_property_read_callback(const prop_info *info,
                                                    void (*callback)(void *cookie, const char *name,
                                                                     const char *value,
                                                                     uint32_t serial),
                                                    void *cookie) {
  if (__system_property_read_callback) {
    return __system_property_read_callback(info, callback, cookie);
  }
  LOG_ALWAYS_FATAL("__system_property_read_callback not support yet!");
}
extern "C" bool xin__system_property_wait(const prop_info *__pi, uint32_t __old_serial,
                                           uint32_t *__new_serial_ptr,
                                           const struct timespec *__relative_timeout) {
  if (__system_property_wait) {
    return __system_property_wait(__pi, __old_serial, __new_serial_ptr, __relative_timeout);
  }
  LOG_ALWAYS_FATAL("__system_property_wait not support yet!");
}

extern "C" uint32_t xin__system_property_area_serial(void) {
  if (__system_property_area_serial) {
    return __system_property_area_serial();
  }
  LOG_ALWAYS_FATAL("__system_property_area_serial not support yet!");
}
extern "C" uint32_t xin__system_property_serial(const prop_info *__pi) {
  if (__system_property_serial) {
    return __system_property_serial(__pi);
  }
  LOG_ALWAYS_FATAL("__system_property_serial not support yet!");
}
