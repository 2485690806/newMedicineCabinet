package leesche.smartrecycling.base.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;

import org.greenrobot.greendao.database.Database;

import leesche.smartrecycling.base.dao.BarCodeInfoDao;
import leesche.smartrecycling.base.dao.DaoMaster;
import leesche.smartrecycling.base.dao.DeliveryRecordEntityDao;
import leesche.smartrecycling.base.dao.MonitorImgEntityDao;
import leesche.smartrecycling.base.dao.PocEntityDao;
import leesche.smartrecycling.base.dao.UserLoginStyleEntityDao;
import leesche.smartrecycling.base.dao.WriteOffEntityDao;

public class ProductOpenHelper extends DaoMaster.DevOpenHelper {

    public ProductOpenHelper(Context context, String name) {
        super(context, name);
    }

    public ProductOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                    @Override
                    public void onCreateAllTables(Database db, boolean ifNotExists) {
                        DaoMaster.createAllTables(db, ifNotExists);
                    }

                    @Override
                    public void onDropAllTables(Database db, boolean ifExists) {
                        DaoMaster.dropAllTables(db, ifExists);
                    }
                }, DeliveryRecordEntityDao.class,
                MonitorImgEntityDao.class,
                UserLoginStyleEntityDao.class,
                WriteOffEntityDao.class,
                BarCodeInfoDao.class,
                PocEntityDao.class);
//        super.onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DaoMaster.dropAllTables(this.wrap(db), true);
        onCreate(this.wrap(db));
    }
}
