package leesche.smartrecycling.base.utils;

import android.text.TextUtils;

import com.leesche.logger.Logger;

import java.util.List;

import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.dao.DeliveryRecordEntityDao;
import leesche.smartrecycling.base.dao.MonitorImgEntityDao;
import leesche.smartrecycling.base.dao.PocEntityDao;
import leesche.smartrecycling.base.dao.QrCodeBindingDao;
import leesche.smartrecycling.base.dao.UserLoginStyleEntityDao;
import leesche.smartrecycling.base.dao.WriteOffEntityDao;
import leesche.smartrecycling.base.entity.CommonUserEntity;
import leesche.smartrecycling.base.entity.DeliveryRecordEntity;
import leesche.smartrecycling.base.entity.MonitorImgEntity;
import leesche.smartrecycling.base.entity.PocEntity;
import leesche.smartrecycling.base.entity.QrCodeBinding;
import leesche.smartrecycling.base.entity.UserLoginStyleEntity;
import leesche.smartrecycling.base.entity.WriteOffEntity;


public class DataSourceOperator {

    static DataSourceOperator dataSourceOperator;
    private DeliveryRecordEntityDao deliveryRecordEntityDao;
    private PocEntityDao pocEntityDao;
    private UserLoginStyleEntityDao userLoginStyleEntityDao;
    private MonitorImgEntityDao monitorImgEntityDao;
    private WriteOffEntityDao writeOffEntityDao;

    private QrCodeBindingDao qrCodeBindingDao;

    public static DataSourceOperator getInstance() {

        if (dataSourceOperator == null) {
            dataSourceOperator = new DataSourceOperator();
        }

        return dataSourceOperator;
    }

    public DeliveryRecordEntityDao getDeliveryRecordDao() {
        if (deliveryRecordEntityDao == null) {
            deliveryRecordEntityDao = DatabaseUtil.getInstance().getDaoSession().getDeliveryRecordEntityDao();
        }
        return deliveryRecordEntityDao;
    }

    /**
     * 插入用户投递记录
     *
     * @param deliveryRecordEntity
     * @return
     */
    public long insertDeliveryRecordToDb(DeliveryRecordEntity deliveryRecordEntity) {
        long insertId = getDeliveryRecordDao().insertOrReplace(deliveryRecordEntity);
//        Logger.i("【Deposit Data】 insertOrReplace: " + deliveryRecordEntity.getOrder_id());
        return insertId;
    }

    /**
     * 删除当前时间投递记录
     */
    public void deleteDeliveryRecordFromDb(DeliveryRecordEntity deliveryRecordEntity) {
        getDeliveryRecordDao().delete(deliveryRecordEntity);
    }

    public DeliveryRecordEntity queryDeliveryRecordFromDb(String orderId) {
        return getDeliveryRecordDao().queryBuilder().where(DeliveryRecordEntityDao.Properties.Order_id.eq(orderId)).unique();
    }

    public boolean queryAndDeleteDeliveryRecordFromDbByOrderId(String orderId) {
        List<DeliveryRecordEntity> deliveryRecordEntity = deliveryRecordEntityDao.queryBuilder()
                .where(DeliveryRecordEntityDao.Properties.Order_id.like("%" + orderId + "%")).list();
        if (deliveryRecordEntity != null && deliveryRecordEntity.size() > 0) {
            getDeliveryRecordDao().delete(deliveryRecordEntity.get(0));
            Logger.i("[系统]投放记录已删除: " + deliveryRecordEntity.get(0).getOrder_id());
            return true;
        }
//        Logger.e("【Deposit Record】order " + orderId + " no exist");
        return false;
    }

    public boolean queryAndDeleteDeliveryRecordFromDb(String orderId) {
        List<DeliveryRecordEntity> deliveryRecordEntity = deliveryRecordEntityDao.queryBuilder()
                .where(DeliveryRecordEntityDao.Properties.Date_time.like("%" + orderId + "%")).list();
        if (deliveryRecordEntity != null && deliveryRecordEntity.size() > 0) {
            getDeliveryRecordDao().delete(deliveryRecordEntity.get(0));
            Logger.i("[系统]投放记录已删除: " + deliveryRecordEntity.get(0).getOrder_id());
            return true;
        }
//        Logger.e("【Deposit Record】order " + orderId + " no exist");
        return false;
    }

