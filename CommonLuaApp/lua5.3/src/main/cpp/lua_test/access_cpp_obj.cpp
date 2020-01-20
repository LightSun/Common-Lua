#include "access_cpp_obj.h"

#include <iostream>
#include <list>
#include <assert.h>

class ACB_Test{
public:
    ~ACB_Test(){
        ext_getLuaPrint()("detach >>> ACB_Test", 50, 1);
    }
    void print(){
        ext_getLuaPrint()("hello lua cpp.", 50, 1);
    }
};

extern "C" {
static int l_list_push(lua_State *L) { // Push elements from LUA
    assert(lua_gettop(L) == 2); // check that the number of args is exactly 2
    std::list<int> **ud = static_cast<std::list<int> **>(luaL_checkudata(L, 1,
                                                                         "ListMT")); // first arg is the list
    int v = luaL_checkinteger(L,
                              2); // seconds argument is the integer to be pushed to the std::list<int>
    (*ud)->push_back(v); // perform the push on C++ object through the pointer stored in user data
    return 0; // we return 0 values in the lua stack
}
static int l_list_pop(lua_State *L) {
    assert(lua_gettop(L) == 1); // check that the number of args is exactly 1
    std::list<int> **ud = static_cast<std::list<int> **>(luaL_checkudata(L, 1,
                                                                         "ListMT")); // first arg is the userdata
    if ((*ud)->empty()) {
        lua_pushnil(L);
        return 1; // if list is empty the function will return nil
    }
    lua_pushnumber(L, (*ud)->front()); // push the value to pop in the lua stack
    // it will be the return value of the function in lua
    (*ud)->pop_front(); // remove the value from the list
    return 1; //we return 1 value in the stack
}

static int sub_print(lua_State *L){
    luaB_dumpStack(L);
    ACB_Test** test = static_cast<ACB_Test **>(luaL_checkudata(L, 1, "ACB_Test"));

    //get field name.
    int result = lua_getfield(L, 1, "classname");
    const char* name = lua_tostring(L, -1);
    lua_pop(L, -1);
    ext_getLuaPrint()("class name is ",50, 0);
    ext_getLuaPrint()(const_cast<char *>(name), 50, 1);

    (*test)->print();
    return 0;
}

static int sub_gc(lua_State *L){
    ACB_Test** test = static_cast<ACB_Test **>(luaL_checkudata(L, 1, "ACB_Test"));
    delete(*test);
    return 0;
}

static int l_new(lua_State *L) {
    luaB_dumpStack(L);
    if(luaL_newmetatable(L, "ACB_Test")) {// ==0 means exists
       // luaB_dumpStack(L); // userdata tab

        lua_pushvalue(L, -1);
        lua_setfield(L, -2, "__index"); // xx .__index = xx. and pop stack
        //luaB_dumpStack(L);

        lua_pushcfunction(L, sub_print);
        lua_setfield(L, -2, "print");
        //luaB_dumpStack(L);

        lua_pushcfunction(L, sub_gc);
        lua_setfield(L, -2, "__gc");
       // luaB_dumpStack(L);

        lua_pushstring(L, "Hello Java");
        lua_setfield(L, -2, "classname");
       // luaB_dumpStack(L);
    }
    ACB_Test ** ud = static_cast<ACB_Test **>(lua_newuserdata(L, sizeof(ACB_Test*)));
    *ud = new ACB_Test();

    luaL_setmetatable(L, "ACB_Test");
    return 1;// must return 1. to make lua yield.
}
}

class Main {
public:
    Main(lua_State *L);

    ~Main();

    void run(const char *script);

    void runScript(const char *script);
    /* data */
protected:
    lua_State *L;
    std::list<int> theList;

    void registerListType();
};

Main::Main(lua_State *L) {
    this->L = L;
   /* L = luaL_newstate();
    luaL_openlibs(L);*/
}

Main::~Main() {
   // lua_close(L);
}

void Main::runScript(const char *script) {
    lua_settop(L, 0); //empty the lua stack
    if (luaL_dostring(L, script)) {
        const char* msg = lua_tostring(L, -1);
        ext_getLuaPrint()("runScript result: ", 50, 0);
        ext_getLuaPrint()(const_cast<char *>(msg), 50, 1);
       // fprintf(stderr, "error: %s\n", lua_tostring(L, -1));
        lua_pop(L, 1);
        exit(1);
    }
    assert(lua_gettop(L) == 0); //empty the lua stack
}

void Main::registerListType() {
    ext_getLuaPrint()("start registerListType for Lua.", 50, 1);
   // std::cout << "Set the list object in lua" << std::endl;
    luaL_newmetatable(L, "ListMT");

    lua_pushvalue(L, -1);
   // luaB_dumpStack(L);//tab tab
    lua_setfield(L, -2, "__index"); // ListMT .__index = ListMT. and pop stack
   // luaB_dumpStack(L);//tab

    lua_pushcfunction(L, l_list_push);
    lua_setfield(L, -2, "push"); // push in lua will call l_list_push in C++
    lua_pushcfunction(L, l_list_pop);
    lua_setfield(L, -2, "pop"); // pop in lua will call l_list_pop in C++
    lua_pushcfunction(L, l_new);
    lua_setfield(L, -2, "new");
}

void Main::run(const char *script) {
    for (unsigned int i = 0; i < 10; i++) // add some input data to the list
        theList.push_back(i * 100);
    registerListType();

    ext_getLuaPrint()("start create userdata for 'ListMT'", 50, 1);
    std::list<int> **ud = static_cast<std::list<int> **>(
            lua_newuserdata(L, sizeof(std::list<int> *)));
    *(ud) = &theList;

    //luaB_dumpStack(L);
    luaL_setmetatable(L, "ListMT"); // set userdata metatable
   // luaB_dumpStack(L);
    lua_setglobal(L, "the_list"); // the_list in lua points to the new userdata. lua can direct access it

    runScript(script);

    std::stringstream ios;
    while (!theList.empty()) { // read the data that lua left in the list
        ios << "from C++: pop value " << theList.front();
        ext_getLuaPrint()(const_cast<char *>(ios.str().c_str()), 50, 1);
        ios.str("");
        theList.pop_front();
    }
}

void testAccessCppObjInLua(lua_State* L,const char* script) {
    Main m(L);
    m.run(script);
}
