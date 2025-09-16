//package com.ycmachine.smartdevice.handler.logic;
//
//import static org.junit.Assert.assertThrows;
//
//import android.content.Context;
//import android.os.SystemClock;
//
//import com.herohan.rknn_yolov5.YoloDetector;
//import com.ycmachine.smartdevice.handler.logic.exception.FraudException;
//import com.ycmachine.smartdevice.handler.logic.exception.HandFoundException;
//import com.ycmachine.smartdevice.handler.logic.exception.TooManyObjectsException;
//import com.rvm.testcases.AbstractFraudTest;
//
//import junit.framework.TestCase;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.RuntimeEnvironment;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import leesche.smartrecycling.base.entity.Containers;
//import nu.pattern.OpenCV;
//
//@RunWith(RobolectricTestRunner.class)
//public class FraudHandlerTest extends AbstractFraudTest {
//
//    private Context context;
//
//    @Before
//    public void setUp() {
//        // context holen
//        context = RuntimeEnvironment.getApplication();
//        this.initLogHandlerMock();
//
//        OpenCV.loadLocally();
//    }
//
//    @Test
//    public void testFraud3() throws HandFoundException, TooManyObjectsException, FraudException {
//        FraudDetectionHandler handler = new FraudDetectionHandler();
//        handler.initValues();
//        Containers refContainer = new Containers();
//        refContainer.setAiX(133);
//        refContainer.setAiY(174);
//        refContainer.setAiW(578 - 133);
//        refContainer.setAiH(287 - 174);
//        handler.setCheckContainer(refContainer);
//
//        List<FraudDetectionHandler.EyeLightState> lightStates = new ArrayList<>();
//        lightStates.add(new FraudDetectionHandler.EyeLightState(0, false, true, true, false));
//        lightStates.add(new FraudDetectionHandler.EyeLightState(559, false, false, true, true));
//        lightStates.add(new FraudDetectionHandler.EyeLightState(763, false, false, false, true));
//        lightStates.add(new FraudDetectionHandler.EyeLightState(1089, false, false, false, false));
//        lightStates.add(new FraudDetectionHandler.EyeLightState(1298, true, false, false, false));
//        lightStates.add(new FraudDetectionHandler.EyeLightState(1381, false, false, false, false));
//        handler.setLightStates(lightStates);
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.9453125f, 163, 188, 444, 273),
//                new TestObject("cap", 0.9008789f, 164, 209, 201, 251)
//        ));
//        assertEquals(1, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.9458008f, 164, 188, 445, 274),
//                new TestObject("cap", 0.9057617f, 164, 209, 200, 251)
//        ));
//        assertEquals(2, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.9453125f, 164, 188, 444, 274),
//                new TestObject("cap", 0.90185547f, 164, 209, 200, 251)
//        ));
//        assertEquals(3, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.94628906f, 164, 188, 445, 274),
//                new TestObject("cap", 0.90771484f, 164, 209, 201, 250)
//        ));
//        assertEquals(4, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.9477539f, 167, 188, 447, 274),
//                new TestObject("cap", 0.8955078f, 166, 209, 204, 252)
//        ));
//        assertEquals(5, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.9501953f, 210, 189, 492, 275),
//                new TestObject("cap", 0.8955078f, 211, 210, 246, 251)
//        ));
//        assertEquals(6, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.9560547f, 243, 189, 528, 275),
//                new TestObject("cap", 0.875f, 243, 211, 277, 252)
//        ));
//        assertEquals(7, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.9448242f, 298, 189, 579, 275),
//                new TestObject("cap", 0.88671875f, 296, 212, 328, 253)
//        ));
//        assertEquals(8, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.9277344f, 365, 191, 551, 275),
//                new TestObject("cap", 0.82714844f, 366, 211, 397, 253)
//        ));
//        assertEquals(9, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.93066406f, 415, 187, 606, 273),
//                new TestObject("cap", 0.8754883f, 416, 213, 453, 256)
//        ));
//        assertEquals(10, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.9057617f, 489, 181, 639, 277),
//                new TestObject("cap", 0.79785156f, 490, 232, 539, 278)
//        ));
//        assertEquals(11, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        assertThrows(FraudException.class, () -> {
//            handler.processNewFrame(createAiResult(
//                    new TestObject("bottle", 0.9345703f, 0, 171, 169, 308)
//            ));
//        });
//    }
//
//    @Test
//    public void testOkWithLongPlasticAndSecondCan() throws HandFoundException, TooManyObjectsException, FraudException {
//        FraudDetectionHandler handler = new FraudDetectionHandler();
//        handler.initValues();
//        Containers refContainer = new Containers();
//        refContainer.setAiX(133);
//        refContainer.setAiY(174);
//        refContainer.setAiW(578 - 133);
//        refContainer.setAiH(287 - 174);
//        handler.setCheckContainer(refContainer);
//
//        List<FraudDetectionHandler.EyeLightState> lightStates = new ArrayList<>();
//        lightStates.add(new FraudDetectionHandler.EyeLightState(0, false, true, true, true));
//        lightStates.add(new FraudDetectionHandler.EyeLightState(517, false, false, true, true));
//        lightStates.add(new FraudDetectionHandler.EyeLightState(719, true, false, false, true));
//        lightStates.add(new FraudDetectionHandler.EyeLightState(937, true, false, false, false));
//        lightStates.add(new FraudDetectionHandler.EyeLightState(1035, true, true, false, false));
//        lightStates.add(new FraudDetectionHandler.EyeLightState(1169, false, true, false, false));
//        lightStates.add(new FraudDetectionHandler.EyeLightState(1336, false, false, true, false));
//        handler.setLightStates(lightStates);
//
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.92578125f, 150, 173, 622, 290),
//                new TestObject("cap", 0.8911133f, 151, 213, 191, 256)
//        ));
//        assertEquals(1, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.92529297f, 151, 173, 622, 291),
//                new TestObject("cap", 0.8930664f, 151, 214, 192, 256)
//        ));
//        assertEquals(2, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.92529297f, 151, 173, 622, 291),
//                new TestObject("cap", 0.8901367f, 151, 214, 192, 256)
//        ));
//        assertEquals(3, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.92285156f, 151, 172, 624, 291),
//                new TestObject("cap", 0.8901367f, 151, 213, 192, 256)
//        ));
//        assertEquals(4, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.9272461f, 154, 171, 611, 290),
//                new TestObject("cap", 0.8959961f, 153, 213, 193, 256)
//        ));
//        assertEquals(5, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.9238281f, 161, 173, 624, 289),
//                new TestObject("cap", 0.9082031f, 164, 214, 204, 257)
//        ));
//        assertEquals(6, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.93652344f, 262, 170, 639, 288),
//                new TestObject("cap", 0.8930664f, 262, 218, 301, 260)
//        ));
//        assertEquals(7, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.9248047f, 311, 156, 639, 291),
//                new TestObject("bottle", 0.9082031f, 0, 194, 99, 267),
//                new TestObject("cap", 0.8515625f, 314, 236, 353, 280)
//        ));
//        assertEquals(8, handler.getMainAiPositions().size());
//        assertEquals(1, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle",0.91845703f,0,196,129,271),
//                new TestObject("bottle",0.88427734f,349,158,639,292),
//                new TestObject("cap",0.82666016f,350,241,394,288)
//        ));
//        assertEquals(9, handler.getMainAiPositions().size());
//        assertEquals(2, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can",0.9194336f,0,195,180,266),
//                new TestObject("bottle",0.83935547f,0,195,180,267),
//                new TestObject("bottle",0.5058594f,513,281,639,340)
//        ));
//        assertEquals(10, handler.getMainAiPositions().size());
//        assertEquals(4, handler.getSecondAiPositions().size());
//
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can",0.9355469f,30,192,221,264)
//        ));
//        assertEquals(10, handler.getMainAiPositions().size());
//        assertEquals(5, handler.getSecondAiPositions().size());
//
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can",0.94433594f,106,197,283,266)
//        ));
//        assertEquals(10, handler.getMainAiPositions().size());
//        assertEquals(6, handler.getSecondAiPositions().size());
//
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can",0.95214844f,165,198,331,267)
//        ));
//        assertEquals(10, handler.getMainAiPositions().size());
//        assertEquals(7, handler.getSecondAiPositions().size());
//
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can",0.9501953f,251,198,417,266)
//        ));
//        assertEquals(10, handler.getMainAiPositions().size());
//        assertEquals(8, handler.getSecondAiPositions().size());
//
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can",0.95458984f,261,198,428,267)
//        ));
//        assertEquals(10, handler.getMainAiPositions().size());
//        assertEquals(9, handler.getSecondAiPositions().size());
//
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can",0.95410156f,262,198,428,267)
//        ));
//        assertEquals(10, handler.getMainAiPositions().size());
//        assertEquals(10, handler.getSecondAiPositions().size());
//
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can",0.953125f,262,198,428,267)
//        ));
//        assertEquals(10, handler.getMainAiPositions().size());
//        assertEquals(11, handler.getSecondAiPositions().size());
//
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can",0.95214844f,262,199,428,267)
//        ));
//        assertEquals(10, handler.getMainAiPositions().size());
//        assertEquals(12, handler.getSecondAiPositions().size());
//
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can",0.9536133f,262,198,428,266)
//        ));
//        assertEquals(10, handler.getMainAiPositions().size());
//        assertEquals(13, handler.getSecondAiPositions().size());
//
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can",0.953125f,262,198,428,266)
//        ));
//        assertEquals(10, handler.getMainAiPositions().size());
//        assertEquals(14, handler.getSecondAiPositions().size());
//
//    }
//
//    @Test
//    public void testOkWithLongPlastic() throws HandFoundException, TooManyObjectsException, FraudException {
//        FraudDetectionHandler handler = new FraudDetectionHandler();
//        handler.initValues();
//        Containers refContainer = new Containers();
//        refContainer.setAiX(133);
//        refContainer.setAiY(174);
//        refContainer.setAiW(578 - 133);
//        refContainer.setAiH(287 - 174);
//        handler.setCheckContainer(refContainer);
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.9296875f, 161, 174, 609, 287),
//                new TestObject("cap", 0.9038086f, 161, 209, 198, 253)
//        ));
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.92529297f, 133, 174, 578, 287),
//                new TestObject("cap", 0.9091797f, 134, 209, 172, 253)
//        ));
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.9291992f, 160, 174, 608, 287),
//                new TestObject("cap", 0.90478516f, 161, 209, 198, 253)
//        ));
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.9243164f, 160, 174, 608, 287),
//                new TestObject("cap", 0.9038086f, 161, 209, 198, 253)
//        ));
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.9291992f, 160, 174, 639, 287),
//                new TestObject("cap", 0.90527344f, 161, 209, 198, 253)
//        ));
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.9267578f, 315, 166, 638, 286),
//                new TestObject("cap", 0.88427734f, 316, 219, 351, 263)
//        ));
//        handler.processNewFrame(createAiResult(
//                new TestObject("bottle", 0.90478516f, 388, 158, 639, 296),
//                new TestObject("cap", 0.87402344f, 391, 237, 431, 283)
//        ));
//        handler.processNewFrame(createAiResult(
//                new TestObject("cap", 0.8125f, 503, 251, 555, 301)
//        ));
//    }
//
//    @Test
//    public void testOkWithSecondObject() throws HandFoundException, TooManyObjectsException, FraudException {
//        FraudDetectionHandler handler = new FraudDetectionHandler();
//        handler.initValues();
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.9550781f, 265, 198, 432, 267)
//        ));
//        assertEquals(1, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.953125f, 264, 198, 432, 267)
//        ));
//        assertEquals(2, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.95458984f, 264, 198, 431, 267)
//        ));
//        assertEquals(3, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.9550781f, 264, 198, 431, 267)
//        ));
//        assertEquals(4, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.9550781f, 265, 198, 432, 267),
//                new TestObject("bottle", 0.3095703f, 547, 114, 640, 198)
//        ));
//        assertEquals(5, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.95458984f, 267, 198, 436, 267)
//        ));
//        assertEquals(6, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.9560547f, 307, 199, 479, 268)
//        ));
//        assertEquals(7, handler.getMainAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().size());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.95703125f, 371, 199, 552, 268),
//                new TestObject("bottle", 0.90234375f, 0, 193, 102, 273)
//        ));
//        assertEquals(8, handler.getMainAiPositions().size());
//        assertEquals(371, handler.getMainAiPositions().get(handler.getMainAiPositions().size() - 1).resultEntity.getLeft());
//        assertEquals(1, handler.getSecondAiPositions().size());
//        assertEquals(0, handler.getSecondAiPositions().get(handler.getSecondAiPositions().size() - 1).resultEntity.getLeft());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.95214844f, 404, 198, 587, 269),
//                new TestObject("can", 0.78027344f, 1, 192, 147, 273)
//        ));
//        assertEquals(9, handler.getMainAiPositions().size());
//        assertEquals(404, handler.getMainAiPositions().get(handler.getMainAiPositions().size() - 1).resultEntity.getLeft());
//        assertEquals(2, handler.getSecondAiPositions().size());
//        assertEquals(1, handler.getSecondAiPositions().get(handler.getSecondAiPositions().size() - 1).resultEntity.getLeft());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.9501953f, 469, 198, 639, 269),
//                new TestObject("can", 0.9355469f, 26, 192, 213, 264),
//                new TestObject("hand", 0.62597656f, 0, 172, 44, 315)
//        ));
//        assertEquals(10, handler.getMainAiPositions().size());
//        assertEquals(469, handler.getMainAiPositions().get(handler.getMainAiPositions().size() - 1).resultEntity.getLeft());
//        assertEquals(3, handler.getSecondAiPositions().size());
//        assertEquals(26, handler.getSecondAiPositions().get(handler.getSecondAiPositions().size() - 1).resultEntity.getLeft());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.94677734f, 84, 192, 266, 264),
//                new TestObject("bottle", 0.87158203f, 524, 199, 638, 282)
//        ));
//        assertEquals(11, handler.getMainAiPositions().size());
//        assertEquals(524, handler.getMainAiPositions().get(handler.getMainAiPositions().size() - 1).resultEntity.getLeft());
//        assertEquals(4, handler.getSecondAiPositions().size());
//        assertEquals(84, handler.getSecondAiPositions().get(handler.getSecondAiPositions().size() - 1).resultEntity.getLeft());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.9506836f, 139, 200, 313, 271)
//        ));
//        assertEquals(11, handler.getMainAiPositions().size());
//        assertEquals(5, handler.getSecondAiPositions().size());
//        assertEquals(139, handler.getSecondAiPositions().get(handler.getSecondAiPositions().size() - 1).resultEntity.getLeft());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.9423828f, 197, 195, 362, 265)
//        ));
//        assertEquals(11, handler.getMainAiPositions().size());
//        assertEquals(6, handler.getSecondAiPositions().size());
//        assertEquals(197, handler.getSecondAiPositions().get(handler.getSecondAiPositions().size() - 1).resultEntity.getLeft());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.9580078f, 226, 197, 391, 268)
//        ));
//        assertEquals(11, handler.getMainAiPositions().size());
//        assertEquals(7, handler.getSecondAiPositions().size());
//        assertEquals(226, handler.getSecondAiPositions().get(handler.getSecondAiPositions().size() - 1).resultEntity.getLeft());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.95703125f, 226, 197, 391, 268)
//        ));
//        assertEquals(11, handler.getMainAiPositions().size());
//        assertEquals(8, handler.getSecondAiPositions().size());
//        assertEquals(226, handler.getSecondAiPositions().get(handler.getSecondAiPositions().size() - 1).resultEntity.getLeft());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.95996094f, 226, 198, 391, 268)
//        ));
//        assertEquals(11, handler.getMainAiPositions().size());
//        assertEquals(9, handler.getSecondAiPositions().size());
//        assertEquals(226, handler.getSecondAiPositions().get(handler.getSecondAiPositions().size() - 1).resultEntity.getLeft());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.9560547f, 226, 197, 392, 268)
//        ));
//        assertEquals(11, handler.getMainAiPositions().size());
//        assertEquals(10, handler.getSecondAiPositions().size());
//        assertEquals(226, handler.getSecondAiPositions().get(handler.getSecondAiPositions().size() - 1).resultEntity.getLeft());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.95703125f, 227, 197, 392, 268)
//        ));
//        assertEquals(11, handler.getMainAiPositions().size());
//        assertEquals(11, handler.getSecondAiPositions().size());
//        assertEquals(227, handler.getSecondAiPositions().get(handler.getSecondAiPositions().size() - 1).resultEntity.getLeft());
//
//        handler.processNewFrame(createAiResult(
//                new TestObject("can", 0.9609375f, 226, 198, 392, 268)
//        ));
//        assertEquals(11, handler.getMainAiPositions().size());
//        assertEquals(12, handler.getSecondAiPositions().size());
//        assertEquals(226, handler.getSecondAiPositions().get(handler.getSecondAiPositions().size() - 1).resultEntity.getLeft());
//
//    }
//
//
//}