#include <bits/pthread_types.h>
#include <cstdlib>
#include <fast_unwind/fast_unwind.h>
#include <xin_linker/xin_macros.h>
#include <log/log.h>
#include <pthread.h>
#include <unistd.h>

#define LOG_TAG "unwind"

static FAST_UNWIND_TLS_INITIAL_EXEC uintptr_t stack_top = 0;
static FAST_UNWIND_TLS_INITIAL_EXEC pthread_once_t once_control_tls = PTHREAD_ONCE_INIT;

void fast_unwind_init() {
  if (getpid() == gettid() && stack_top == 0) {
    LOG_ALWAYS_FATAL("main thread pthread_attr_t must be init in advance!");
  }
  pthread_attr_t attr;
  pthread_getattr_np(pthread_self(), &attr);
  stack_top = (uintptr_t)(attr.stack_size + static_cast<char *>(attr.stack_base));
}

XIN_EXPORT void fast_unwind_init_main_thread() {
  if (getpid() != gettid()) {
    LOG_ALWAYS_FATAL("%s must be called on main thread!", __FUNCTION__);
  }
  stack_top = -1;
  pthread_once(&once_control_tls, fast_unwind_init);
}

inline __attribute__((__always_inline__)) uintptr_t get_thread_stack_top() { return stack_top; }

size_t frame_pointer_unwind(uintptr_t *buf, size_t num_entries) {
  pthread_once(&once_control_tls, fast_unwind_init);
  struct frame_record {
    uintptr_t next_frame, return_addr;
  };

  auto begin = reinterpret_cast<uintptr_t>(__builtin_frame_address(0));
  auto end = get_thread_stack_top();

  stack_t ss;
  if (sigaltstack(nullptr, &ss) == 0 && (ss.ss_flags & SS_ONSTACK)) {
    end = reinterpret_cast<uintptr_t>(ss.ss_sp) + ss.ss_size;
  }

  size_t num_frames = 0;
  while (true) {
    auto *frame = reinterpret_cast<frame_record *>(begin);
    if (num_frames < num_entries) {
      buf[num_frames] = frame->return_addr;
    }
    ++num_frames;
    if (frame->next_frame < begin + sizeof(frame_record) || frame->next_frame >= end ||
        frame->next_frame % sizeof(void *) != 0) {
      break;
    }
    begin = frame->next_frame;
  }

  return num_frames;
}