    /**
     * 获取所有未上报的投递记录
     *
     * @return
     */
    public List<DeliveryRecordEntity> getDeliveryRecordFromDb() {
        return getInstance().getDeliveryRecordDao().queryBuilder().list();
    }

    //【查询】根据日期获取所有投放记录
    public List<DeliveryRecordEntity> getDeliveryRecordByDate(String dateStr) {
        return getDeliveryRecordDao().queryBuilder().where(
                DeliveryRecordEntityDao.Properties.Date_time.like("%" + dateStr + "%")).list();
    }

    //【查询】根据日期、上传状态获取投放记录
    public List<DeliveryRecordEntity> getDeliveryRecordByDateAndStatus(String dateStr, int status) {
        return getDeliveryRecordDao().queryBuilder().where(
                DeliveryRecordEntityDao.Properties.Date_time.like("%" + dateStr + "%"),
                DeliveryRecordEntityDao.Properties.UploadStatus.eq(status)).list();
    }

    public List<DeliveryRecordEntity> getDeliveryRecordByStatus(int status) {
        return getDeliveryRecordDao().queryBuilder().where(
                DeliveryRecordEntityDao.Properties.UploadStatus.eq(status)).list();
    }

    public List<DeliveryRecordEntity> getDeliveryRecordByDateAndStatus(String dateStr) {
        return getDeliveryRecordDao().queryBuilder().where(
                DeliveryRecordEntityDao.Properties.Date_time.like("%" + dateStr + "%"),
                DeliveryRecordEntityDao.Properties.UploadStatus.notEq(1)).list();
    }

    //【更新】投放记录上传状态
    public void modifyDepositIsExist(DeliveryRecordEntity deliveryRecordEntity) {
        DeliveryRecordEntity deliveryRecordEntityFormDb = queryDeliveryRecordFromDb(deliveryRecordEntity.getOrder_id());
        if (deliveryRecordEntityFormDb != null) {
            getDeliveryRecordDao().update(deliveryRecordEntity);
//            Logger.i("【Deposit Data】 update：" + deliveryRecordEntity);
        }
    }

    public void modifyDepositIsExist(String orderId, int status) {
        DeliveryRecordEntity deliveryRecordEntityFormDb = queryDeliveryRecordFromDb(orderId);
        if (deliveryRecordEntityFormDb != null) {
            deliveryRecordEntityFormDb.setUploadStatus(status);
            getDeliveryRecordDao().update(deliveryRecordEntityFormDb);
//            Logger.i("【Deposit Data】 update：" + deliveryRecordEntityFormDb);
        }
    }

    public DeliveryRecordEntity queryTheFirstOrder() {
        return getDeliveryRecordDao().queryBuilder().limit(1).unique();
    }

    public List<DeliveryRecordEntity> queryWeightingDataListByCondition(String date, int orderStatus) {
        return getDeliveryRecordDao().queryBuilder().where(
                DeliveryRecordEntityDao.Properties.Date_time.like("%" + date + "%"),
                DeliveryRecordEntityDao.Properties.UploadStatus.eq(orderStatus)).list();
    }

    public void deleteOver7DaysWeightData(String date, int orderStatus) {
        List<DeliveryRecordEntity> weightDataEntities = queryWeightingDataListByCondition(date, orderStatus);
        getDeliveryRecordDao().deleteInTx(weightDataEntities);
    }

    public long getDeliveryRecordCountFromDb() {
        return getDeliveryRecordDao().queryBuilder().count();
    }

    public long getDeliveryRecordCountByDate() {
        String dateStr = DateUtil.getDateStr(1);
        List<DeliveryRecordEntity> deliveryRecordEntities = getDeliveryRecordByDateAndStatus(dateStr, 0);
        if (deliveryRecordEntities != null) {
            return deliveryRecordEntities.size();
        }
        return 0;
    }

//    public DataSourceOperator getKitchenRecordDao() {
//        if (kitchenRecordEntityDao == null) {
//            kitchenRecordEntityDao = DatabaseUtil.getInstance().getDaoSession().getKitchenRecordEntityDao();
//        }
//        return dataSourceOperator;
//    }

