package leesche.smartrecycling.base.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import leesche.smartrecycling.base.dao.DaoMaster;
import leesche.smartrecycling.base.dao.DaoSession;
import leesche.smartrecycling.base.dao.GreenDaoContext;

/**
 * sqlite 数据库操作类
 */
public class DatabaseUtil {

    public static DatabaseUtil databaseUtil;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    public static DatabaseUtil getInstance() {
        if (databaseUtil == null) {
            databaseUtil = new DatabaseUtil();
        }
        return databaseUtil;
    }

    /*
     * 设置greenDAO
     */
    public void setDatabase(Context context) {
        mHelper = new ProductOpenHelper(new GreenDaoContext(context), "recycling-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

}
