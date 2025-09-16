//package com.ycmachine.smartdevice.activity;
//
//
//import android.os.Bundle;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.alibaba.fastjson.JSON;
//import com.leesche.logger.Logger;
//
//import java.util.List;
//import java.util.Map;
//
//import lombok.var;
//
//public class TestMedPointAll   extends AppCompatActivity {
//
//    private static long nowEpoch() {
//        return System.currentTimeMillis() / 1000;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Logger.i("c测试");
//        new Thread(()->{
//
//            try {
//
//                MedPointClient client = new MedPointClient();
//
//            /* =========================
//               HEALTH
//               ========================= */
//                String health = client.getHealthStatus();
//                System.out.println("Health: " + health);
//
//            /* =========================
//               PAIRING / AUTH
//               ========================= */
//                String pairing = client.requestPairingCode();
//                System.out.println("Pairing: " + pairing);
//
//                // Extrae device_code y user_code si quieres mostrarlos en pantalla
//                var pairingJson = client.asJsonMap(pairing);
//                String deviceCode = String.valueOf(pairingJson.get("device_code"));
//                String userCode   = String.valueOf(pairingJson.get("user_code"));
//                System.out.println("Device code: " + deviceCode + " | User code: " + userCode);
//
//                // Completar pairing -> access/refresh token
//                String complete = client.completePairing(deviceCode);
//                System.out.println("Complete Pairing: " + complete);
//
//                var completeJson = client.asJsonMap(complete);
//                String accessToken  = String.valueOf(completeJson.get("access_token"));
//                String refreshToken = String.valueOf(completeJson.get("refresh_token"));
//
//                // Refresh token (opcional)
//                String refreshed = client.refreshAccessToken(refreshToken);
//                System.out.println("Refresh: " + refreshed);
//                accessToken = String.valueOf(client.asJsonMap(refreshed).get("access_token"));
//
//            /* =========================
//               MACHINE: BAGS (CONTENTS)
//               ========================= */
//                // Bag Loaded
//                Map<String, Object> bagLoaded = Map.of(
//                        "barcode", "ABC123456789",
//                        "location", "21",
//                        "pin", "123456",
//                        "timestamp", nowEpoch()
//                );
//                String bagLoadedResp = client.bagLoadedNotification(accessToken, bagLoaded);
//                System.out.println("Bag Loaded: " + bagLoadedResp);
//                String medpointBagId = String.valueOf(client.asJsonMap(bagLoadedResp).get("id"));
//
//                // Bag Update
//                Map<String, Object> bagUpdate = Map.of(
//                        "location", "26",
//                        "pin", "987654",
//                        "timestamp", nowEpoch()
//                );
//                String bagUpdateResp = client.bagUpdateNotification(accessToken, medpointBagId, bagUpdate);
//                System.out.println("Bag Updated: " + bagUpdateResp);
//
//                // Bag Removed
//                String bagRemovedResp = client.bagRemovedNotification(accessToken, medpointBagId);
//                System.out.println("Bag Removed: " + bagRemovedResp);
//
//            /* =========================
//               MACHINE: DISPENSES (bag collected)
//               ========================= */
//                // Opción A: con exención
//                Map<String, Object> collectedExempt = Map.of(
//                        "exemption", "A",                 // código de exención
//                        "timestamp", nowEpoch()
//                );
//                String collectedRespA = client.bagCollectedNotification(accessToken, medpointBagId, collectedExempt);
//                System.out.println("Bag Collected (exemption): " + collectedRespA);
//
//                // Opción B: con pago
//                Map<String, Object> collectedPaid = Map.of(
//                        "amount_paid", 12.50,             // importe pagado
//                        "timestamp", nowEpoch()
//                );
//                String collectedRespB = client.bagCollectedNotification(accessToken, medpointBagId, collectedPaid);
//                System.out.println("Bag Collected (paid): " + collectedRespB);
//
//            /* =========================
//               MACHINE: PRESCRIPTIONS (drop-off)
//               ========================= */
//                String dropOffResp = client.prescriptionDropOff(accessToken, "RXBARCODE-00123456789");
//                System.out.println("Prescription DropOff: " + dropOffResp);
//
//            /* =========================
//               MACHINE: EVENTS
//               ========================= */
//                // Ejemplo: puerta abierta y luego cerrada
//                Map<String, Object> eventOpen = Map.of(
//                        "type", "door",
//                        "event", "opened",
//                        "ts", nowEpoch()
//                );
//                String eventOpenResp = client.machineEvent(accessToken, eventOpen);
//                System.out.println("Event (door opened): " + eventOpenResp);
//
//                Map<String, Object> eventClose = Map.of(
//                        "type", "door",
//                        "event", "closed",
//                        "ts", nowEpoch()
//                );
//                String eventCloseResp = client.machineEvent(accessToken, eventClose);
//                System.out.println("Event (door closed): " + eventCloseResp);
//
//                // Otro ejemplo: error
//                Map<String, Object> eventError = Map.of(
//                        "type", "system",
//                        "event", "error",
//                        "code", "SENSOR_TIMEOUT",
//                        "message", "No response from weight sensor",
//                        "ts", nowEpoch()
//                );
//                String eventErrorResp = client.machineEvent(accessToken, eventError);
//                System.out.println("Event (error): " + eventErrorResp);
//
//            /* =========================
//               MACHINE: STATUS
//               ========================= */
//                // Ejemplo: lote de métricas
//                Map<String, Object> statusPayload = Map.of(
//                        "data", List.of(
//                                Map.of("name", "temperature", "value", 23.4, "ts", nowEpoch()),
//                                Map.of("name", "humidity",    "value", 45.2, "ts", nowEpoch()),
//                                Map.of("name", "cpu_load",    "value", 0.37, "ts", nowEpoch()),
//                                Map.of("name", "door_state",  "value", "closed", "ts", nowEpoch())
//                        )
//                );
//                String statusResp = client.machineStatusUpdate(accessToken, statusPayload);
//                System.out.println("Status Update: " + statusResp);
//
//            } catch (Exception e) {
//                // Manejo simple de errores para demo
//                Logger.i("故障"+ JSON.toJSONString(e));
//                e.printStackTrace();
//            }
//        }).start();
//    }
//}
