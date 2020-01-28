# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

include_directories(src/main/cpp/lua)
include_directories(src/main/cpp/cjson)

#set(LIB_DIR ${CMAKE_SOURCE_DIR}/src/main/jniLibs)

#aux_source_directory(src/main/jni/lua SRC_LUA)
#aux_source_directory(src/main/jni/luaextra SRC_LUA_EXTRA)
aux_source_directory(src/main/cpp/cjson SRC_CJSON)
aux_source_directory(src/main/cpp/common SRC_COMMON)
aux_source_directory(src/main/cpp/luaext_java SRC_LUA_JAVA)
aux_source_directory(src/main/cpp/lua_test SRC_LUA_TESTS)
add_library( # Sets the name of the library.
        lua

        # Sets the library as a shared library.
        #STATIC
        SHARED

        # Provides a relative path to your source file(s).
        ${SRC_COMMON}
        src/main/cpp/lua/lapi.c
        src/main/cpp/lua/lauxlib.c
        src/main/cpp/lua/lbaselib.c
        src/main/cpp/lua/lbitlib.c
        src/main/cpp/lua/lcode.c
        src/main/cpp/lua/lcorolib.c
        src/main/cpp/lua/lctype.c
        src/main/cpp/lua/ldblib.c
        src/main/cpp/lua/ldebug.c
        src/main/cpp/lua/ldo.c
        src/main/cpp/lua/ldump.c
        src/main/cpp/lua/lfunc.c
        src/main/cpp/lua/lgc.c
        src/main/cpp/lua/linit.c

        src/main/cpp/lua/liolib.c
        src/main/cpp/lua/llex.c
        src/main/cpp/lua/lmathlib.c
        src/main/cpp/lua/lmem.c
        src/main/cpp/lua/loadlib.c
        src/main/cpp/lua/lobject.c
        src/main/cpp/lua/lopcodes.c
        src/main/cpp/lua/loslib.c
        src/main/cpp/lua/lparser.c

        src/main/cpp/lua/lstate.c
        src/main/cpp/lua/lstring.c
        src/main/cpp/lua/lstrlib.c
        src/main/cpp/lua/ltable.c
        src/main/cpp/lua/ltablib.c
        src/main/cpp/lua/ltm.c
        src/main/cpp/lua/lundump.c
        src/main/cpp/lua/lutf8lib.c
        src/main/cpp/lua/lvm.c
        src/main/cpp/lua/lzio.c

        src/main/cpp/luaextra/lua_extra.c
        src/main/cpp/luaextra/lua_internal.c
        src/main/cpp/luaextra/LuaRegistry.cpp
        src/main/cpp/luaextra/lua_bridge.cpp
        src/main/cpp/luaextra/blowfish.c
        )

add_library(
        cjson

        SHARED

        ${SRC_CJSON}
)

add_library( # Sets the name of the library.
        luajava

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${SRC_LUA_JAVA}
        )
add_library(
        lua_test
        SHARED
        ${SRC_LUA_TESTS}
)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.


# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)


target_link_libraries( # Specifies the target library.
        # Links the target library to the log library
        # included in the NDK.
        luajava
        lua
        lua_test
        ${log-lib}
        )
target_link_libraries( # Specifies the target library.
        # Links the target library to the log library
        # included in the NDK.
        lua_test
        lua
        )
target_link_libraries( # Specifies the target library.
        # Links the target library to the log library
        # included in the NDK.
        cjson
        lua
        )