    /**
     * 插入厨余垃圾有效投递记录
     *
     * @param kitchenRecordEntity
     * @return
     */
//    public long insertKichenRecordToDb(KitchenRecordEntity kitchenRecordEntity) {
//        long insertId = kitchenRecordEntityDao.insertOrReplace(kitchenRecordEntity);
//        return insertId;
//    }

    /**
     * 查询厨余垃圾有效投递记录是否存在
     *
     * @param user_phone
     * @return
     */
//    public boolean queryKitchenRecordIsExit(String user_phone, String date) {
//        KitchenRecordEntity _kitchenRecordEntity = kitchenRecordEntityDao.queryBuilder()
//                .where(KitchenRecordEntityDao.Properties.User_phone.eq(user_phone)
//                        , KitchenRecordEntityDao.Properties.Delivery_date.eq(date))
//                .unique();
//        return _kitchenRecordEntity != null;
//    }


    /**
     * 删除当前日期厨余垃圾有效投递记录
     */
//    public void deleteKitchenRecordByDateFromDb(String date) {
//        List<KitchenRecordEntity> kitchenRecordEntities = kitchenRecordEntityDao.queryBuilder()
//                .where(KitchenRecordEntityDao.Properties.Delivery_date.eq(date))
//                .list();
//        Log.i("KitchenRecord", kitchenRecordEntities.size() + "条");
//        kitchenRecordEntityDao.deleteInTx(kitchenRecordEntities);
//    }
    public DataSourceOperator getPocDao() {
        if (pocEntityDao == null) {
            pocEntityDao = DatabaseUtil.getInstance().getDaoSession().getPocEntityDao();
        }
        return dataSourceOperator;
    }

    /**
     * 添加投递视频记录
     *
     * @param pocEntity
     * @return
     */
    public long insertPocRecordToDb(PocEntity pocEntity) {
        PocEntity pocEntity1 = queryPocFromDb(pocEntity.getOrder_id());
        if (pocEntity1 != null) {
            pocEntity1.setStatus(pocEntity.getStatus());
            pocEntity1.setLocalVideoPaths(pocEntity.getLocalVideoPaths());
            pocEntityDao.update(pocEntity1);
            return -1;
        }
        return pocEntityDao.insertOrReplace(pocEntity);
    }

    /**
     * 更新投递视频记录
     *
     * @param pocEntity
     * @return
     */
    public void updatePocRecordToDb(PocEntity pocEntity) {
        pocEntityDao.update(pocEntity);
    }

    public void queryAndUpdatePocRecordToDab(String orderId, int status) {
        PocEntity pocEntity = queryPocFromDb(orderId);
        if (pocEntity != null) {
            pocEntity.setStatus(status);
            pocEntityDao.update(pocEntity);
        }
    }

    public List<PocEntity> queryPocRecordByStatus(int status) {
        List<PocEntity> pocEntities = pocEntityDao.queryBuilder()
                .where(PocEntityDao.Properties.Status.eq(status))
                .list();
        return pocEntities;
    }

    public void deletePocRecordEntities(List<PocEntity> pocEntities) {
        pocEntityDao.deleteInTx(pocEntities);
    }

    public void deletePocRecordEntity(PocEntity pocEntity) {
        pocEntityDao.delete(pocEntity);
    }

    /**
     * 查找视频记录
     *
     * @param orderId
     * @return
     */
    public PocEntity queryPocFromDb(String orderId) {
        List<PocEntity> pocEntities = pocEntityDao.queryBuilder()
                .where(PocEntityDao.Properties.Order_id.eq(orderId))
                .list();
        if (pocEntities != null && pocEntities.size() > 0) {
            return pocEntities.get(0);
        }
        return null;
    }

    public DataSourceOperator getUserLoginStyleDao() {
        if (userLoginStyleEntityDao == null) {
            userLoginStyleEntityDao = DatabaseUtil.getInstance().getDaoSession().getUserLoginStyleEntityDao();
        }
        return dataSourceOperator;
    }

    /**
     * 添加用户登录方式记录
     */
    public long insertUserLoginStyleToDb(UserLoginStyleEntity userLoginStyleEntity) {
        long insertId = userLoginStyleEntityDao.insertOrReplace(userLoginStyleEntity);
        return insertId;
    }

    /**
     * 查询用户登录方式记录
     */
    public List<UserLoginStyleEntity> queryUserLoginStyleFormDb() {
        return userLoginStyleEntityDao.queryBuilder().list();
    }

