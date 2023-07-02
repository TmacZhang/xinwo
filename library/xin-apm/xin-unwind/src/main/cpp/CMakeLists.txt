# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

set(TARGET plt-unwind)
project(${TARGET})

# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.4.1)

add_compile_options(-Wall -Wextra -Wexit-time-destructors -DDEXFILE_SUPPORT)
# 配置-std=c++1z
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

include_directories(
        ${CMAKE_SOURCE_DIR}/include
        # TODO: optimize hard code
        ${CMAKE_SOURCE_DIR}/../../../../plt-base/src/main/cpp/include
        ${CMAKE_SOURCE_DIR}/../../../../plt-base/src/main/cpp/lzma
)
# TODO: optimize hard code
link_directories(${CMAKE_SOURCE_DIR}/../../../../plt-base/libs/${ANDROID_ABI}/)

# Define the base library, its includes and the needed defines.
set(BASE_SOURCES ArmExidx.cpp DexFiles.cpp DwarfCfa.cpp DwarfEhFrameWithHdr.cpp DwarfMemory.cpp
        DwarfOp.cpp DwarfSection.cpp Elf.cpp ElfInterface.cpp ElfInterfaceArm.cpp Global.cpp
        JitDebug.cpp Log.cpp MapInfo.cpp Maps.cpp Memory.cpp MemoryMte.cpp LocalUnwinder.cpp
        Regs.cpp RegsArm.cpp RegsArm64.cpp RegsX86.cpp RegsX86_64.cpp RegsMips.cpp RegsMips64.cpp
        Unwinder.cpp Symbols.cpp DexFile.cpp)

file(GLOB DEXFILE_SUPPORT_SOURCES dexfile_support/dex_file_supp.cpp)

set(BACKTRACE_SOURCES backtrace/Backtrace.cpp backtrace/BacktraceCurrent.cpp
        backtrace/BacktraceMap.cpp backtrace/BacktracePtrace.cpp backtrace/ThreadEntry.cpp
        backtrace/UnwindStack.cpp backtrace/UnwindStackMap.cpp)

set(FAST_UNWIND_SOURCES fast_unwind/fast_unwind.cpp fast_unwind/fast_unwind_test.cpp)

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
        ${DEXFILE_SUPPORT_SOURCES}
        ${BACKTRACE_SOURCES}
        ${FAST_UNWIND_SOURCES}
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

        plt-base

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})