#ifndef XIN_DLFCN_H
#define XIN_DLFCN_H

#include <link.h>

namespace xin {
namespace linker {

class DlFcn {
public:
  /**
   * Android N+ dlopen bypass
   */
  static void *dlopen(const char *lib_name, int flags);

  /**
   * Android N+ dlsym bypass
   */
  static void *dlsym(void *handle, const char *name);

  /**
   * Android N+ dlclose bypass
   */
  static int dlclose(void *handle);
  /**
   * Inspired by https://github.com/avs333/Nougat_dlfunctions/
   *
   * Parse ELF file based on /proc/<pid>/mappings and store .dynsym、.dynstr、.symtab、.strtab
   * information.
   *
   * It's much less effective than DlFcn::dlopen, do not use this in low
   * memory state or high performance sensitive scenario!
   *
   * It's more powerful than DlFcn::dlopen which can only get symbols in .dynsym(GLOBAL), it can
   * also get symbols in .symtab(LOCAL).
   */
  static void *dlopen_elf(const char *lib_name, int flags);
  /**
   * Since dlopen_elf consumes more memory, when fetching multiple symbols in a so, try to open
   * it only once, get all symbol addresses and cache them and then close it.
   */
  static void *dlsym_elf(void *handle, const char *name);
  /**
   * Release memroy.
   */
  static int dlclose_elf(void *handle);

private:
  struct dl_iterate_data {
    dl_phdr_info info_;
  };

  static int android_api_;

  static void init_api();

  static int dl_iterate_callback(dl_phdr_info *info, size_t size, void *data);

  /**
   * ELF hash func
   */
  static uint32_t elf_hash(const uint8_t *name);
  /**
   * GNU hash func
   */
  static uint32_t elf_gnu_hash(const uint8_t *name);
};

} // namespace linker

} // namespace xin

#endif // XIN_DLFCN_H