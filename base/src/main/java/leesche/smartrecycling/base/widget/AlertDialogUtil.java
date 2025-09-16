package leesche.smartrecycling.base.widget;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogUtil {

    /**
     * 列表 dialog
     */
    public void showList(Context context, String title, String[] items, final ListAlertDialogOnclickCallBack listAlertDialogOnclickCallBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listAlertDialogOnclickCallBack.onclick(i);
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    public interface ListAlertDialogOnclickCallBack {
        void onclick(int position);
    }

    /**
     * 两个按钮的 dialog
     */
    public void showTwoBtn(Context context, String title, String message, String leftBtn, String rightBtn, final TwoBtnDialogOnclickCallBack twoBtnDialogOnclickCallBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title)
                .setMessage(message).setPositiveButton(leftBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        twoBtnDialogOnclickCallBack.leftBtnOnclick();
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(rightBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        twoBtnDialogOnclickCallBack.rightBtnOnclick();
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    public interface TwoBtnDialogOnclickCallBack {
        void leftBtnOnclick();

        void rightBtnOnclick();
    }

    /**
     * 带有进度的 dialog
     */
    public void showLoading(Context context, String title, progressDialogCallBack progressDialogCallBack) {
        final int MAX_VALUE = 100;
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgress(0);
        progressDialog.setTitle(title);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(MAX_VALUE);
        progressDialog.show();
        progressDialogCallBack.progressDialog(progressDialog);
    }

    public interface progressDialogCallBack {
        void progressDialog(ProgressDialog progressDialog);
    }

}