    /**
     * 更新用户登录方式记录
     */
    public void updateUserLoginStyleToDb(UserLoginStyleEntity userLoginStyleEntity) {
        getUserLoginStyleDao().userLoginStyleEntityDao.update(userLoginStyleEntity);
    }

    /**
     * 通过手机号码查询用户是否成功登录过
     */
    public UserLoginStyleEntity queryUserIsExistByPhone(String user_phone) {
        if (TextUtils.isEmpty(user_phone)) return null;
        List<UserLoginStyleEntity> userLoginStyleEntitys = getUserLoginStyleDao().userLoginStyleEntityDao.queryBuilder()
                .where(UserLoginStyleEntityDao.Properties.User_phone.eq(user_phone))
                .list();
        if (userLoginStyleEntitys.size() == 0) return null;
        return userLoginStyleEntitys.get(0);
    }

    public UserLoginStyleEntity queryUserIsExistByPhone2(String user_phone) {
        if (TextUtils.isEmpty(user_phone)) return null;
        List<UserLoginStyleEntity> userLoginStyleEntitys = getUserLoginStyleDao().userLoginStyleEntityDao.queryBuilder()
                .where(UserLoginStyleEntityDao.Properties.User_phone.eq(user_phone),
                        UserLoginStyleEntityDao.Properties.User_type.eq(2001))
                .list();
        if (userLoginStyleEntitys.size() == 0) return null;
        return userLoginStyleEntitys.get(0);
    }

    public UserLoginStyleEntity queryUserIsExistByPhone3(String user_phone) {
        if (TextUtils.isEmpty(user_phone)) return null;
        List<UserLoginStyleEntity> userLoginStyleEntitys = getUserLoginStyleDao().userLoginStyleEntityDao.queryBuilder()
                .where(UserLoginStyleEntityDao.Properties.User_phone.eq(user_phone))
                .list();
        if (userLoginStyleEntitys.size() == 0) return null;
        return userLoginStyleEntitys.get(0);
    }

    /**
     * 通过IC卡查询用户是否成功登录过
     */
    public UserLoginStyleEntity queryUserIsExistByIc(String ic_card_num) {
        if (TextUtils.isEmpty(ic_card_num)) return null;
        List<UserLoginStyleEntity> userLoginStyleEntitys = getUserLoginStyleDao().userLoginStyleEntityDao.queryBuilder()
                .where(UserLoginStyleEntityDao.Properties.Ic_card_num.eq(ic_card_num))
                .list();
        if (userLoginStyleEntitys.size() == 0) return null;
        return userLoginStyleEntitys.get(0);
    }

    /**
     * 通过二维码查询用户是否成功登录过
     */
    public UserLoginStyleEntity queryUserIsExistByQrCode(String qr_code) {
        if (TextUtils.isEmpty(qr_code)) return null;
        List<UserLoginStyleEntity> userLoginStyleEntitys = getUserLoginStyleDao().userLoginStyleEntityDao.queryBuilder()
                .where(UserLoginStyleEntityDao.Properties.Qr_code.eq(qr_code))
                .list();
        if (userLoginStyleEntitys.size() == 0) return null;
        return userLoginStyleEntitys.get(0);
    }

    /**
     * 通过门牌号查询用户是否成功登录过
     */
    public UserLoginStyleEntity queryUserIsExistByDoorPlateNum(String door_plate_num) {
        UserLoginStyleEntity userLoginStyleEntity = getUserLoginStyleDao().userLoginStyleEntityDao.queryBuilder()
                .where(UserLoginStyleEntityDao.Properties.Door_plate_num.eq(door_plate_num))
                .unique();
        return userLoginStyleEntity;
    }

    public void checkAndSaveUserLoginStyle2(String phone, String psw) {
//        Log.i("debug", phone + "&" + psw);
        UserLoginStyleEntity userLoginStyleEntity = getUserLoginStyleDao().queryUserIsExistByPhone2(phone);
        if (userLoginStyleEntity == null) {
            userLoginStyleEntity = new UserLoginStyleEntity();
            userLoginStyleEntity.setUser_phone(phone);
            userLoginStyleEntity.setQr_code(psw);
            userLoginStyleEntity.setUser_type(2001);
            DataSourceOperator.getInstance().getUserLoginStyleDao().insertUserLoginStyleToDb(userLoginStyleEntity);
        } else {
            userLoginStyleEntity.setUser_type(2001);
            userLoginStyleEntity.setQr_code(psw);
            DataSourceOperator.getInstance().updateUserLoginStyleToDb(userLoginStyleEntity);
        }
    }

