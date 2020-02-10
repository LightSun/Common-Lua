local m = {}

local lib = require("Strings")
local andEnv = require("AndroidEnv")

-- R.id.xx / R.drawable.xx
function m.ref(str)
    local arr = lib.split(str, ".");

    print("arr = ", #arr)
    -- 1, get the full name of entry class
    -- 2, push. sub class.
    -- 3. getField
    local classname;
    if lib.isUpper(arr[1]) then
        classname = andEnv.getClassName(arr[1])
    else
        classname = arr[1];
    end

    for i = 2, #arr - 1, 1
    do
        classname = classname..'$'..arr[i]
    end
    local tab = andEnv.pushClass(classname);
    return tab.getField(tab, arr[#arr])
end

print("R.layout.main = ", m.ref('R.layout.main'))
return m;