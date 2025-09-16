package com.ycmachine.smartdevice.activity.weight;//package com.example.myapplication1.activity.weight;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//
//import com.letmelife.vrvending.BuildConfig;
//import com.letmelife.vrvending.R;
//import com.letmelife.vrvending.common.app.BaseFragment;
//import com.letmelife.vrvending.data.preference.PrefConst;
//import com.letmelife.vrvending.databinding.FragmentWeightBinding;
//
//import javax.inject.Inject;
//
//import dagger.hilt.android.AndroidEntryPoint;
//
//@AndroidEntryPoint
//public class WeightFragment extends BaseFragment<WeightViewModel, FragmentWeightBinding> {
//
//    @Inject
//    SharedPreferences sharedPreferences;
//
//    private int weightChannelNumber = 0;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_weight;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        loadWeightChannelNumber();
//    }
//
//    private void loadWeightChannelNumber() {
//        weightChannelNumber = sharedPreferences.getInt(
//                PrefConst.KEY_WEIGHT_CHANNEL_NUM,
//                BuildConfig.DEFAULT_WEIGHT_CHANNEL_NUM
//        );
//    }
//
//    @Override
//    protected void initViews(@NonNull View root) {
//        binding.readWeight.setOnClickListener(view -> {
//            WeightData[] data = viewModel.readWeight();
//            binding.layer1Weight.setText(data[0].weight == -1 ? "未连接或故障" : String.valueOf(data[0].weight));
//            binding.layer2Weight.setText(data[1].weight == -1 ? "未连接或故障" : String.valueOf(data[1].weight));
//            binding.layer3Weight.setText(data[2].weight == -1 ? "未连接或故障" : String.valueOf(data[2].weight));
//            binding.layer4Weight.setText(data[3].weight == -1 ? "未连接或故障" : String.valueOf(data[3].weight));
//            binding.layer5Weight.setText(data[4].weight == -1 ? "未连接或故障" : String.valueOf(data[4].weight));
//            binding.layer6Weight.setText(data[5].weight == -1 ? "未连接或故障" : String.valueOf(data[5].weight));
//            binding.layer7Weight.setText(data[6].weight == -1 ? "未连接或故障" : String.valueOf(data[6].weight));
//            binding.layer8Weight.setText(data[7].weight == -1 ? "未连接或故障" : String.valueOf(data[7].weight));
//            binding.layer9Weight.setText(data[8].weight == -1 ? "未连接或故障" : String.valueOf(data[8].weight));
//            binding.layer10Weight.setText(data[9].weight == -1 ? "未连接或故障" : String.valueOf(data[9].weight));
//        });
//
////        for (int idx = 1; idx <= weightChannelNumber; idx++) {
////            TextView textView = new TextView(requireContext());
////            textView.setPadding(20, 20, 20, 20);
////            textView.setText("第" + idx + "层");
////            textView.setTextSize(20.0f);
////            binding.weightContainer.addView(textView);
////        }
//
//        subscribeUi();
//    }
//
//    private void subscribeUi() {
//        // 订阅UI逻辑
//    }
//}