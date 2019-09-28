require("luaui")

function onStartup(dir)
    local paint = Sk.newPaint();
    paint:setColor{a=1, r=1, g=0, b=0};
    if true then
        print("---- before newDocumentPDF")
        local doc = Sk.newDocumentPDF(dir..'/test.pdf');
        print("---- before beginPage")
        local canvas = doc:beginPage(72*8.5, 72*11);
        print("---- end beginPage")
        canvas:drawText("Hello Lua", 300, 300, paint);
        doc:close();
        doc = nil;
    end
end

onStartup("/sdcard")