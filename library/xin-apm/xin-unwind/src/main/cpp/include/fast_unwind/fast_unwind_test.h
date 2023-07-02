
#pragma once

#include <backtrace/backtrace_constants.h>
#include <bits/pthread_types.h>
#include <cstdint>
#include <sys/cdefs.h>

__BEGIN_DECLS

struct malloc_allocation_t {
  size_t index;
  int allocation_size;
  void *memory_address;
  int backtrace_frame_count;
  uintptr_t backtrace[MAX_BACKTRACE_FRAMES];
};

struct malloc_allocation_list_node_t {
  malloc_allocation_t *current_allocation;
  malloc_allocation_list_node_t *next_node;
};

struct map_node_t {
  long long int key;
  malloc_allocation_t *malloc_allocation;
  map_node_t *next;
};

struct map_t {
  int size;
  map_node_t **list;
  pthread_mutex_t *list_mutex;
};

extern map_t *create_map(int size);

extern void free_map(map_t *t);

extern void insert_in_map(map_t *t, long long int key, malloc_allocation_t *val);

extern malloc_allocation_list_node_t *dump_all_allocation_in_map(map_t *t);

extern malloc_allocation_t *remove_in_map(map_t *t, long long int key);

extern void init_memory_allocation();

extern void add_memory_allocation(void *memory_address, size_t size);

extern void remove_memory_allocation(void *memory_address);

extern malloc_allocation_list_node_t *get_all_malloc_allocations();

extern void free_all_malloc_allocation(malloc_allocation_list_node_t *);

extern long long int get_all_allocation_size();

extern long long int get_live_allocation_size();

extern long long int get_allocation_index();

__END_DECLS