    public void checkAndSaveUser2LoginStyle(String loginType, String result) {
        UserLoginStyleEntity userLoginStyleEntity = getUserLoginStyleDao()
                .queryUserIsExistByPhone(Constants.USER_PHONE);
        if (userLoginStyleEntity == null && loginType.equals(Constants.LoginType.BACK_SCAN)) {
            userLoginStyleEntity = getUserLoginStyleDao().queryUserIsExistByQrCode(result);
        }
        if (userLoginStyleEntity == null && loginType.equals(Constants.LoginType.IC_CARD)) {
            userLoginStyleEntity = getUserLoginStyleDao().queryUserIsExistByIc(result);
        }
        if (userLoginStyleEntity == null) {
            userLoginStyleEntity = new UserLoginStyleEntity();
            userLoginStyleEntity.setUser_phone(Constants.USER_PHONE);
            switch (loginType) {
                case Constants.LoginType.PHONE:
                    break;
                case Constants.LoginType.IC_CARD:
                    userLoginStyleEntity.setIc_card_num(result);
                    break;
                case Constants.LoginType.BACK_SCAN:
                    userLoginStyleEntity.setQr_code(result);
                    break;
            }
            userLoginStyleEntity.setUser_type(2000);
            DataSourceOperator.getInstance().getUserLoginStyleDao().insertUserLoginStyleToDb(userLoginStyleEntity);
        } else {
            switch (loginType) {
                case Constants.LoginType.PHONE:
                    userLoginStyleEntity.setUser_phone(result);
                    break;
                case Constants.LoginType.IC_CARD:
                    userLoginStyleEntity.setIc_card_num(result);
                    break;
                case Constants.LoginType.BACK_SCAN:
                    userLoginStyleEntity.setQr_code(result);
                    break;
                default:
                    break;
            }
            DataSourceOperator.getInstance().updateUserLoginStyleToDb(userLoginStyleEntity);
        }
    }

    public void checkAndSaveUserLoginStyle(String loginType, String result) {
        UserLoginStyleEntity userLoginStyleEntity = getUserLoginStyleDao()
                .queryUserIsExistByPhone(Constants.USER_PHONE);
        if (userLoginStyleEntity == null && loginType.equals(Constants.LoginType.BACK_SCAN)) {
            userLoginStyleEntity = getUserLoginStyleDao().queryUserIsExistByQrCode(result);
        }
        if (userLoginStyleEntity == null && loginType.equals(Constants.LoginType.IC_CARD)) {
            userLoginStyleEntity = getUserLoginStyleDao().queryUserIsExistByIc(result);
        }
        if (userLoginStyleEntity == null) {
            userLoginStyleEntity = new UserLoginStyleEntity();
            userLoginStyleEntity.setUser_phone(Constants.USER_PHONE);
            switch (loginType) {
                case Constants.LoginType.PHONE:
                    userLoginStyleEntity.setUser_type(1000);
                    break;
                case Constants.LoginType.IC_CARD:
                    userLoginStyleEntity.setIc_card_num(result);
                    userLoginStyleEntity.setUser_type(1110);
                    break;
                case Constants.LoginType.BACK_SCAN:
                    userLoginStyleEntity.setQr_code(result);
                    userLoginStyleEntity.setUser_type(1110);
                    break;
            }
            DataSourceOperator.getInstance().getUserLoginStyleDao()
                    .insertUserLoginStyleToDb(userLoginStyleEntity);
        } else {
            switch (loginType) {
                case Constants.LoginType.PHONE:
                    userLoginStyleEntity.setUser_phone(result);
                    userLoginStyleEntity.setUser_type(1000);
                    break;
                case Constants.LoginType.IC_CARD:
                    userLoginStyleEntity.setIc_card_num(result);
                    userLoginStyleEntity.setUser_type(1010);
                    break;
                case Constants.LoginType.BACK_SCAN:
                    userLoginStyleEntity.setQr_code(result);
                    userLoginStyleEntity.setUser_type(1010);
                    break;
                default:
                    break;
            }
            DataSourceOperator.getInstance().updateUserLoginStyleToDb(userLoginStyleEntity);
        }
        //查询添加的记录
        if (Constants.IS_TEST) {
            List<UserLoginStyleEntity> userLoginStyleEntities = getUserLoginStyleDao().queryUserLoginStyleFormDb();
//            for (UserLoginStyleEntity _userLoginStyleEntity : userLoginStyleEntities) {
//                Logger.i("【Database User】" + _userLoginStyleEntity.toString());
//            }
        }
    }

