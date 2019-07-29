--
-- LUA_TSTRING = 4
local bridge = LuaBridge("com.heaven7.test", 0);
local result = bridge:call("size", 0);
print(result);
result = bridge:call("size2", "hello bridge_call", 1);
print(result);
