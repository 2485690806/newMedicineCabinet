package leesche.smartrecycling.base.handler;

import android.content.Context;

import com.google.gson.JsonParser;
import com.leesche.logger.Logger;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import leesche.smartrecycling.base.R;
import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.common.EventType;
import leesche.smartrecycling.base.eventbus.BasicMessageEvent;
import leesche.smartrecycling.base.utils.AutoInstaller;
import leesche.smartrecycling.base.utils.CalcUtil;
import leesche.smartrecycling.base.utils.FileUtil;

public class SystemFuncHandler {

    private Context mContext;

//    public void enterManagerLogin(FragmentActivity context) {
//        if (ClientConstant.CUR_PAGE == ClientConstant.PageFlag.USER_HOME) {
//            codeCounter.incrementAndGet();
//            if (codeCounter.get() == 5) {
//                codeCounter.set(0);
//                ManagerLoginFragment.showDialog(context);
//            }
//        }
//    }
//
//    public void displayMachineCode(FragmentActivity context) {
//        if (ClientConstant.CUR_PAGE == ClientConstant.PageFlag.USER_HOME) {
//            codeCounter.incrementAndGet();
//            if (codeCounter.get() == 5) {
//                codeCounter.set(0);
//                MachineCodeFragment.showDialog(context);
//            }
//        }
//    }

    public void downloadApkToInstall(String updateUrl) {
        String firDir = Constants.SDCARD_DIR + File.separator + "AutoInstaller";
        FileDownloader.getImpl().create(updateUrl)
                .setPath(firDir, true)
                .setListener(fileDownloadListener)
                .start();
    }

    FileDownloadListener fileDownloadListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask baseDownloadTask, int i, int i1) {
            EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.USER_HINT_INFO,
                    mContext.getString(R.string.app_downloading)));
        }

        @Override
        protected void progress(BaseDownloadTask baseDownloadTask, int i, int i1) {
//            String valueStr = i + "|" + i1;
//            if (i == i1) {
//                valueStr = "OK";
//            }
            int progress = (int) (CalcUtil.divide((double) i, (double) i1) * 100);
            EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.USER_HINT_INFO,
                    mContext.getString(R.string.app_downloading) + progress + "%"));
        }

        @Override
        protected void completed(BaseDownloadTask baseDownloadTask) {
            String filePath = baseDownloadTask.getPath() + File.separator + baseDownloadTask.getFilename();
            EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.USER_HINT_INFO,
                    mContext.getString(R.string.app_downloaded)));
            Logger.i("[System] fill downloaded path: " + filePath);
            if (filePath.contains(".bin")) {
                return;
            }
            if (filePath.contains(".apk")) {
                AutoInstaller.getDefault(mContext).install(new File(filePath));
            }
        }

        @Override
        protected void paused(BaseDownloadTask baseDownloadTask, int i, int i1) {

        }

        @Override
        protected void error(BaseDownloadTask baseDownloadTask, Throwable throwable) {

        }

        @Override
        protected void warn(BaseDownloadTask baseDownloadTask) {

        }
    };

    public void init(Context context) {
        mContext = context;
    }

    private static AtomicInteger codeCounter = new AtomicInteger(0);

    public void downloadVideoFile(List<String> videoUrls) {
        if (videoUrls.size() == 0) return;
        final FileDownloadListener parallelTarget = createListener();
        final List<BaseDownloadTask> taskList = new ArrayList<>();
        for (String rawUrl : videoUrls) {
            taskList.add(FileDownloader.getImpl().create(rawUrl));
        }
        FileUtil.createDir(Constants.VIDEO_CACHE_DIR);
        new FileDownloadQueueSet(parallelTarget)
                .disableCallbackProgressTimes()
                .setAutoRetryTimes(5)
                .setDirectory(Constants.VIDEO_CACHE_DIR)
                .downloadTogether(taskList)
                .start();
    }

    public void downloadPrintLogoFile(List<String> videoUrls) {
        if (videoUrls.size() == 0) return;
        final FileDownloadListener parallelTarget = createListener();
        final List<BaseDownloadTask> taskList = new ArrayList<>();
        for (String rawUrl : videoUrls) {
            taskList.add(FileDownloader.getImpl().create(rawUrl));
        }
        String dir = Constants.BASE_CACHE_DIR + File.separator + "printer";
        FileUtil.createDir(dir);
        new FileDownloadQueueSet(parallelTarget)
                .disableCallbackProgressTimes()
                .setAutoRetryTimes(5)
                .setDirectory(dir)
                .downloadTogether(taskList)
                .start();
    }

    private FileDownloadListener createListener() {
        return new FileDownloadListener() {
            @Override
            protected boolean isInvalid() {
//                if (getActivity() != null) return getActivity().isFinishing();
                return true;
            }

            @Override
            protected void pending(final BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
            }

            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                super.connected(task, etag, isContinue, soFarBytes, totalBytes);

            }

            @Override
            protected void progress(final BaseDownloadTask task, final int soFarBytes, final int totalBytes) {

            }

            @Override
            protected void blockComplete(final BaseDownloadTask task) {

            }

            @Override
            protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
                super.retry(task, ex, retryingTimes, soFarBytes);

            }

            @Override
            protected void completed(BaseDownloadTask task) {
                Logger.i("[系统]广告：" + task.getFilename() + " 下载完成");
            }

            @Override
            protected void paused(final BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
            }

            @Override
            protected void warn(BaseDownloadTask task) {
            }
        };
    }

    public String getLocalFilePath(String imageHref) {
        String fileName = imageHref.substring(imageHref.lastIndexOf("/"));
        if (imageHref.endsWith("q_70")) {
            String _fileName = imageHref.split("\\?")[0];
            fileName = _fileName.substring(_fileName.lastIndexOf("/"));
        }
        return Constants.VIDEO_CACHE_DIR + fileName;
    }

    JsonParser jsonParser;


    private static final class SystemFuncHandlerHolder {
        static final SystemFuncHandler systemFuncHandler = new SystemFuncHandler();
    }

    public static SystemFuncHandler getInstance() {
        return SystemFuncHandler.SystemFuncHandlerHolder.systemFuncHandler;
    }

}
