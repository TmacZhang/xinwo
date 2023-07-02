#pragma once

#include <backtrace/backtrace_constants.h>
#include <cstdint>
#include <sys/cdefs.h>

__BEGIN_DECLS

extern "C" size_t android_unsafe_frame_pointer_chase(uintptr_t *buf, size_t num_entries)
    __attribute__((weak));

#define FAST_UNWIND_TLS_INITIAL_EXEC __thread __attribute__((tls_model("initial-exec")))

uintptr_t get_thread_stack_top();
/* must initialize pthread_attr_t attr in main thread to avoid open@LIBC recursive */
void fast_unwind_init_main_thread();

size_t frame_pointer_unwind(uintptr_t *buf, size_t num_entries);

inline __attribute__((__always_inline__)) size_t fast_unwind(uintptr_t *buf, size_t num_entries) {
  if (android_unsafe_frame_pointer_chase) {
    return android_unsafe_frame_pointer_chase(buf, num_entries);
  }
  return frame_pointer_unwind(buf, num_entries);
}

__END_DECLS