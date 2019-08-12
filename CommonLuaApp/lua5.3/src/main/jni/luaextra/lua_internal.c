//
// Created by Administrator on 2019/7/24.
//

#include "lua_internal.h"

LUALIB_API int luaB_dumpStack(lua_State* L){
    __printImpl("\nbegin dump lua stack: ", 0, 1);
    int i = 0;
    int top = lua_gettop(L);

    char buf[20];
    for (i = 1; i <= top; ++i) {
        int t = lua_type(L, i);
        switch (t) {
            case LUA_TSTRING:
            {
                sprintf(buf, "'%s' ", lua_tostring(L, i));
                __printImpl(buf, 0, 0);
            }
                break;
            case LUA_TBOOLEAN:
            {
                __printImpl(lua_toboolean(L, i) ? "true " : "false ", 0, 0);
            }
                break;

            case LUA_TNUMBER:
            {
                sprintf(buf, "%g ", lua_tonumber(L, i));
                __printImpl(buf, 0, 0);
            }
                break;
            default:
            {
                sprintf(buf, "%s ", lua_typename(L, t));
                __printImpl(buf, 0, 0);
            }
                break;
        }
    }
    __flushPrint();
    return 0;
}

#include <malloc.h>
#include <stdlib.h>
#include <unistd.h>
LUALIB_API FILE* ext_decode(FILE* ofp){
    int            fd, len;
    size_t         sz;
    FILE          *fp;
    unsigned char *buf, *obuf;
    char           file_temp[] = "/tmp/luajit-XXXXXX";

    fp = NULL;
    buf = NULL;
    obuf = NULL;
    fd = -1;

    fseek(ofp, 0L, SEEK_END);
    sz = ftell(ofp);

    obuf = malloc(sz);
    if (obuf == NULL) {
        goto failed;
    }

    fseek(ofp, 0L, SEEK_SET);
    if (fread(obuf, 1, sz, ofp) < sz) {
        goto failed;
    }

    fclose(ofp);
    ofp = NULL;

   /* buf = blowfish_decrypt(obuf + FILE_HEADER_LEN,
                           sz - FILE_HEADER_LEN,
                           g_key,
                           g_iv,
                           &len);*/
    if (buf == NULL) {
        goto failed;
    }

    free(obuf);
    obuf = NULL;

    fd = mkstemp(file_temp);
    if (fd < 0) {
        goto failed;
    }
    unlink(file_temp);

    fp = fdopen(fd, "wb+");
    if (fp == NULL) {
        goto failed;
    }
    fwrite(buf, 1, len, fp);
    free(buf);
    buf = NULL;

    return fp;

    failed:

    if (fp) {
        fclose(fp);
    }

    if (ofp) {
        fclose(ofp);
    }

    if (obuf) {
        free(obuf);
    }

    if (buf) {
        free(buf);
    }

    return NULL;
}