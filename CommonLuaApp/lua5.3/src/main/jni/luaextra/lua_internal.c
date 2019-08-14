//
// Created by Administrator on 2019/7/24.
//

#include "lua_internal.h"

#include <unistd.h>
#include "stdlib.h"
#include "stdio.h"
#include "string.h"
#define BF_KEY "heaven7"
#define BF_KEY_LEN 7

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

LUALIB_API FILE *ext_decode(FILE *infile, int headerSize, const char *fn) {
    int* ibuf = malloc(4);
    FILE          *fp;
    char *outBuf, *inBuf, *tmpBuf;
    /*int            fd;
    const char      *file_temp = "tmp-lut-XXXXXX";*/

    BLOWFISH_KEY* key;

    fp = NULL;
    inBuf = NULL;
    outBuf = NULL;
    tmpBuf = NULL;
    key = NULL;
    //move to offset
    fseek(infile, headerSize, SEEK_SET);

    //read size and expect size
    if(fread(ibuf, 4, 1, infile) != 1){
        goto failed;
    }
    int totalSize = *ibuf;
    if(fread(ibuf, 4, 1, infile) != 1){
        goto failed;
    }
    int expectSize = *ibuf;

    inBuf = malloc(totalSize);
    if (inBuf == NULL) {
        goto failed;
    }
    outBuf = malloc(totalSize);
    if (outBuf == NULL) {
        goto failed;
    }
    tmpBuf = malloc(expectSize);

    //head + total+expect+en_content
    if (fread(inBuf, 1, totalSize, infile) < totalSize) {
        goto failed;
    }
    fclose(infile);
    infile = NULL;

    key = malloc(sizeof(BLOWFISH_KEY));
    BF_de(key, BF_KEY, BF_KEY_LEN, (const char*)inBuf, outBuf, totalSize);

    free(key);
    key = NULL;
    free(inBuf);
    inBuf = NULL;

    memcpy(outBuf, tmpBuf, expectSize);
    free(outBuf);
    outBuf = NULL;

    char *fullPath = NULL;
    createTempFile(fn, &fullPath);
    if(fullPath == NULL){
        goto failed;
    }
    fp = fopen(fullPath, "w+");
    if (fp == NULL) {
        goto failed;
    }
    unlink(fullPath); //delete on exit
   /* // the end six must be 'XXXXXX'
    fd = mkstemp(fullPath);//must use array
    if (fd < 0) {
        goto failed;
    }
    unlink(fullPath);

    fp = fdopen(fd, "wb+");
    if (fp == NULL) {
        goto failed;
    }*/
    fwrite(tmpBuf, 1, expectSize, fp);
    free(tmpBuf);
     //fp is managed by lua
    return fp;

    failed:
    if (fp) {
        fclose(fp);
    }
    if (infile) {
        fclose(infile);
    }
    if(tmpBuf){
        free(tmpBuf);
    }
    if (inBuf) {
        free(inBuf);
    }
    if (outBuf) {
        free(outBuf);
    }
    if(key){
        free(key);
    }
    return NULL;
}