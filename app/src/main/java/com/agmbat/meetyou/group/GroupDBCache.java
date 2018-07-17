package com.agmbat.meetyou.group;

import com.agmbat.db.DbException;
import com.agmbat.db.DbManager;
import com.agmbat.imsdk.db.MeetDatabase;
import com.agmbat.log.Debug;

import java.util.ArrayList;
import java.util.List;

public class GroupDBCache {

    /**
     * 先清空原数据, 再保存新的列表
     *
     * @param groupTags
     */
    public static void saveAllGroupTags(List<GroupTag> groupTags) {
        DbManager db = MeetDatabase.getInstance().getDatabase();
        if(null == db){
            return;
        }
        try {
            db.delete(GroupTag.class);
            for (GroupTag groupTag : groupTags) {
                db.save(groupTag);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 查询数据库的list
     *
     * @return
     */
    public static List<GroupTag> getGroupTags() {
        DbManager db = MeetDatabase.getInstance().getDatabase();
        if (db == null) {
            Debug.printStackTrace();
            return new ArrayList<>();
        }
        try {
            List<GroupTag> groupTags = db.selector(GroupTag.class).findAll();
            return groupTags;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
