package com.heaven7.lua;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;

import com.heaven7.core.util.Logger;
import com.heaven7.java.base.util.FileUtils;
import com.heaven7.java.base.util.IOUtils;
import com.heaven7.java.lua.LuaSearcher;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaWrapper;
import com.heaven7.java.pc.schedulers.Schedulers;
import com.heaven7.lua.test.AndroidEnv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by heaven7 on 2019/8/13.
 */
public final class Luaer {

    private static final String TAG = "Luaer";
    private static final String LUA_DIR = Environment.getExternalStorageDirectory() + "/vida/lua";
    private static final String LUA_PARENT_DIR = Environment.getExternalStorageDirectory() + "/vida";

    private final Context context;
    private LuaState mLuaState;

    public Luaer(Context context) {
        this.context = context;
    }

    public void initLuaState(){
        if(mLuaState != null) throw new IllegalStateException();
        mLuaState = new LuaState();
        AndroidEnv.initialize(context, this);
    }
    public void destroyLuaState(){
        if(mLuaState != null){
            mLuaState.destroyNative();
            mLuaState = null;
        }
    }
    public LuaState getLuaState(){
        return mLuaState;
    }

    public Context getApplicationContext(){
        return context.getApplicationContext();
    }
    public File getFilesDir(){
        return context.getFilesDir();
    }
    public AssetManager getAssets(){
        return context.getAssets();
    }
    public Resources getResources(){
        return context.getResources();
    }

    public String doFile(String relativePath){
        String path = LUA_DIR + "/" + relativePath;
        if(!new File(path).exists()){
            return "can't find lua file,  path = " + path;
        }
        if(mLuaState.doFile(path) != 0){
            String msg = mLuaState.toString(-1);
            mLuaState.pop(1);
            return msg;
        }
        return null;
    }
    public String loadFile(String relativePath){
        String path = LUA_DIR + "/" + relativePath;
        if(!new File(path).exists()){
            return "can't find lua file,  path = " + path;
        }
        if(mLuaState.loadFile(path) != 0){
            String msg = mLuaState.toString(-1);
            mLuaState.pop(1);
            return msg;
        }
        return null;
    }

    public void initEnv(){
        FileUtils.deleteDir(new File(LUA_DIR));

        final Map<String, Boolean> mCMap = new HashMap<>();
        mCMap.put("cjson", true);
        LuaWrapper.getDefault().registerLuaSearcher(new LuaSearcher() {
            @Override
            public String getLuaFilepath(String module) {
                if(mCMap.containsKey(module)){
                    return null;
                }
                Logger.d(TAG, "getLuaFilepath", "module = " + module);
                return LUA_DIR + "/" + module + ".lua";
            }
            @Override
            public String getClibFilepath(String module) {
                Logger.d(TAG, "getClibFilepath", "module = " + module);
                //return LUA_DIR + "/lib" + module + ".so";
                return new File(getFilesDir(), "libcjson.so").getPath();
            }
        });
        Schedulers.io().newWorker().schedule(new Runnable() {
            @Override
            public void run() {
                AssetsFileCopyUtils.copyAll(getApplicationContext(), "lua", LUA_PARENT_DIR);
                File dst = new File(getFilesDir(), "libcjson.so");
                System.out.println("libcjson: path is " + dst.getPath());
                if(dst.exists()){
                    System.out.println("libcjson load ok(already copied).");
                    return;
                }
                //lua load libcjson .the c json can't be load on sdcard.
                try {
                    InputStream in = getAssets().open("clua/libcjson.so");
                    OutputStream out = new FileOutputStream(dst);
                    IOUtils.copyLarge(in, out);
                    IOUtils.closeQuietly(in);
                    IOUtils.closeQuietly(out);
                    System.out.println("libcjson load ok.");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Logger.d(TAG, "run", "lua script copy done");
            }
        });
    }
    public static byte[] intToBytes(int val){
       /* ByteBuffer buffer = ByteBuffer.allocateDirect(4)
                .order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(0);
        buffer.putInt(val);
        return buffer.array(); //11个字节 .*/
       return ByteConvertUtil.getBytes(val, false);
    }
    public static void writeToFile(String file, byte[] enResult, int rawLength) {
        byte[] bytes1 = intToBytes(enResult.length);
        byte[] bytes2 = intToBytes(rawLength);
        Logger.d(TAG, "writeToFile", Arrays.toString(bytes1));
        Logger.d(TAG, "writeToFile", Arrays.toString(bytes2));
        FileOutputStream dos = null;
        try {
            File f = new File(file);
            if(f.exists()){
                f.delete();
            }
            dos = new FileOutputStream(file);
            dos.write("l:u:a:nat".getBytes(Charset.defaultCharset()));
            dos.write(bytes1);
            dos.write(bytes2);
            dos.write(enResult);
            dos.flush();
            Logger.i(TAG, "writeToFile", "write total len = " + (9 + 8 + enResult.length)); //73 - 17
            Logger.i(TAG, "writeToFile", "rawLength = " + rawLength);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            IOUtils.closeQuietly(dos);
        }
    }

    public static String readStringWithLine(Reader r) throws IOException {
        BufferedReader br = r instanceof BufferedReader ? (BufferedReader) r : new BufferedReader(r);

        String str;
        StringBuilder sb = new StringBuilder();
        while ((str = br.readLine()) != null) {
            sb.append(str).append("\n");
        }
        return sb.toString();
    }
    public void loadLuaAssets(String file) {
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(getAssets().open(file));
            int state = mLuaState.doString(Luaer.readStringWithLine(in));
            Logger.i(TAG, "loadLua", "state = " + state + ", " + mLuaState.toString(-1));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
    public String loadLuaAssetsAsString(String file) {
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(getAssets().open(file));
            return Luaer.readStringWithLine(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
    public void loadLuaRaw(int file) {
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(getResources().openRawResource(file));
            int state = mLuaState.doString(Luaer.readStringWithLine(in));
            String msg = mLuaState.toString(-1);
            Logger.i(TAG, "loadLua", "state = " + state + " ,msg = " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
}
