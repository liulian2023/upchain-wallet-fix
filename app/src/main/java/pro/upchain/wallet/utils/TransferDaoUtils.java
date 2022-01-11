package pro.upchain.wallet.utils;

import android.text.TextUtils;

import java.util.List;

import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.entity.PendingHistoryEntity;
import pro.upchain.wallet.entity.PendingHistoryEntityDao;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class TransferDaoUtils {
    public static PendingHistoryEntityDao pendingHistoryEntityDao = MyApplication.getsInstance().getDaoSession().getPendingHistoryEntityDao();

    /**
     * 插入新创建钱包
     *
     * @param pendingHistoryEntity 新创建钱包
     */
    public static void insertNewTransfer(PendingHistoryEntity pendingHistoryEntity) {

        pendingHistoryEntityDao.insert(pendingHistoryEntity);
    }

    /**
     * 查询所有钱包
     */
    public static List<PendingHistoryEntity> loadAll() {
        return pendingHistoryEntityDao.loadAll();
    }



    /**
     * 删除单条记录
     */
    public static boolean delete(PendingHistoryEntity entity) {
        try {

            pendingHistoryEntityDao.delete(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
