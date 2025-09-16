//package com.ycmachine.smartdevice.handler.logic;
//
//import android.content.Context;
//import android.os.SystemClock;
//
//import junit.framework.TestCase;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.RuntimeEnvironment;
//
//import leesche.smartrecycling.base.entity.Containers;
//import nu.pattern.OpenCV;
//
//@RunWith(RobolectricTestRunner.class)
//public class TransactionHandlerTest extends TestCase {
//
//    private Context context;
//    private TransactionHandler transactionHandler;
//
//    @Before
//    public void setUp() {
//        // load context
//        context = RuntimeEnvironment.getApplication();
//    }
//
//    @Test
//    public void testValidCountings() {
//        transactionHandler = TransactionHandler.getInstance();
//
//        transactionHandler.initTransaction();
//        transactionHandler.allChecksDone(
//                createAttempt("1", "111111", TransactionHandler.ALUMINIUM, TransactionHandler.CAN)
//        );
//        transactionHandler.validContainerPassedDoor();
//        transactionHandler.acceptPendingAttempts();
//
//        assertEquals(1, transactionHandler.getCountCan());
//        assertEquals(0, transactionHandler.getCountBottle());
//        assertEquals(0, transactionHandler.getCountRejected());
//
//        transactionHandler.allChecksDone(
//                createAttempt("2", "222222", TransactionHandler.PLASTIC, TransactionHandler.BOTTLE)
//        );
//        transactionHandler.validContainerPassedDoor();
//        transactionHandler.acceptPendingAttempts();
//
//        assertEquals(1, transactionHandler.getCountCan());
//        assertEquals(1, transactionHandler.getCountBottle());
//        assertEquals(0, transactionHandler.getCountRejected());
//
//        transactionHandler.rejectAttempt(TransactionHandler.INVALID_BARCODE,
//                createAttempt("3", "333333", TransactionHandler.PLASTIC, TransactionHandler.BOTTLE)
//        );
//
//        assertEquals(1, transactionHandler.getCountCan());
//        assertEquals(1, transactionHandler.getCountBottle());
//        assertEquals(1, transactionHandler.getCountRejected());
//    }
//
//    private Containers createAttempt(String attemptId, String barcode, String material, String containerType) {
//        Containers attempt = transactionHandler.createAttempt(barcode);
//        attempt.setAttemptId(attemptId);
//        attempt.setMaterial(material);
//        attempt.setContainerType(containerType);
//        return attempt;
//    }
//}