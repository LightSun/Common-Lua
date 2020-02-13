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
    local nextIndex;
    if lib.isUpper(arr[1]) then
        classname = andEnv.getClassName(arr[1])
        nextIndex = 2;
    else
        local name = arr[1];
        for i = 2, #arr
        do
            name = name..'.'..arr[i];
            if lib.isUpper(arr[i]) then
                classname = name;
                nextIndex = i + 1;
                break
            end
        end
    end

    if(nextIndex == #arr) then
        error("for refer android R. the name must not only be class name.")
    end
    local tab = andEnv.pushClass(classname);
    return tab.getField(tab, arr[#arr])
end

--print("R.layout.main = ", m.ref('R.layout.main'))
return m;