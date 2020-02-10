
local m;
if(_G.ANDROID_ENV == nil) then
    m = {}
    _G.ANDROID_ENV = m;
else
   m = _G.ANDROID_ENV
end

function m.SET_ANDROID_ENV(map)
    -- br.call(method, args..., size)
    print("setUpAndroidEnv", map)
    m._package = map.call("getPackage", 0)
    m._getClassName = map.call("getClassName", 0) --func
    m._pushClass = map.call("pushClass", 0) -- func

    print("getPackage", m._package);
    print("getClassName", m._getClassName);
    print("pushClass", m._pushClass);
end

function m.getPackage()
    return m.package;
end

function m.getClassName(s)
    return m._getClassName(s);
end
-- push class and return tab
function m.pushClass(cn)
    return m._pushClass(cn);
end

--m["SET_ANDROID_ENV"] = SET_ANDROID_ENV;
return m;