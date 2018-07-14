
package com.agmbat.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.agmbat.log.Log;

import android.os.FileObserver;

public class RecursiveFileObserver extends FileObserver {

    public interface OnEventListener {
        public void onEvent(int event, String path);
    }

    // 可以监听的事件类型：
    // ACCESS ： 即文件被访问
    // MODIFY ： 文件被修改
    // ATTRIB ： 文件属性被修改，如 chmod、chown、touch 等
    // CLOSE_WRITE ： 可写文件被 close
    // CLOSE_NOWRITE ： 不可写文件被 close
    // OPEN ： 文件被 open
    // MOVED_FROM ： 文件被移走，如 mv
    // MOVED_TO ： 文件被移来，如 mv、cp
    // CREATE ： 创建新文件
    // DELETE ： 文件被删除，如 rm
    // DELETE_SELF ： 自删除，即一个可执行文件在执行时删除自己
    // MOVE_SELF ： 自移动，即一个可执行文件在执行时移动自己
    // CLOSE ： 文件被关闭，等同于(IN_CLOSE_WRITE | IN_CLOSE_NOWRITE)
    // ALL_EVENTS ： 包括上面的所有事件

    private static final String TAG = "RecursiveFileObserver";

    private static final int CREATE2 = 0x40000100;
    private static final int DELETE2 = 0x40000200;

    private List<SingleFileObserver> mObservers;
    private String mPath;
    private int mMask;

    private OnEventListener mEventListener;

    private static int mask(int mask) {
        return mask | FileObserver.CREATE | FileObserver.DELETE | CREATE2 | DELETE2;
    }

    public RecursiveFileObserver(String path) {
        this(path, ALL_EVENTS);
    }

    public RecursiveFileObserver(String path, int mask) {
        super(path, mask(mask));
        mPath = path;
        mMask = mask(mask);
    }

    public void setOnEventListener(OnEventListener l) {
        mEventListener = l;
    }

    private OnEventListener mChildEventListener = new OnEventListener() {

        @Override
        public void onEvent(int event, String path) {
            RecursiveFileObserver.this.onEvent(event, path);
        }
    };

    @Override
    public void startWatching() {
        if (mObservers != null) {
            return;
        }
        mObservers = new ArrayList<SingleFileObserver>();
        Stack<File> stack = new Stack<File>();
        stack.push(new File(mPath));

        while (!stack.isEmpty()) {
            File parent = stack.pop();
            mObservers.add(new SingleFileObserver(parent.getAbsolutePath(), mMask,
                    mChildEventListener));
            File[] files = parent.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory() && !f.getName().equals(".") && !f.getName().equals("..")) {
                        stack.push(f);
                    }
                }
            }
        }

        for (int i = 0; i < mObservers.size(); i++) {
            SingleFileObserver sfo = mObservers.get(i);
            sfo.startWatching();
        }
    };

    @Override
    public void stopWatching() {
        if (mObservers == null) {
            return;
        }
        for (int i = 0; i < mObservers.size(); i++) {
            SingleFileObserver sfo = mObservers.get(i);
            sfo.stopWatching();
        }
        mObservers.clear();
        mObservers = null;
    };

    @Override
    public final void onEvent(int event, String path) {
        Log.v(TAG, "onEvent(" + event + " : " + path);
        int newEvent = event;
        switch (event) {
            case FileObserver.ACCESS:
                Log.v(TAG, "ACCESS: " + path);
                break;
            case FileObserver.ATTRIB:
                Log.v(TAG, "ATTRIB: " + path);
                break;
            case FileObserver.CLOSE_NOWRITE:
                Log.v(TAG, "CLOSE_NOWRITE: " + path);
                break;
            case FileObserver.CLOSE_WRITE:
                Log.v(TAG, "CLOSE_WRITE: " + path);
                break;
            case FileObserver.CREATE:
            case CREATE2:
                Log.v(TAG, "CREATE: " + path);
                File file = new File(path);
                if (file.exists() && file.isDirectory()) {
                    SingleFileObserver sfo = new SingleFileObserver(path, mMask,
                            mChildEventListener);
                    mObservers.add(sfo);
                    sfo.startWatching();
                }
                newEvent = FileObserver.CREATE;
                break;
            case FileObserver.DELETE:
            case DELETE2:
                Log.v(TAG, "DELETE: " + path);
                SingleFileObserver observer = getFileObserver(path);
                if (observer != null) {
                    observer.stopWatching();
                    mObservers.remove(observer);
                }
                newEvent = FileObserver.DELETE;
                break;
            case FileObserver.DELETE_SELF:
                Log.v(TAG, "DELETE_SELF: " + path);
                break;
            case FileObserver.MODIFY:
                Log.v(TAG, "MODIFY: " + path);
                break;
            case FileObserver.MOVE_SELF:
                Log.v(TAG, "MOVE_SELF: " + path);
                break;
            case FileObserver.MOVED_FROM:
                Log.v(TAG, "MOVED_FROM: " + path);
                break;
            case FileObserver.MOVED_TO:
                Log.v(TAG, "MOVED_TO: " + path);
                break;
            case FileObserver.OPEN:
                Log.v(TAG, "OPEN: " + path);
                break;
            default:
                Log.v(TAG, "DEFAULT(" + event + " : " + path);
                break;
        }
        if (mEventListener != null) {
            mEventListener.onEvent(newEvent, path);
        }

    }

    private SingleFileObserver getFileObserver(String path) {
        for (SingleFileObserver observer : mObservers) {
            if (observer.mPath.equals(path)) {
                return observer;
            }
        }
        return null;
    }

    /**
     * Monitor single directory and dispatch all events to its parent, with full
     * path.
     */
    static class SingleFileObserver extends FileObserver {

        private String mPath;
        private OnEventListener mListener;

        public SingleFileObserver(String path, int mask, OnEventListener l) {
            super(path, mask);
            mPath = path;
            mListener = l;
        }

        @Override
        public void onEvent(int event, String path) {
            if (path != null) {
                File newFile = new File(mPath, path);
                String newPath = newFile.getAbsolutePath();
                mListener.onEvent(event, newPath);
            }
        }
    }
}
