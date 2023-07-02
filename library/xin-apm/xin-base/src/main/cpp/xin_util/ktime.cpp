
#include <cstdint>
#include <ctime>
#include <xin_util/ktime.h>

static constexpr uint64_t MILLIS_PER_SEC = 1000;
static constexpr uint64_t MICRO_PER_SEC = 1000 * MILLIS_PER_SEC;
static constexpr uint64_t NANOS_PER_SEC = 1000 * MICRO_PER_SEC;

uint64_t nanotime() {
  timespec ts{};
  clock_gettime(CLOCK_MONOTONIC, &ts);
  return static_cast<uint64_t>(ts.tv_sec * NANOS_PER_SEC + ts.tv_nsec);
}

uint64_t now() {
  timespec ts{};
  clock_gettime(CLOCK_REALTIME, &ts);
  return static_cast<uint64_t>(ts.tv_sec * MILLIS_PER_SEC +
                               ts.tv_nsec * MILLIS_PER_SEC / NANOS_PER_SEC);
}