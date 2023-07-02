
#pragma once

#include <sys/cdefs.h>
#include <sys/system_properties.h>
#include <sys/types.h>

__BEGIN_DECLS

#if defined(__USE_GNU) && !defined(basename)
/*
 * glibc has a basename in <string.h> that's different to the POSIX one in <libgen.h>.
 * It doesn't modify its argument, and in C++ it's const-correct.
 */
#if defined(__cplusplus)
extern "C++" char *basename(char *__path) __RENAME(__gnu_basename);
extern "C++" const char *basename(const char *__path) __RENAME(__gnu_basename);
#else
char *basename(const char *__path) __RENAME(__gnu_basename);
#endif
#endif

ssize_t xin_process_vm_readv(pid_t pid, const struct iovec *lvec, unsigned long liovcnt,
                              const struct iovec *rvec, unsigned long riovcnt, unsigned long flags);
ssize_t xin_process_vm_writev(pid_t pid, const struct iovec *lvec, unsigned long liovcnt,
                               const struct iovec *rvec, unsigned long riovcnt,
                               unsigned long flags);

void xin_set_abort_message(const char *__msg);

const char *xin_getprogname(void);

void xin__system_property_read_callback(const prop_info *info,
                                         void (*callback)(void *cookie, const char *name,
                                                          const char *value, uint32_t serial),
                                         void *cookie);
bool xin__system_property_wait(const prop_info *__pi, uint32_t __old_serial,
                                uint32_t *__new_serial_ptr,
                                const struct timespec *__relative_timeout);

uint32_t xin__system_property_area_serial(void);
uint32_t xin__system_property_serial(const prop_info *__pi);

__END_DECLS