    public void checkAndSaveUserLoginStyle(CommonUserEntity commonUserEntity) {
        if (commonUserEntity == null) return;
        String userPhone = commonUserEntity.getPhone();
        List<UserLoginStyleEntity> userLoginStyleEntities = getUserLoginStyleDao().queryUserLoginStyleFormDb();
//        for (UserLoginStyleEntity _userLoginStyleEntity : userLoginStyleEntities) {
//            Logger.i("【Database User】" + _userLoginStyleEntity.toString());
//        }
        if (!TextUtils.isEmpty(commonUserEntity.getLoginValue())) {
            UserLoginStyleEntity userLoginStyleEntity = getUserLoginStyleDao().queryUserIsExistByIc(commonUserEntity.getLoginValue());
            boolean isExist = userLoginStyleEntity != null;
            if (userLoginStyleEntity == null) {
                userLoginStyleEntity = new UserLoginStyleEntity();
                userLoginStyleEntity.setUser_phone(userPhone);
            }
            int userType = 1;
            if (commonUserEntity.getUser_type().equals("user")) {
                userType = 0;
            }
            if (commonUserEntity.getUser_type().equals("operation")) {
                userType = 2;
            }
            userLoginStyleEntity.setUser_type(userType);
            if (commonUserEntity.getLoginType().equals("icCard")) {
                userLoginStyleEntity.setIc_card_num(commonUserEntity.getLoginValue());
            }
            if (isExist) {
                DataSourceOperator.getInstance().updateUserLoginStyleToDb(userLoginStyleEntity);
            } else {
                DataSourceOperator.getInstance().getUserLoginStyleDao().insertUserLoginStyleToDb(userLoginStyleEntity);
            }
        }
    }

    public UserLoginStyleEntity checkUserIsExist(String loginType, String loginValue) {
        UserLoginStyleEntity userLoginStyleEntity = null;
        if (loginType.equals("icCard")) {
            userLoginStyleEntity = getUserLoginStyleDao().queryUserIsExistByIc(loginValue);
        }
        if (loginType.equals("phone")) {
            userLoginStyleEntity = getUserLoginStyleDao().queryUserIsExistByPhone3(loginValue);
        }
        return userLoginStyleEntity;
    }

    public DataSourceOperator getMonitorImgDao() {
        if (monitorImgEntityDao == null) {
            monitorImgEntityDao = DatabaseUtil.getInstance().getDaoSession().getMonitorImgEntityDao();
        }
        return dataSourceOperator;
    }

    public long insertMonitorImgEntityToDb(MonitorImgEntity monitorImgEntity) {
//        MonitorImgEntity _monitorImgEntity = DataSourceOperator.getInstance().getMonitorImgDao()
//                .queryMonitorImgEntity(monitorImgEntity.getOrderId(), monitorImgEntity.getMonitorAddress());
//        long insertId = 0;
//        if(_monitorImgEntity!=null){
//            monitorImgEntityDao.update(monitorImgEntity);
//            insertId = monitorImgEntity.getId();
//        }else{
//            insertId = monitorImgEntityDao.insertOrReplace(monitorImgEntity);
//        }
        long insertId = monitorImgEntityDao.insertOrReplace(monitorImgEntity);
//        Logger.i("【监控图片数据库】 插入/更新" + "ID: " + insertId);
        return insertId;
    }

    public void updateMonitorImgEntityToDb(MonitorImgEntity monitorImgEntity) {
        monitorImgEntityDao.update(monitorImgEntity);
    }

    public List<MonitorImgEntity> queryMonitorImgEntityFormDb() {
        return monitorImgEntityDao.queryBuilder().list();
    }

//    public void queryMonitorImgEntityToUpload() {
//        List<MonitorImgEntity> monitorImgEntities = monitorImgEntityDao.queryBuilder().list();
//        for (MonitorImgEntity monitorImgEntity: monitorImgEntities){
//
//        }
//    }

