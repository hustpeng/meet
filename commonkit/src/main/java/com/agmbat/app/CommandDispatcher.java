/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * Android Common Kit
 *
 * @author mayimchen
 * @since 2016-07-23
 */
package com.agmbat.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.agmbat.android.SystemManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CommandDispatcher {

    private static CommandDispatcher sInstance;
    private final Context mContext;
    private final Map<String, Command> mMap;

    private CommandDispatcher() {
        mContext = SystemManager.getContext();
        mMap = new HashMap<String, Command>();
    }

    public static CommandDispatcher getInstance() {
        if (sInstance == null) {
            sInstance = new CommandDispatcher();
        }
        return sInstance;
    }

    public static void dispatch(String cmd) {
        Command command = getInstance().getCommand(cmd);
        if (command != null) {
            command.handleCommand();
        }
    }

    public void addCommand(Command command) {
        mMap.put(command.getCommand(), command);
    }

    private Command getCommand(String cmd) {
        return mMap.get(cmd);
    }

    public void registerCmdReceiver() {
        mContext.registerReceiver(new CommandReceiver(), getFilter());
    }

    private IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        Iterator<String> it = mMap.keySet().iterator();
        while (it.hasNext()) {
            filter.addAction(it.next());
        }
        return filter;
    }

    public interface Command {

        public String getCommand();

        public void handleCommand();

    }

    public static class CommandReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            CommandDispatcher.dispatch(action);
        }
    }

}
