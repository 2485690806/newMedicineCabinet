//package com.ycmachine.smartdevice.handler.logic;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//
//import android.content.Context;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.RuntimeEnvironment;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//import nu.pattern.OpenCV;
//
//
//@RunWith(RobolectricTestRunner.class)
//public class ClosedDoorHandlerTest {
//
//    private Context context;
//
//    @Before
//    public void setUp() {
//        // context holen
//        context = RuntimeEnvironment.getApplication();
//
//        OpenCV.loadLocally();
//    }
//
//    // Hilfsmethode: Testbilder aus assets laden
//    private String loadTestImageInAndroid(String fileName, String assetName) {
//        try {
//            InputStream is = getClass().getResourceAsStream("/" + assetName);
//            String resourceFileName = getClass().getResource("/generated").toURI().getPath() + "/" + fileName;
//            File tempFile = new File(resourceFileName);
//
//            OutputStream os = new FileOutputStream(tempFile);
//            byte[] buffer = new byte[1024];
//            int read;
//            while ((read = is.read(buffer)) != -1) {
//                os.write(buffer, 0, read);
//            }
//            is.close();
//            os.close();
//            return tempFile.getAbsolutePath();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private String prepareBlockingImage(String number) {
//        return loadTestImageInAndroid("blocking-true-" + number + ".jpg", "BlockedDoor-blocked-true-" + number + ".jpg");
//    }
//
//    private String prepareNonBlockingImage(String number) {
//        return loadTestImageInAndroid("blocking-false-" + number + ".jpg", "BlockedDoor-blocked-false-" + number + ".jpg");
//    }
//
//    @Test
//    public void closedDoorBlockingTest() {
//        String refPath = loadTestImageInAndroid("blocking-true-ref.jpg","BlockedDoor-ReferenceImage.jpg");
//
//        ClosedDoorHandler doorHandler = new ClosedDoorHandler();
//        assertTrue("blocked-true-001",doorHandler.hasBlockingElements(refPath,prepareBlockingImage("001")));
//        assertTrue("blocked-true-002",doorHandler.hasBlockingElements(refPath,prepareBlockingImage("002")));
//        assertTrue("blocked-true-003",doorHandler.hasBlockingElements(refPath,prepareBlockingImage("003")));
//        //assertTrue("blocked-true-004",doorHandler.hasBlockingElements(refPath,prepareBlockingImage("004")));
//        assertTrue("blocked-true-005",doorHandler.hasBlockingElements(refPath,prepareBlockingImage("005")));
//    }
//
//    @Test
//    public void closedDoorNonBlockingTest() {
//        String refPath = loadTestImageInAndroid("blocking-true-ref.jpg","BlockedDoor-ReferenceImage.jpg");
//
//        ClosedDoorHandler doorHandler = new ClosedDoorHandler();
//        assertFalse("blocked-false-001",doorHandler.hasBlockingElements(refPath,prepareNonBlockingImage("001")));
//        assertFalse("blocked-false-002",doorHandler.hasBlockingElements(refPath,prepareNonBlockingImage("002")));
//        assertFalse("blocked-false-003",doorHandler.hasBlockingElements(refPath,prepareNonBlockingImage("003")));
//        //assertFalse("blocked-false-004",doorHandler.hasBlockingElements(refPath,prepareNonBlockingImage("004")));
//    }
//
//}