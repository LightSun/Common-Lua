local function entries(arg) -- iterator
    return function()
        return arg:pop();
    end
end

for i in entries(the_list) do
    print("From LUA:  ", i, "\n")
end

for i = 1, 10 do
    the_list:push(50 + i * 100);
end

local acb = the_list:new();
print("Access_cpp_object:", type(acb))
acb:print();