    public MonitorImgEntity queryMonitorImgEntity(String orderId, String ipAddress) {
        return monitorImgEntityDao.queryBuilder()
                .where(MonitorImgEntityDao.Properties.OrderId.eq(orderId),
                        MonitorImgEntityDao.Properties.MonitorAddress.eq(ipAddress))
                .unique();
    }

    public boolean queryAndDeleteMonitorImgEntityFromDb(String ossObjectKey, boolean isDeleteFile) {
        try {
            List<MonitorImgEntity> monitorImgEntityS = monitorImgEntityDao.queryBuilder()
                    .where(MonitorImgEntityDao.Properties.OssObjectKey.eq(ossObjectKey)).list();
            if (monitorImgEntityS == null || monitorImgEntityS.size() == 0) return false;
            if (monitorImgEntityS.get(0) != null) {
                monitorImgEntityDao.delete(monitorImgEntityS.get(0));
                if (isDeleteFile)
                    FileUtil.deleteSingleFile(monitorImgEntityS.get(0).getImgLocalSavePath());
                Logger.i("[DataSourceOperator] Local data has been deleted " + monitorImgEntityS.get(0).toString());
                return true;
            }
            Logger.i("[DataSourceOperator] Order" + ossObjectKey + "does not exist");
            return false;
        } catch (Exception e) {
            Logger.i("【Query Data From DB】 error: " + e.getMessage());
        }
        return false;
    }

    public boolean queryOrderIdAndDeleteMonitorImgEntityFromDb(String orderId, boolean isDeleteFile) {
        try {
            List<MonitorImgEntity> monitorImgEntityS = monitorImgEntityDao.queryBuilder()
                    .where(MonitorImgEntityDao.Properties.OrderId.eq(orderId)).list();
            if (monitorImgEntityS == null || monitorImgEntityS.size() == 0) return false;
            if (monitorImgEntityS.get(0) != null) {
                monitorImgEntityDao.delete(monitorImgEntityS.get(0));
                if (isDeleteFile) {
                    String[] imagePaths = monitorImgEntityS.get(0).getImgLocalSavePath().split("and");
                    for (String path : imagePaths) {
                        FileUtil.deleteSingleFile(path);
                    }
//                    Logger.e("【监控图片】本地数据已删除 " + monitorImgEntityS.get(0).toString());
                }
                return true;
            }
//            Logger.e("【监控图片】订单" + orderId + "不存在");
            return false;
        } catch (Exception e) {
//            Logger.i("【Query Data From DB】 error: " + e.getMessage());
        }
        return false;
    }

    public boolean queryAndUpdateMonitorImgEntityFromDb(String ossObjectKey, String errorMsg) {
        MonitorImgEntity monitorImgEntity = monitorImgEntityDao.queryBuilder()
                .where(MonitorImgEntityDao.Properties.OssObjectKey.eq(ossObjectKey)).unique();
        if (monitorImgEntity != null) {
            monitorImgEntity.setLastUploadErrorInfo(errorMsg);
            monitorImgEntity.setHaveUploadCount(monitorImgEntity.getHaveUploadCount() + 1);
            if (monitorImgEntity.getHaveUploadCount() < 5) {
                monitorImgEntityDao.update(monitorImgEntity);
//                Logger.e("【监控图片】本地数据已更新 " + monitorImgEntity.toString());

            } else {
                monitorImgEntityDao.delete(monitorImgEntity);
                FileUtil.deleteSingleFile(monitorImgEntity.getImgLocalSavePath());
//                Logger.e("【监控图片】超过限定上传次数 " + monitorImgEntity.toString());
            }
            return true;
        }
//        Logger.e("【监控图片】订单" + ossObjectKey + "不存在");
        return false;
    }

    /************************************************************************************
     *
     *  2022年9月8日16:13:12  添加核销码表数据操作
     *
     * **********************************************************************************
     */
    public DataSourceOperator getWriteOffEntityDao() {
        if (writeOffEntityDao == null) {
            writeOffEntityDao = DatabaseUtil.getInstance().getDaoSession().getWriteOffEntityDao();
        }
        return dataSourceOperator;
    }

    //删除所有核销码实体
    public void deleteAllWriteOffEntity() {
        getWriteOffEntityDao().writeOffEntityDao.deleteAll();
    }

