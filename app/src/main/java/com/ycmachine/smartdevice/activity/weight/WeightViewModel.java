package com.ycmachine.smartdevice.activity.weight;//package com.example.myapplication1.activity.weight;
//
//import androidx.lifecycle.ViewModel;
//
//import com.letmelife.vrvending.device.Device;
//import com.letmelife.vrvending.device.operator.DeviceOperator;
//
//import javax.inject.Inject;
//
//import dagger.hilt.android.lifecycle.HiltViewModel;
//
//@HiltViewModel
//public class WeightViewModel extends ViewModel {
//
//    private final DeviceOperator deviceOperator;
//    private final Device device;
//
//    @Inject
//    public WeightViewModel(DeviceOperator deviceOperator, Device device) {
//        this.deviceOperator = deviceOperator;
//        this.device = device;
//    }
//
//    public Object readWeight() {
//        return deviceOperator.readWeightData(-1);
//    }
//}