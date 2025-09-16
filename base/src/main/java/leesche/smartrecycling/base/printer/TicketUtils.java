package leesche.smartrecycling.base.printer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.leesche.logger.Logger;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import leesche.smartrecycling.base.entity.AIBottlePrintEntity;
import leesche.smartrecycling.base.utils.QrCodeUtil;

public class TicketUtils {

    public static String formatNumber(double number) {
        DecimalFormat df = new DecimalFormat("#.00");  // 或 "#.##" 表示自动省略末尾0
        return df.format(number);
    }
    public static Bitmap generateKDMedicalTicket(Context context, AIBottlePrintEntity aiBottlePrintEntity) throws IOException {
        StringBuilder codeStr = new StringBuilder();
        //单据头
//        int ticketWidth = 576;
        // 步骤1: 获取打印机DPI，这里我们假设一个常用值
        float printerDpi = 203; // 常用打印机DPI值
        // 步骤2: 计算图片的像素尺寸
        float mmToPx = printerDpi / 25.4f; // 1mm = 25.4px when printed
        int ticketWidth = (int) (80 * mmToPx - 25); // 80mm x 80mm image in px

        TextPaint titlePaint = new TextPaint();
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(32);
        StaticLayout titleLayout = new StaticLayout(aiBottlePrintEntity.getDesc(), titlePaint, ticketWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0, false);

        TextPaint codePaint = new TextPaint();
        codePaint.setColor(Color.BLACK);
        codePaint.setTextSize(28);
        StaticLayout codeLayout = new StaticLayout(aiBottlePrintEntity.getWriteOffNumberMark(), codePaint, ticketWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0, false);
        // 单据尾
        TextPaint tailPaint = new TextPaint();
        tailPaint.setColor(Color.BLACK);
        tailPaint.setTextSize(24);
        StaticLayout tailLayout = new StaticLayout(aiBottlePrintEntity.getDesc2(), tailPaint, ticketWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0, false);
        StaticLayout snLayout = new StaticLayout("SN: " + aiBottlePrintEntity.getSn(), tailPaint, ticketWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0, false);
        StaticLayout remarkLayout = null;
        if (!TextUtils.isEmpty(aiBottlePrintEntity.getRemark())) {
            remarkLayout = new StaticLayout(aiBottlePrintEntity.getRemark(), tailPaint, ticketWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0, false);
        }
//        StaticLayout addressLayout = null;
//        if (!TextUtils.isEmpty(aiBottlePrintEntity.getAddress())) {
//            addressLayout = new StaticLayout(aiBottlePrintEntity.getAddress(), tailPaint, ticketWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0, false);
//        }
        TextPaint itemPaint = new TextPaint();
        itemPaint.setColor(Color.BLACK);
        itemPaint.setTextSize(32);

        TextPaint itemLinePaint = new TextPaint();
        itemLinePaint.setColor(Color.BLACK);
        itemLinePaint.setTextSize(24);

        List<StaticLayout> itemWasteInfoList = new ArrayList<>();
        int itemWasteInfoWidth = ticketWidth - 10;
        StaticLayout itemDateTimeLayout = new StaticLayout("Date|Time: " + aiBottlePrintEntity.getPrintTime() + "\n", tailPaint, itemWasteInfoWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0, false);
//        itemWasteInfoList.add(itemDateTimeLayout);


        TextPaint Text02 = new TextPaint();
        Text02.setColor(Color.WHITE);
        Text02.setTextSize(32);
        StaticLayout titleLayoutText02 = null;
        if (!TextUtils.isEmpty(aiBottlePrintEntity.getAdditionalExplanatoryText())) {
//            Logger.i("getAdditionalExplanatoryText"+aiBottlePrintEntity.getAdditionalExplanatoryText());
            titleLayoutText02 = new StaticLayout(aiBottlePrintEntity.getAdditionalExplanatoryText(), Text02, ticketWidth-20, Layout.Alignment.ALIGN_CENTER, 1f, 0, false);
        }

        StaticLayout itemAboveLine = new StaticLayout("----------------------------------------------------------------------------------",
                itemLinePaint, itemWasteInfoWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0, false);
        itemWasteInfoList.add(itemAboveLine);
//        String itemTitle = "Item                            Qty                  Amount";
        // 标题
        String itemTitle = String.format("%-28s%-15s%8s", "Item", "Qty", "Amount");
        if (!TextUtils.isEmpty(aiBottlePrintEntity.getItemText())) {
            itemTitle = aiBottlePrintEntity.getItemText();
        }
        Logger.i(itemTitle);
        StaticLayout itemTitleLayout = new StaticLayout(itemTitle, itemPaint, itemWasteInfoWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0, false);
        itemWasteInfoList.add(itemTitleLayout);
        for (AIBottlePrintEntity.DepositItem depositItem : aiBottlePrintEntity.getDepositItems()) {
            itemWasteInfoList.add(itemAboveLine);
//            StringBuilder itemContent = new StringBuilder(depositItem.getItemName());
//            int length = depositItem.getItemName().codePointCount(0, depositItem.getItemName().length());
//            if (length < 18) {
//                for (int i = 0; i < 18 - length; i++) {
//                    itemContent.append("  ");
//                }
//            }
//            itemContent.append(depositItem.getItemQuantity());
//            length = (depositItem.getItemQuantity() + "").codePointCount(0, (depositItem.getItemQuantity() + "").length());
//            if (length < 9) {
//                for (int i = 0; i < 9 - length; i++) {
//                    itemContent.append("  ");
//                }
//            }
//            length = (depositItem.getItemAmount() + "").codePointCount(0, (depositItem.getItemAmount() + "").length());
//            if (length < 9) {
//                for (int i = 0; i < 9 - length; i++) {
//                    itemContent.append("  ");
//                }
//            }

            @SuppressLint("DefaultLocale") String itemTitle2 = String.format("%-28s%-18s%10s", depositItem.getItemName(),depositItem.getItemQuantity(), String.format("%.2f", depositItem.getItemAmount()));

//            itemContent.append(depositItem.getItemAmount());
            StaticLayout itemContentLayout = new StaticLayout(itemTitle2, itemPaint, itemWasteInfoWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0, false);
            itemWasteInfoList.add(itemContentLayout);
        }
        itemWasteInfoList.add(itemAboveLine);

        String depositFeeText = TextUtils.isEmpty(aiBottlePrintEntity.getDSRText()) ? "Deposit Fee" : aiBottlePrintEntity.getDSRText();
        String pointsText = TextUtils.isEmpty(aiBottlePrintEntity.getPointsText()) ? "Points" : aiBottlePrintEntity.getPointsText();
        StaticLayout itemTotalLayout = new StaticLayout(depositFeeText + " " + aiBottlePrintEntity.getCurrencySymbol() + " " + aiBottlePrintEntity.getTotalMoney(), itemPaint, itemWasteInfoWidth, Layout.Alignment.ALIGN_CENTER, 1.2f, 0, false);
        StaticLayout itemTotalPointsLayout = new StaticLayout(pointsText + " " + Math.round(aiBottlePrintEntity.getTotalMoney() * 100), itemPaint, itemWasteInfoWidth, Layout.Alignment.ALIGN_CENTER, 1.2f, 0, false);
        //        itemWasteInfoList.add(itemTotalLayout);
        // 计算位图高度
        int bitmapWidth = (int) (0.4 * ticketWidth);
        // 发票号二维码
        Bitmap invoiceQRCodeBitmap = QrCodeUtil.createQRCode(aiBottlePrintEntity.getQrCode(), 400);
        int bitmapHeight = titleLayout.getHeight() + tailLayout.getHeight() + codeLayout.getHeight() + itemDateTimeLayout.getHeight() +
                (aiBottlePrintEntity.isDisplayDSR() ? itemTotalLayout.getHeight() : 0) +
                (aiBottlePrintEntity.isDisplayPoint() ? itemTotalPointsLayout.getHeight() : 0) + invoiceQRCodeBitmap.getHeight()
                + snLayout.getHeight() + (remarkLayout != null ? remarkLayout.getHeight() : 0) + (titleLayoutText02 != null ? titleLayoutText02.getHeight() : 0) +
                (aiBottlePrintEntity.getBitmap() != null ? aiBottlePrintEntity.getBitmap().getHeight() : 0) +
                (aiBottlePrintEntity.getBottomBitmap() != null ? aiBottlePrintEntity.getBottomBitmap().getHeight() : 0) + 240;
        for (int row = 0; row < itemWasteInfoList.size(); ++row) {
            bitmapHeight += itemWasteInfoList.get(row).getHeight();
        }
        // 创建位图并绘制位图
        Bitmap bitmap = Bitmap.createBitmap(ticketWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        canvas.translate(0, 0);
        // 绘制图片
        if (aiBottlePrintEntity.getBitmap() != null) {
            canvas.drawBitmap(aiBottlePrintEntity.getBitmap(), (ticketWidth - aiBottlePrintEntity.getBitmap().getWidth()) / 2, 0, null);
            canvas.translate(0, aiBottlePrintEntity.getBitmap().getHeight() + 20);
        }
        // 绘制头
        titleLayout.draw(canvas);
        canvas.translate(0, titleLayout.getHeight() + 20);
        //绘制核销码
        codeLayout.draw(canvas);
        canvas.translate(0, codeLayout.getHeight() + 10);
        // 绘制二维码
        canvas.drawBitmap(invoiceQRCodeBitmap, (ticketWidth - invoiceQRCodeBitmap.getWidth()) / 2, 0, null);
        canvas.translate(20, invoiceQRCodeBitmap.getHeight() + 10);
        itemDateTimeLayout.draw(canvas);
        canvas.translate(0, 40);
        // 绘制SN
        snLayout.draw(canvas);
        canvas.translate(0, snLayout.getHeight()+15);
        //绘制自定义内容2
        if(titleLayoutText02!=null){
            // 1. 计算背景尺寸（宽度减少，高度包含上下边距）
            float textHeight = titleLayoutText02.getHeight();
            float paddingHorizontal = 20f; // 左右缩进
            float paddingVertical = 10f;   // 上下边距

// 2. 绘制黑色背景（宽度减少，高度增加 paddingVertical）
            Paint bgPaint = new Paint();
            bgPaint.setColor(Color.BLACK);
            canvas.drawRect(
                    paddingHorizontal, // 左边距
                    0,                 // 顶部位置（后续通过 translate 调整居中）
                    ticketWidth - paddingHorizontal, // 右边距
                    textHeight + paddingVertical * 2, // 高度 = 文字高度 + 上下边距
                    bgPaint
            );

// 3. 移动画布，使文字垂直居中
            canvas.translate(
                    paddingHorizontal, // 水平缩进
                    paddingVertical    // 垂直居中偏移 = (背景高度 - 文字高度) / 2
            );

// 4. 绘制白色文本
            titleLayoutText02.draw(canvas);

// 5. 移动画布到下一段位置（背景高度 + 额外间距）
            canvas.translate(0, textHeight + paddingVertical * 2 + 10);

//            // 1. 绘制黑色背景（计算文本高度）
//            float textHeight = titleLayoutText02.getHeight();
//            canvas.drawRect(0, 0, ticketWidth-20, textHeight+10, new Paint());  // 黑色背景
//// 2. 绘制白色文本
//            titleLayoutText02.draw(canvas);
//            canvas.translate(0, titleLayoutText02.getHeight() + 10);
        }
        //绘制内容
        StaticLayout _itemNameLayout = itemWasteInfoList.get(1);
        for (int row = 0; row < itemWasteInfoList.size(); ++row) {
            _itemNameLayout = itemWasteInfoList.get(row);
            _itemNameLayout.draw(canvas);
            if (row < itemWasteInfoList.size() - 1) {
                canvas.translate(0, _itemNameLayout.getHeight());
            }
        }
        canvas.translate(-20, _itemNameLayout.getHeight() + 15);
        //绘制DSR
        if (aiBottlePrintEntity.isDisplayDSR()) {
            itemTotalLayout.draw(canvas);
            canvas.translate(0, itemTotalLayout.getHeight() + 15);
        }
        // 绘制Points
        if (aiBottlePrintEntity.isDisplayPoint()) {
            itemTotalPointsLayout.draw(canvas);
            canvas.translate(0, itemTotalPointsLayout.getHeight() + 15);
        }
        // 绘制尾
        tailLayout.draw(canvas);
        canvas.translate(20, tailLayout.getHeight() + 20);
        // 绘制附加信息
        if (remarkLayout != null) {
            remarkLayout.draw(canvas);
            canvas.translate(0, remarkLayout.getHeight() + 40);
        }
        // 绘制底部图片
        if (aiBottlePrintEntity.getBottomBitmap() != null) {
            canvas.drawBitmap(aiBottlePrintEntity.getBottomBitmap(), (ticketWidth - aiBottlePrintEntity.getBottomBitmap().getWidth()) / 2, 0, null);
//            canvas.translate(0, aiBottlePrintEntity.getBottomBitmap().getHeight() + 60);
        }
        return bitmap;
    }
}
