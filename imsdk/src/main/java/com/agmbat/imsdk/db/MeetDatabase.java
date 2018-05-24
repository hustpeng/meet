package com.agmbat.imsdk.db;

import android.content.Context;

import com.agmbat.android.AppResources;
import com.agmbat.db.DbException;
import com.agmbat.db.DbManager;
import com.agmbat.db.DbManagerFactory;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.sqlite.SqliteDbConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MeetDatabase {

    private static final String DB_NAME = "meet.db";
    private static final int DB_VERSION = 1;

    private static final SqliteDbConfig sConfig;

    private static SqliteDbConfig initDbConfig() {
        SqliteDbConfig daoConfig = new SqliteDbConfig();
        daoConfig.setDbName(DB_NAME);
        File dir = AppResources.getAppContext().getDir("sqldb", Context.MODE_PRIVATE);
        daoConfig.setDbDir(dir);
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

    static {
        sConfig = initDbConfig();
    }

    private static final MeetDatabase INSTANCE = new MeetDatabase();

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
        return DbManagerFactory.getInstance(sConfig);
    }

    /**
     * 保存好友申请列表
     *
     * @param contactInfo
     */
    public void saveFriendRequest(ContactInfo contactInfo) {
        FriendRequest request = new FriendRequest(contactInfo);
        DbManager db = DbManagerFactory.getInstance(sConfig);
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
        DbManager db = DbManagerFactory.getInstance(sConfig);
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
        DbManager db = DbManagerFactory.getInstance(sConfig);
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
