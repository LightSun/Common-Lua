--
--int/byte/short/long float/double/boolean/

local a = {'a', 'b', 'c'}

local bridge = LuaBridge("com.heaven7.test", 0);
print("type bridge = ", type(bridge)); --table
local result = bridge:call("size", 0);
print(result);
result = bridge:call("size2", "hello bridge_call", 1);
print(result);

--bridge:call("size3", a, 1);
