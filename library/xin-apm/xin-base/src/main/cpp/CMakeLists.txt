# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

set(TARGET plt-base)
project(${TARGET})

# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.4.1)

add_compile_options(-Wall -Wextra -Wexit-time-destructors -D_FILE_OFFSET_BITS=64 -D_7ZIP_ST)
#配置-std=c++1z
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

include_directories(
        ${CMAKE_SOURCE_DIR}/include
        ${CMAKE_SOURCE_DIR}/lzma
)

add_subdirectory(fmtlib)

set(BASE_SOURCES abi_compatibility.cpp chrono_utils.cpp file.cpp cmsg.cpp liblog_symbols.cpp
        logging.cpp mapped_file.cpp parsebool.cpp parsenetaddress.cpp process.cpp properties.cpp
        stringprintf.cpp strings.cpp threads.cpp )
file(GLOB PROC_INFO_SOURCES procinfo/*.cpp)
file(GLOB ASYNC_SAFE_SOURCES async_safe/*.cpp)
set(LZMA_SOURCES lzma/7zAlloc.c lzma/7zArcIn.c lzma/7zBuf2.c lzma/7zBuf.c lzma/7zCrc.c lzma/7zCrcOpt.c
        lzma/7zDec.c lzma/7zFile.c lzma/7zStream.c lzma/Aes.c lzma/AesOpt.c lzma/Alloc.c lzma/Bcj2.c
        lzma/Bra86.c lzma/Bra.c lzma/BraIA64.c lzma/CpuArch.c lzma/Delta.c lzma/LzFind.c
        lzma/Lzma2Dec.c lzma/Lzma2Enc.c lzma/Lzma86Dec.c lzma/Lzma86Enc.c lzma/LzmaDec.c lzma/LzmaEnc.c
        lzma/LzmaLib.c lzma/Ppmd7.c lzma/Ppmd7Dec.c lzma/Ppmd7Enc.c lzma/Sha256.c lzma/Sort.c
        lzma/Xz.c lzma/XzCrc64.c lzma/XzCrc64Opt.c lzma/XzDec.c lzma/XzEnc.c lzma/XzIn.c)
set(BIONIC_SOURCES bionic/ndk_port.cpp)
set(XIN_LINKER_SOURCES xin_linker/xin_dlfcn.cpp)
set(XIN_UTIL_SOURCES xin_util/ktime.cpp)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        ${TARGET}

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${BASE_SOURCES}
        ${PROC_INFO_SOURCES}
        ${ASYNC_SAFE_SOURCES}
        ${LZMA_SOURCES}
        ${BIONIC_SOURCES}
        ${XIN_LINKER_SOURCES}
        ${XIN_UTIL_SOURCES}
)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        ${TARGET}

        fmt

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})