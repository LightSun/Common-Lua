# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

include_directories(src/main/jni/lua)
include_directories(src/main/jni/cjson)

#aux_source_directory(src/main/jni/lua SRC_LUA)
#aux_source_directory(src/main/jni/luaextra SRC_LUA_EXTRA)
aux_source_directory(src/main/jni/cjson SRC_CJSON)
aux_source_directory(src/main/jni/common SRC_COMMON)
aux_source_directory(src/main/jni/luaext_java SRC_LUA_JAVA)
add_library( # Sets the name of the library.
        lua

        # Sets the library as a shared library.
        #STATIC
        SHARED

        # Provides a relative path to your source file(s).
        ${SRC_COMMON}
        src/main/jni/lua/lapi.c
        src/main/jni/lua/lauxlib.c
        src/main/jni/lua/lbaselib.c
        src/main/jni/lua/lbitlib.c
        src/main/jni/lua/lcode.c
        src/main/jni/lua/lcorolib.c
        src/main/jni/lua/lctype.c
        src/main/jni/lua/ldblib.c
        src/main/jni/lua/ldebug.c
        src/main/jni/lua/ldo.c
        src/main/jni/lua/ldump.c
        src/main/jni/lua/lfunc.c
        src/main/jni/lua/lgc.c
        src/main/jni/lua/linit.c

        src/main/jni/lua/liolib.c
        src/main/jni/lua/llex.c
        src/main/jni/lua/lmathlib.c
        src/main/jni/lua/lmem.c
        src/main/jni/lua/loadlib.c
        src/main/jni/lua/lobject.c
        src/main/jni/lua/lopcodes.c
        src/main/jni/lua/loslib.c
        src/main/jni/lua/lparser.c

        src/main/jni/lua/lstate.c
        src/main/jni/lua/lstring.c
        src/main/jni/lua/lstrlib.c
        src/main/jni/lua/ltable.c
        src/main/jni/lua/ltablib.c
        src/main/jni/lua/ltm.c
        src/main/jni/lua/lundump.c
        src/main/jni/lua/lutf8lib.c
        src/main/jni/lua/lvm.c
        src/main/jni/lua/lzio.c

        src/main/jni/luaextra/lua_extra.c
        src/main/jni/luaextra/lua_internal.c
        src/main/jni/luaextra/LuaRegistry.cpp

        # test
        src/main/jni/luaextra/test.cpp
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
        ${log-lib}
        )
target_link_libraries( # Specifies the target library.
        # Links the target library to the log library
        # included in the NDK.
        cjson
        lua
        )