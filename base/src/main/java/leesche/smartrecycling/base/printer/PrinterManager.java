//package leesche.smartrecycling.base.printer;
//
//import android.bluetooth.BluetoothDevice;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.ContextWrapper;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.os.Build;
//import android.text.Layout;
//import android.text.StaticLayout;
//import android.text.TextPaint;
//import android.text.TextUtils;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.leesche.logger.Logger;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//
//import POSAPI.POSBluetoothAPI;
//import POSAPI.POSInterfaceAPI;
//import POSAPI.POSSerialAPI;
//import POSAPI.POSUSBAPI;
//import POSAPI.POSWIFIAPI;
//import POSSDK.POSSDK;
//import android_wifi_api.SearchPortInfo;
//import leesche.smartrecycling.base.entity.AIBottlePrintEntity;
//import leesche.smartrecycling.base.utils.QrCodeUtil;
//
//
//public class PrinterManager {
//    public POSSDK possdk;
//    public POSInterfaceAPI posInterfaceAPI;
//    public Context myContext;
//    public static int port_type = 0;
//    private static final int SearchPortMAX = 10;
//    private SearchPortInfo port_info[] = new SearchPortInfo[SearchPortMAX];
//    static PrinterManager printerManager;
//
//    public static PrinterManager getInstance() {
//        if (printerManager == null) {
//            printerManager = new PrinterManager();
//        }
//        return printerManager;
//    }
//
//    public void init(Context context) {
//        myContext = context;
//    }
//
//    public int EnumDevice(int portType, String[] deviceInfo, int deviceInfoLen) {
//        int nNum = 0;
//        if (portType == 3) {
//            //蓝牙
//            POSBluetoothAPI mBluetoothManager = POSBluetoothAPI.getInstance(myContext);
//            Set<BluetoothDevice> bdv = mBluetoothManager.getBondedDevices();
//            if (bdv.size() > 0) {
//                // Iterative search to bluetooth information
//                for (Iterator<BluetoothDevice> iterator = bdv.iterator(); iterator.hasNext(); ) {
//                    BluetoothDevice bluetoothDevice = (BluetoothDevice) iterator.next();
//                    deviceInfo[0] += (bluetoothDevice.getName() + "|" + bluetoothDevice.getAddress() + "@");
//                    nNum++;
//                }
//            }
//        }
//        if (portType == 2) {
//            //网口
//            POSInterfaceAPI interface_wifi = new POSWIFIAPI();
//            for (int i = 0; i < SearchPortMAX; i++) {
//                port_info[i] = new SearchPortInfo();
//            }
//            int sch_prt_num = interface_wifi.WIFISearchPort(port_info, SearchPortMAX);
//            if (sch_prt_num > 0) {
//                for (int i = 0; i < sch_prt_num; i++) {
//                    nNum++;
//                    deviceInfo[0] += (port_info[i].GetPrtName() + "|" + port_info[i].GetIPAddress() + "@");
//                }
//            }
//
//        }
//        return nNum;
//    }
//
//    public int OpenPrinter(String portType, String portInfo) {
//        int nReturn = 0;
//        if (portType.contains("USB")) {
//            port_type = 1;
////            Context wrappedContext = new ContextWrapper(myContext) {
////                @Override
////                public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
////                        return super.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
////                    } else {
////                        return super.registerReceiver(receiver, filter);
////                    }
////                }
////            };
//            posInterfaceAPI = new POSUSBAPI(myContext);
//            nReturn = posInterfaceAPI.OpenDevice();
//        }
//        if (portType.contains("NET")) {
//            port_type = 3;
//            posInterfaceAPI = new POSWIFIAPI();
//            nReturn = posInterfaceAPI.OpenDevice(portInfo, 9100);
//        }
//
//        if (portType.contains("COM")) {
//            port_type = 2;
//            posInterfaceAPI = new POSSerialAPI();
//            Log.e("111", portInfo.split("\\|")[0]);
//            Log.e("111", portInfo.split("\\|")[1]);
//            nReturn = posInterfaceAPI.OpenDevice(new File(portInfo.split("\\|")[0]), Integer.parseInt(portInfo.split("\\|")[1]));
//        }
//        if (portType.contains("BT")) {
//            port_type = 4;
//            posInterfaceAPI = new POSBluetoothAPI(myContext);
//            nReturn = posInterfaceAPI.OpenDevice(portInfo);
//        }
//        if (nReturn == 1000) {
//            nReturn = 0;
//            possdk = new POSSDK(posInterfaceAPI);
//        } else {
//            nReturn = -1;
//        }
//        return nReturn;
//    }
//
//    public int ClosePrinter() {
//
//        return posInterfaceAPI.CloseDevice();
//    }
//
//    public int Sample_Restaurant() {
//        int nRet = 0;
//        String lineBetwen = "------------------------------------------------\n";
////        possdk.textSelectCharSetAndCodePage()
//        nRet = myPrintText(lineBetwen);
//        nRet = myPrintText("2020年04月10日09：48\n");
//        nRet = myPrintText(lineBetwen);
//        nRet = myPrintText("#20美团外卖\n");
//        nRet = myPrintText("切尔西ChelseaKitchen\n");
//        nRet = myPrintText("在线支付(已支付)\n");
//        nRet = myPrintText("订单号：5415221202244734\n");
//        nRet = myPrintText("下单时间：2021-04-10 10：00：00\n");
//        nRet = myPrintText("---------------------1号口袋--------------------\n");
//        nRet = myPrintText("意大利茄汁意面X1                            32.9\n");
//        nRet = myPrintText("7寸浓香芝士披萨X1                           34.9\n");
//        nRet = myPrintText("葡式蛋挞2个装X1                                9\n");
//        nRet = myPrintText("9寸培根土豆披萨X1                           54.9\n");
//        nRet = myPrintText("9寸芝士加量X1                                 10\n");
//        nRet = myPrintText("---------------------其他-----------------------\n");
//        nRet = myPrintText("[满100.0元减40.0元]\n");
//        nRet = myPrintText("[减配送费3.0元]\n");
//        nRet = myPrintText("\n");
//        nRet = myPrintText("餐盒费：7\n");
//        nRet = myPrintText("[赠送惠尔康茶饮 X 1]:\n");
//        nRet = myPrintText(lineBetwen);
//        nRet = myPrintText("原价：￥141.7  \n");
//        nRet = myPrintText("实付：￥107.7 \n");
//        nRet = myPrintText(lineBetwen);
//        nRet = myPrintText("通鑫学生公寓A5-2\n");
//        nRet = myPrintText("号（A5-107）\n");
//        nRet = myPrintText("131****0501\n");
//        nRet = myPrintText("苏（先生）\n");
//        nRet = myPrintText(lineBetwen);
//
//        possdk.systemCutPaper(66, 0);
//        return nRet;
//    }
//
//    public int POSQueryStatus(byte[] pStatus) {
//
//        int result = 0;
//        int data_size = 0;
//        byte[] recbuf = new byte[64];// accept buffer
//
//        //Query Status
//        data_size = possdk.systemQueryStatus(recbuf, 4, port_type);
//
//        if ((recbuf[0] & 0x04) == 0x04) {
//            // Drawer open/close signal is HIGH (connector pin 3).
//            pStatus[0] |= 0x01;
//        } else {
//            pStatus[0] &= 0xFE;
//        }
//
//        if ((recbuf[0] & 0x08) == 0x08) {
//            // Printer is Off-line.
//            pStatus[0] |= 0x02;
//        } else {
//            pStatus[0] &= 0xFD;
//        }
//
//        if ((recbuf[0] & 0x20) == 0x20) {
//            // Cover is open.
//            pStatus[0] |= 0x04;
//        } else {
//            pStatus[0] &= 0xFB;
//        }
//
//        if ((recbuf[0] & 0x40) == 0x40) {
//            // Paper is being fed by the FEED button.
//            pStatus[0] |= 0x08;
//        } else {
//            pStatus[0] &= 0xF7;
//        }
//
//        if ((recbuf[1] & 0x40) == 0x40) {
//            // Error occurs.
//            pStatus[0] |= 0x10;
//        } else {
//            pStatus[0] &= 0xEF;
//        }
//
//        if ((recbuf[1] & 0x08) == 0x08) {
//            // Auto-cutter error occurs.
//            pStatus[0] |= 0x20;
//        } else {
//            pStatus[0] &= 0xDF;
//        }
//
//        if ((recbuf[2] & 0x03) == 0x03) {
//            // Paper near-end is detected by the paper roll near-end sensor.
//            pStatus[0] |= 0x40;
//        } else {
//            pStatus[0] &= 0xBF;
//        }
//
//        if ((recbuf[2] & 0x0C) == 0x0C) {
//            // Paper roll end detected by paper roll sensor.
//            pStatus[0] |= 0x80;
//        } else {
//            pStatus[0] &= 0x7F;
//        }
//        return data_size;
//    }
//
//    private int myPrintText(String str) {
//        try {
//            //GB18030
//            return possdk.textPrint(str.getBytes("windows-1256"), str.getBytes("windows-1256").length);
//        } catch (UnsupportedEncodingException e) {
//            return -1;
//        }
//
//    }
//
//    public int Sample_Restaurant_En() {
//        int nRet = 0;
//        possdk.textSelectCharSetAndCodePage(0, 37);
//        nRet = myPrintText("XxxxXxxx\n");
//        nRet = myPrintText("201 East 31st St.\n");
//        nRet = myPrintText("New York, NY 10000\n");
//        nRet = myPrintText("0344590786\n");
//        nRet = myPrintText("\n");
//        nRet = myPrintText("Server: Kristen                       Station: 7\n");
//        nRet = myPrintText("الخادم: كريستين                        المحطة: 7\n");
//        nRet = myPrintText("------------------------------------------------\n");
//        nRet = myPrintText("Order #: 123401                          Dine In\n");
//        nRet = myPrintText("Table: L6                               Guest: 2\n");
//        nRet = myPrintText("------------------------------------------------\n");
//        nRet = myPrintText("1 Lamb Embuchado.                          12.00\n");
//        nRet = myPrintText("1 NY Strip 6oz                             18.00\n");
//        nRet = myPrintText("1 Mozzarella Flatbread                     10.00\n");
//        nRet = myPrintText("1 Mahan                                     5.00\n");
//        nRet = myPrintText("\n");
//        nRet = myPrintText("Bar Subtotal:                               0.00\n");
//        nRet = myPrintText("Food Subtotal:                             45.00\n");
//        nRet = myPrintText("Tax:                                        3.99\n");
//        nRet = myPrintText("                                        ========\n");
//        nRet = myPrintText("TOTAL:            $49.00\n");
//        nRet = myPrintText("\n");
//        nRet = myPrintText(">> Ticket #: 11 <<\n");
//        nRet = myPrintText("4/23/2019 7:03:24 PM\n");
//        nRet = myPrintText("**********************************************\n");
//        nRet = myPrintText("Join our mailing list for exclusive offers\n");
//        nRet = myPrintText("\n");
//        nRet = myPrintText("www.XxxxXxxx.com\n");
//        nRet = myPrintText("\n");
//        nRet = myPrintText("15% Gratuity = $6.75\n");
//        nRet = myPrintText("18% Gratuity = $8.10\n");
//        nRet = myPrintText("20% Gratuity = $9.00\n");
//        nRet = myPrintText("22% Gratuity = $9.90\n");
//        nRet = myPrintText("\n");
//        nRet = myPrintText("**********************************************\n");
//        nRet = myPrintText("Join Us For Our $5 Happy Hour Daily 5-8pm\n");
//        possdk.systemCutPaper(66, 0);
//        return nRet;
//    }
//
//    public int SamplePageMode() {
//        int error_code = 0;
//        possdk.pageModeSetPrintArea(0, 0, 640, 500, 0);
//        //set print position
//        error_code = possdk.pageModeSetStartingPosition(20, 200);
//        error_code = myPrintText("This is page mode!\n");
//        possdk.systemFeedLine(6);
//        error_code = possdk.pageModePrint();
//
//        //*****************************************************************************************
//        //clear buffer in page mode
//        error_code = possdk.pageModeClearBuffer();
//        possdk.systemCutPaper(66, 0);
//        return error_code;
//    }
//
//    //打印条码样张
//    public int Sample_PrintBarCode() {
//        int result = 0;
//        myPrintText("UPC-A\n");
//        String data_UPCA = "123456789012";
//        result = possdk.barcodePrint1Dimension(data_UPCA, data_UPCA.length(), 65, 3, 60, 1, 0);
//        possdk.systemFeedLine(1);
//
//        myPrintText("UPC-E\n");
//        String data_UPCE = "023456789012";
//        result = possdk.barcodePrint1Dimension(data_UPCE, data_UPCE.length(), 66, 3, 60, 1, 0);
//        possdk.systemFeedLine(1);
//
//        myPrintText("EAN13\n");
//        String data_EAN13 = "3456789012345";
//        result = possdk.barcodePrint1Dimension(data_EAN13, data_EAN13.length(), 67, 3, 60, 1, 0);
//        possdk.systemFeedLine(1);
//
//        myPrintText("EAN8\n");
//        String data_EAN8 = "12345678";
//        result = possdk.barcodePrint1Dimension(data_EAN8, data_EAN8.length(), 68, 3, 60, 1, 0);
//        possdk.systemFeedLine(1);
//
//        myPrintText("CODE39\n");
//        String data_CODE39 = "01234ABCDE $%+-./";
//        result = possdk.barcodePrint1Dimension(data_CODE39, data_CODE39.length(), 69, 3, 60, 1, 0);
//        possdk.systemFeedLine(1);
//
//        myPrintText("ITF\n");
//        String data_ITF = "01234567891234";
//        result = possdk.barcodePrint1Dimension(data_ITF, data_ITF.length(), 70, 3, 60, 1, 0);
//        possdk.systemFeedLine(1);
//
//        myPrintText("CODEBAR\n");
//        String data_CODEBAR = "A0123456789$+-./:D";
//        result = possdk.barcodePrint1Dimension(data_CODEBAR, data_CODEBAR.length(), 71, 3, 60, 1, 0);
//        possdk.systemFeedLine(1);
//
//        myPrintText("CODE93\n");
//        String data_CODE93 = "01234ABCDE $%+-./";
//        result = possdk.barcodePrint1Dimension(data_CODE93, data_CODE93.length(), 72, 3, 60, 1, 0);
//        possdk.systemFeedLine(1);
//
//        myPrintText("CODE128\n");
//        String data_CODE128 = "{B" + "01234ABCDExyz";
//        try {
//            result = possdk.barcodePrint1Dimension(data_CODE128, data_CODE128.getBytes("GB18030").length, 73, 3, 60, 1, 0);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        possdk.systemFeedLine(1);
//
//        myPrintText("QR\n");
//        String data_QR = "0123456789ABCDEFGHIJKLmnopqrstuvwxyz汉字";
//        try {
//            result = possdk.barcodePrintQR(data_QR, data_QR.getBytes("GB18030").length, 0, 3, 2, 0);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        possdk.systemFeedLine(1);
//
//        myPrintText("PDF417\n");
//        String data_PDF417 = "0123456789ABCDEF汉字";
//        try {
//            result = possdk.barcodePrintPDF417(data_PDF417, data_PDF417.getBytes("GB18030").length, 2, 3, 3, 10, 2, 3, 6);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        possdk.systemFeedLine(1);
//
//        result = possdk.systemCutPaper(66, 0);
//
//        return result;
//    }
//
//    public int Sample_PrintImage(Context context, AIBottlePrintEntity aiBottlePrintEntity) {
//        int result = 0;
//        try {
//            Bitmap bitmap = TicketUtils.generateKDMedicalTicket(context, aiBottlePrintEntity);
//            result = possdk.imageStandardModeRasterPrint(bitmap, 640, 0);
////            result = myPrintText(aiBottlePrintEntity.getDesc2());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        result = possdk.systemCutPaper(66, 0);
//
//        return result;
//    }
//
//    public int getPrinterStatus() {
//        byte[] status = new byte[10];
//        int nReturn = POSQueryStatus(status);
//        int code = -1;
//        if (nReturn == 1000) {
//            byte[] btEmp = new byte[8];
//            byte bitindex = 1;
//            for (int i = 0; i < btEmp.length; i++) {
//                btEmp[i] = (byte) (status[0] & bitindex);
//                bitindex = (byte) (bitindex << 1);
//            }
//            String strSatus = "Query success\r\n";
//            int statusCode = 0;
//            if (btEmp[7] != 0) {
//                strSatus += "Paper End\r\n";
//                statusCode = 202;
//            }
//            if (btEmp[4] != 0) {
//                strSatus += "Printer Error\r\n";
//                statusCode = 201;
//            }
//            if (btEmp[6] != 0) {
//                strSatus += "Paper Near End\r\n";
//                statusCode = 200;
//            }
//            if (btEmp[1] != 0) {
//                strSatus += "Offline\r\n";
//            }
//            if (btEmp[3] != 0) {
//                strSatus += "Feeding\r\n";
//            }
//            if (btEmp[5] != 0) {
//                strSatus += "Cutter Error\r\n";
//            }
//            Logger.i("[系统]打印机 状态获取成功：" + strSatus + " 原因：" + strSatus);
//            return statusCode;
//        } else {
//            Logger.i("[系统]打印机 状态获取失败：" + nReturn);
//        }
//        return code;
//    }
//}
