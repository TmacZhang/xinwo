
#pragma once

#include <cstdint>
#include <sys/cdefs.h>

__BEGIN_DECLS

uint64_t nanotime();
// UTC time
static uint64_t now();

__END_DECLS