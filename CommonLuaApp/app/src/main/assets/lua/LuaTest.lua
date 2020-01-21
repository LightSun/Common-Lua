--
--- travel tables all field. ignored functions
local function travelTable(tab , func)
    if not tab then
        return nil
    end
    -- not table
    if (type(tab) ~= "table") then
        error("tab is not table")
    end

    local index;
    while true do
        -- travel all member.
        local key , val = next(tab , index);
        if (not val) then
            break
        end
       --[[ if (type(val) == "function") then
            -- ignored
        else]]
            local shouldBreak = func(key , val)
            if (shouldBreak) then
                break
            end
        --end
        index = key
    end
end

local function func(key, val)
    print(tostring(key), tostring(val))
end

dumpStack()
local ff = FooWrapper(3)
dumpStack()
print("type ff = ", type(ff)) -- table
local v = ff:add(1, 4)        -- v = 5
dumpStack()

travelTable(ff, func)
print("after add v = ",v);
ff:setV(6)

print("after set v = ",ff:getV());

local ff2 = FooWrapper(4)
print(ff:getV())       -- v = 6
print(ff2:getV())     -- v = 4

--[[  for travelTable:
add	    function: 0x776e252da0
getV	function: 0x776e252e00
setV    function: 0x776e252dd0
0	userdata: 0x776e252d98
 -- ]]--