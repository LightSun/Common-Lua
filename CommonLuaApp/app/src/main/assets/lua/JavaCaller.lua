
local m = {}
-- make call from ' br.call(method, args..., size)' to 'br.method(...args)'
function m.wrap(tab)
    assert(type(tab)=="table", "must be table")
    local self = {};
    self.tab = tab;
    local meta = {
        __index = function(t, k)
            -- k is the method name
            local rawTab = t.tab;
            local fun = function(...)
                local args = {...}
                local len = #args
                table.insert(args, len + 1, len)
                if not type(rawTab.call) == "function" then
                    error("the raw table which is wrapped must have a function 'call'.")
                end
                return rawTab.call(k, table.unpack(args));
            end
            return fun;
        end
    };
    setmetatable(self, meta)
    return self;
end

return m;