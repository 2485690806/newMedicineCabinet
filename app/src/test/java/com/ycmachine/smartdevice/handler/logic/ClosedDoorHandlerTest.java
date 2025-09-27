package com.ycmachine.smartdevice.handler.logic;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import leesche.smartrecycling.base.entity.GridRegion;
import leesche.smartrecycling.base.qrcode.ImageCropper;


@RunWith(RobolectricTestRunner.class)
public class ClosedDoorHandlerTest {

    private Context context;

    @Before
    public void setUp() {
        // context holen
        context = RuntimeEnvironment.getApplication();

//        OpenCV.loadLocally();
    }

    // Hilfsmethode: Testbilder aus assets laden
    private String loadTestImageInAndroid(String fileName, String assetName) {
        try {
            InputStream is = getClass().getResourceAsStream("/" + assetName);
            String resourceFileName = getClass().getResource("/generated").toURI().getPath() + "/" + fileName;
            File tempFile = new File(resourceFileName);

            OutputStream os = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            is.close();
            os.close();
            return tempFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String prepareBlockingImage(String number) {
        return loadTestImageInAndroid("blocking-true-" + number + ".jpg", "BlockedDoor-blocked-true-" + number + ".jpg");
    }

    private String prepareNonBlockingImage(String number) {
        return loadTestImageInAndroid("blocking-false-" + number + ".jpg", "BlockedDoor-blocked-false-" + number + ".jpg");
    }

    @Test
    public void closedDoorBlockingTest() {
//        String path = loadTestImageInAndroid("/", "test.jpg");
//        System.out.println(path);
        GridRegion  gridRegion= new GridRegion(1, 100, 100, 400, "400");
        String cropGrid = ImageCropper.cropGrid(context, "test.jpg", gridRegion, 1);
        System.out.println("Cropped image path: " + cropGrid);
    }

    @Test
    public void closedDoorNonBlockingTest() {
    }

}