    //查询指定核销码实体是否存在
    public boolean isWriteOffCodeExist(String code) {
        return getWriteOffEntityDao().writeOffEntityDao.queryBuilder().where(WriteOffEntityDao.Properties.Code.eq(code)).unique() != null;
    }

    //插入核销码实体
    public void insertWriteOffCodeToDb(WriteOffEntity writeOffEntity) {
        getWriteOffEntityDao().writeOffEntityDao.insertOrReplace(writeOffEntity);
//        if(id > -1){
//            Logger.i("【核销码】插入成功: " + writeOffEntity.toString());
//        }
    }

    //获取今日核销记录
    public List<WriteOffEntity> getTodayWriteOffCodeEntities() {
        return getWriteOffEntityDao().writeOffEntityDao.queryBuilder().list();
    }

    public String getLastWriteOffDate() {
        List<WriteOffEntity> writeOffEntities = getWriteOffEntityDao().writeOffEntityDao.queryBuilder().list();
        if (writeOffEntities.size() == 0) return DateUtil.getCurTime(DateUtil.FORMAT_DATE);
        WriteOffEntity writeOffEntity = getWriteOffEntityDao().writeOffEntityDao.queryBuilder().orderAsc(WriteOffEntityDao.Properties.Id).limit(1).unique();
        return writeOffEntity != null ? writeOffEntity.getDate() : null;
    }

    public void checkWriteOffDataToDelete() {
        String curDate = DateUtil.getCurTime(DateUtil.FORMAT_DATE);
        String lastDate = getLastWriteOffDate();
        if (!curDate.equals(lastDate)) {
            List<WriteOffEntity> writeOffEntities = getWriteOffEntityDao().writeOffEntityDao.queryBuilder().where(WriteOffEntityDao.Properties.Date.eq(lastDate)).list();
            getWriteOffEntityDao().writeOffEntityDao.deleteInTx(writeOffEntities);
//            Logger.i("【核销码】 删除数据：" + writeOffEntities.size() + "条");
        }
    }


    /***********************************************************************************
     *  2025年9月22日16:59:12  添加二维码绑定表数据操作
     *  ********************************************************************************
     */


    public QrCodeBindingDao getQrCodeBindingDao() {
        if (qrCodeBindingDao == null) {
            qrCodeBindingDao = DatabaseUtil.getInstance().getDaoSession().getQrCodeBindingDao();
        }
        return qrCodeBindingDao;
    }

    public void deleteQrCodeBindingDao() {
        getQrCodeBindingDao().deleteAll();
    }

    public List<QrCodeBinding> getQrCodeBindingDaoList() {
        return getQrCodeBindingDao().queryBuilder().list();
//        return DatabaseUtil.getInstance().getDaoSession().getBarCodeInfoDao().queryBuilder().list();
    }


    public long insertQrCodeBindingToDb(QrCodeBinding containers) {

        return  getQrCodeBindingDao().insertOrReplace(containers);

    }
    public void updateQrCodeBindingToDb(QrCodeBinding containers) {
        getQrCodeBindingDao().update(containers);
    }
    public void updateBagIdByItemQr(String itemQr, String BagId) {
        QrCodeBinding qrCodeBinding = findByItemQrCode(itemQr);
        if (qrCodeBinding != null) {
            qrCodeBinding.setBagId(BagId);
            getQrCodeBindingDao().update(qrCodeBinding);
        }else{
            Logger.e("【QrCodeBinding】itemQr not exist: " + itemQr);
        }
    }

    public QrCodeBinding findByGridQr(String gridQr) {
        return getQrCodeBindingDao().queryBuilder()
                .where(QrCodeBindingDao.Properties.GridQrCode.eq(gridQr)).limit(1).unique();
    }

    public QrCodeBinding findByItemQrCode(String itemQr) {
        return getQrCodeBindingDao().queryBuilder()
                .where(QrCodeBindingDao.Properties.ItemQrCode.eq(itemQr)).limit(1).unique();
    }
    public void deleteByItemQrCode(String itemQr) {
        QrCodeBinding unique = getQrCodeBindingDao().queryBuilder()
                .where(QrCodeBindingDao.Properties.ItemQrCode.eq(itemQr)).limit(1).unique();

         getQrCodeBindingDao().delete(unique);
    }
}
