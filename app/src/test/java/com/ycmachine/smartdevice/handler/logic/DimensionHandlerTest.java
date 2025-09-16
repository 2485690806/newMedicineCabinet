//package com.ycmachine.smartdevice.handler.logic;
//
//import android.content.Context;
//
//import com.ycmachine.smartdevice.handler.logic.dimension.AiDimension;
//import com.ycmachine.smartdevice.handler.logic.dimension.CameraPerspektiveCorrection;
//
//import junit.framework.TestCase;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.RuntimeEnvironment;
//
//@RunWith(RobolectricTestRunner.class)
//public class DimensionHandlerTest extends TestCase {
//
//    private Context context;
//    private DimensionHandler handler;
//    private CameraPerspektiveCorrection correctionInstance;
//
//    @Before
//    public void setUp() {
//        // load context
//        context = RuntimeEnvironment.getApplication();
////        correctionInstance = new CameraPerspektiveCorrection();
////        handler = DimensionHandler.getInstance();
//    }
//    @Test
//    public void testRedBull() {
//        runDimensionTest("RedBull Dose", 135, 54, 211, 192, 164, 70);
//        runDimensionTest("RedBull Dose", 135, 54, 193, 192, 166, 70);
//        runDimensionTest("RedBull Dose", 135, 54, 218, 192, 166, 71);
//        runDimensionTest("RedBull Dose", 135, 54, 207, 191, 165, 68);
//        runDimensionTest("RedBull Dose", 135, 54, 237, 193, 165, 71);
//        runDimensionTest("RedBull Dose", 135, 54, 222, 192, 166, 70);
//    }
//
//    private void runDimensionTest(String objectType, double realWidth, double realHeight, int aiXInPixel, int aiYInPixel, int aiWidthInPixel, int aiHeightInPixel) {
//        AiDimension aiDimension = handler.calculateDimension(211,192,164,70);
//
//        // the AI detected pixel width of the object tansformed to millimeters
//        double rawCalculatedWidthInMillimeters = correctionInstance.ConvertWidthPixelToMillimeter(aiWidthInPixel);
//        // the AI detected pixel height of the object tansformed to millimeters
//        double rawCalculatedHeightInMillimeters = correctionInstance.ConvertHeightPixelToMillimeter(aiHeightInPixel);
//
//        double deviationWidthInMm =   realWidth - aiDimension.getWidth();
//        double deviationHeightInMm =   realHeight - aiDimension.getHeight();
//
//        System.out.println("Type: " + objectType);
//        System.out.println("    Real W/H: " + realWidth + " / " + realHeight + " mm; AI W/H: " + rawCalculatedWidthInMillimeters + " / " + rawCalculatedHeightInMillimeters + " mm; Corrected W/H: " + aiDimension.getWidth() + " / " + aiDimension.getHeight() + " mm; Deviation W: " + deviationWidthInMm + " mm; Abweichung H: " + deviationHeightInMm + " mm");
//
//    }
//}