package com.agmbat.imsdk.db;

import android.content.Context;
import android.text.TextUtils;

import com.agmbat.android.AppResources;
import com.agmbat.db.DbException;
import com.agmbat.db.DbManager;
import com.agmbat.db.DbManagerFactory;
import com.agmbat.file.FileUtils;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.imsdk.asmack.roster.FriendRequest;
import com.agmbat.log.Debug;
import com.agmbat.sqlite.SqliteDbConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户数据库
 */
public class MeetDatabase {

    /**
     * 数据库名称
     */
    private static final String DB_NAME = "meet.db";

    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 1;


    private static Map<String, SqliteDbConfig> sDbMap = new HashMap<>();


    private static final MeetDatabase INSTANCE = new MeetDatabase();

    /**
     * 创建对应的config
     *
     * @param user
     * @return
     */
    private static SqliteDbConfig initDbConfig(String user) {
        SqliteDbConfig daoConfig = new SqliteDbConfig();
        daoConfig.setDbName(DB_NAME);
        File dir = AppResources.getAppContext().getDir("sqldb", Context.MODE_PRIVATE);
        File userDir = new File(dir, user);
        FileUtils.ensureDir(userDir);
        daoConfig.setDbDir(userDir);
        daoConfig.setDbVersion(DB_VERSION);
        daoConfig.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                // TODO: ...
                // db.addColumn(...);
                // db.dropTable(...);
                // ...
            }
        });
        return daoConfig;
    }


    public static MeetDatabase getInstance() {
        return INSTANCE;
    }

    private MeetDatabase() {
    }

    /**
     * 获取数据库
     *
     * @return
     */
    public DbManager getDatabase() {
        String loginUser = XMPPManager.getInstance().getConnectionUserName();
        if (TextUtils.isEmpty(loginUser)) {
            return null;
        }
        SqliteDbConfig config = sDbMap.get(loginUser);
        if (config == null) {
            config = initDbConfig(loginUser);
            sDbMap.put(loginUser, config);
        }
        return DbManagerFactory.getInstance(config);
    }

    /**
     * 保存好友申请列表
     *
     * @param contactInfo
     */
    public void saveFriendRequest(ContactInfo contactInfo) {
        FriendRequest request = new FriendRequest(contactInfo);
        DbManager db = getDatabase();
        if (db == null) {
            Debug.printStackTrace();
            return;
        }
        try {
            FriendRequest exist = db.selector(FriendRequest.class)
                    .where("jid", "in", new String[]{request.getJid()})
                    .findFirst();
            if (exist == null) {
                db.save(request);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除好友申请列表中的item
     *
     * @param contactInfo
     */
    public void deleteFriendRequest(ContactInfo contactInfo) {
        DbManager db = getDatabase();
        if (db == null) {
            Debug.printStackTrace();
            return;
        }
        try {
            FriendRequest exist = db.selector(FriendRequest.class)
                    .where("jid", "in", new String[]{contactInfo.getBareJid()})
                    .findFirst();
            if (exist != null) {
                db.delete(exist);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取好友申请信息
     *
     * @return
     */
    public List<ContactInfo> getFriendRequestList() {
        List<ContactInfo> list = new ArrayList<>();
        DbManager db = getDatabase();
        if (db == null) {
            Debug.printStackTrace();
            return list;
        }
        try {
            List<FriendRequest> requestList = db.selector(FriendRequest.class).findAll();
            if (requestList != null) {
                for (FriendRequest request : requestList) {
                    list.add(request.toContactInfo());
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }


}
