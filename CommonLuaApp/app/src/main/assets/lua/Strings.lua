
local m = {}

function m.replaceAll(str, pattern, repl, count)
    local re, _= string.gsub(str, pattern, repl, count)
    return re;
end

function m.trim(str)
    if str == nil then
        return nil, "the string parameter is nil"
    end
    str = string.gsub(str, " ", "")
    return str
end

function m.count(str, substr, from, to)
    if str == nil or substr == nil then
        return nil, "the string or the sub-string parameter is nil"
    end
    from = from or 1
    if to == nil or to > string.len(str) then
        to = string.len(str)
    end
    local str_tmp = string.sub(str, from ,to)
    local _, n = string.gsub(str, substr, '')
    return n
end
function m.startsWidth(str, substr)
    if str == nil or substr == nil then
        return nil, "the string or the sub-stirng parameter is nil"
    end
    if string.find(str, substr) ~= 1 then
        return false
    else
        return true
    end
end

function m.endsWidth(str, substr)
    if str == nil or substr == nil then
        return nil, "the string or the sub-string parameter is nil"
    end
    local str_tmp = string.reverse(str)
    local substr_tmp = string.reverse(substr)
    if string.find(str_tmp, substr_tmp) ~= 1 then
        return false
    else
        return true
    end
end


function m.split(str, ch)
    local find, sub, insert = string.find, string.sub, table.insert
    local res = {}
    local start, start_pos, end_pos = 1, 1, 1
    while true do
        start_pos, end_pos = find(str, ch, start, true)
        if not start_pos then
            break
        end
        insert(res, sub(str, start, start_pos - 1))
        start = end_pos + 1
    end
    insert(res, sub(str,start))
    return res
end

function m.bytes2Str(bytes)
    --string.char(unpack(bytes))
    local str = ""
    for i = 1 , #bytes do
        str = str..string.char(bytes[i])
    end
    return str;
end

-- judge the first char is upper or not
function m.isUpper(ch)
    local val = ch;
    if(type(ch) == "string") then
        val = string.byte(ch);
    end
    return val >= string.byte('A') and val <= string.byte('Z')
end
return m;