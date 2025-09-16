package leesche.smartrecycling.base.entity;

import com.google.gson.JsonObject;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DevConfigEntity {
    private ThirdPartyApiServer thirdPartyApiServer;
    private AppViewConfig appViewConfig;
    private String timeZone;
    private String appFunction;
    private List<DeviceBoxType> deviceBoxType;
    private HardwareSetting hardwareSetting;
    private String version;
    private CarbonWallet carbonWallet;

    private AmountDisplayTypeEntity amountDisplayType;
    private    CurrencySymbolEntity currencySymbol;


    @Setter
    @Getter
    public static class AmountDisplayTypeEntity {

        private  String DSR;
        private  String points;
    }
    @Setter
    @Getter
    public static class CurrencySymbolEntity {

        private  String currency;


    }
    @Setter
    @Getter
    public static class CarbonWallet {
        private String walletLogo;
        private String status;
        private int campaignId;
    }

    @Setter
    @Getter
    public static class ThirdPartyApiServer {
        private String thirdServerStatus;
        private String mqttDomain;
        private String imageServer;
        private String mqttAccount;
        private String serverDomain;
        private String mqttTopic;
        private String tomrobotsServerStatus;
        private String mqttPassword;
        private String serverConnectionMethod;


    }

    @Setter
    @Getter
    public static class AppViewConfig {
        private ViewSetting viewSetting;
        private List<ViewLanguages> viewLanguages;
        private JsonObject viewPicture;


        @Setter
        @Getter
        public static class ViewSetting {
            private String displayTime;
            private String displayDate;
        }

        @Setter
        @Getter
        public static class ViewLanguages {
            private LanguageContent languageContent;
            private String logo;

            private Integer sortOrder;

            private String languageCode;
            private String languageName;

            // 检查是否有 sortOrder（根据你的业务逻辑定义）
            public boolean hasSortOrder() {
                // 例如，如果 sortOrder 为 0 表示没有设置
//                return sortOrder != 0;
                // 或者如果后台返回的是包装类型 Integer，可以判断是否为 null
                 return sortOrder != null;
            }

            @Setter
            @Getter
            public static class LanguageContent {
                private String donationModel;
                private StatusContentText statusContentText;
                private LoginType loginType;
                private String serviceContentText;
                private String technicalSupport;
                private JsonObject recyclingType;
                private String printReceipts;
                private String serviceTitleText;
                private StatusTitleText statusTitleText;
                private String finishButton;

                @Getter
                @Setter
                public static class StatusContentText {
                    private String reject1;
                    private String reject2;
                    private String reject3;
                    private String reject4;
                    private String reject5;
                    private String reject6;
                    private String reject7;
                    private String reject8;
                    private String reject9;
                    private String reject10;

                    private String ready;
                    private String processing;
                    private String fault;
                    private String welcome;
                    private String printing;
                    private String wellDone;

                    private String warning;
                    private String danger1;
                    private String danger2;
                    private String full;

                    private String loading1;
                    private String loading2;
                    private String loading3;
                    private String loading4;

                    private String fault2;
                    private String fault3;
                    private String warning4;
                    private String warning5;
                    private String disable;
                    private String maintenance;


                }

                @Setter
                @Getter
                public static class LoginType {
                    private String qrCodeText;
                    private String mobileText;
                    private String anonyouslyText;

                }

                @Setter
                @Getter
                public static class StatusTitleText {
                    private String ready;
                    private String reject;
                    private String processing;
                    private String fault;
                    private String welcome;
                    private String printing;
                    private String wellDone;
                    private String warning;
                    private String danger;
                    private String full;
                    private String disable;
                    private String maintenance;

                }
            }
        }
    }

    //{
//"barcodeDatabaseVerification": "close",
//        "printerModel": "Custom",
//        "weightThresholdSetting": 80,
//        "aiRecognition": "n",
//        "qrCodeScanner": "close",
//        "userOperationDurationSetting": 300,
//        "currentDetection": "open",
//        "phoneNumberLengthSetting": 11,
//        "imageUpload": "n",
//        "monitoringModel": "Onvif",
//        "transmission": "belt",
//        "weighingCell": "y",
//        "flatteningMechanism": "close",
//        "EANCodeType": [
//        "EAN-13",
//        "DM",
//        "EAN-8"
//        ]
    //  }

    @Getter
    @Setter
    public static class HardwareSetting {
        private String barcodeDatabaseVerification;
        private String printerModel;
        private Integer weightThresholdSetting;
        private String transmission;
        private String aiRecognition;
        private String qrCodeScanner;
        private Integer userOperationDurationSetting;
        private String currentDetection = "open";
        private String fraudHandling = "open";
        private Integer fraudTimeout = 10;
        private String weighingCell;
        private String flatteningMechanism;
        private Integer phoneNumberLengthSetting;
        private Integer startingSpeedOfMotor = 150;
        private Integer speedAfterMotorStartup = 350;
        private String imageUpload;
        private String monitoringModel;

        private Double aiHeightRatio = 0.299;
        private Double aiWidthRatio = 0.287;

        private String multithreading = "open";

        private String displayCompletionButton = "open";

        private String[] EANCodeType;

        private String[] detectionMaterial;
        private String motorPosition;

        private String displayBarcode; // 没有这个字段就不显示
        private String displayWeight;

    }

public static class DeviceBoxType {
        private Integer probeHeight;
        private Integer boxHeight;
        private Integer currentHeigth;
        private List<TypeList> typeList;
        private Integer currentCapacity;
        private String compressSwitch;
        private Integer currentHeight;
        private String takePhotoSwitch;
        private Integer fillValue;
        private Boolean isFull;
        private String closeBoxSwitch;
        private String openOtherBoxSwitch;
        private Integer overflowDegree;
        private String boxCode;

        public Integer getProbeHeight() {
            return probeHeight;
        }

        public void setProbeHeight(Integer probeHeight) {
            this.probeHeight = probeHeight;
        }

        public Integer getBoxHeight() {
            return boxHeight;
        }

        public void setBoxHeight(Integer boxHeight) {
            this.boxHeight = boxHeight;
        }

        public Integer getCurrentHeigth() {
            return currentHeigth;
        }

        public void setCurrentHeigth(Integer currentHeigth) {
            this.currentHeigth = currentHeigth;
        }

        public List<TypeList> getTypeList() {
            return typeList;
        }

        public void setTypeList(List<TypeList> typeList) {
            this.typeList = typeList;
        }

        public Integer getCurrentCapacity() {
            return currentCapacity;
        }

        public void setCurrentCapacity(Integer currentCapacity) {
            this.currentCapacity = currentCapacity;
        }

        public String getCompressSwitch() {
            return compressSwitch;
        }

        public void setCompressSwitch(String compressSwitch) {
            this.compressSwitch = compressSwitch;
        }

        public Integer getCurrentHeight() {
            return currentHeight;
        }

        public void setCurrentHeight(Integer currentHeight) {
            this.currentHeight = currentHeight;
        }

        public String getTakePhotoSwitch() {
            return takePhotoSwitch;
        }

        public void setTakePhotoSwitch(String takePhotoSwitch) {
            this.takePhotoSwitch = takePhotoSwitch;
        }

        public Integer getFillValue() {
            return fillValue;
        }

        public void setFillValue(Integer fillValue) {
            this.fillValue = fillValue;
        }

        public Boolean getIsFull() {
            return isFull;
        }

        public void setIsFull(Boolean isFull) {
            this.isFull = isFull;
        }

        public String getCloseBoxSwitch() {
            return closeBoxSwitch;
        }

        public void setCloseBoxSwitch(String closeBoxSwitch) {
            this.closeBoxSwitch = closeBoxSwitch;
        }

        public String getOpenOtherBoxSwitch() {
            return openOtherBoxSwitch;
        }

        public void setOpenOtherBoxSwitch(String openOtherBoxSwitch) {
            this.openOtherBoxSwitch = openOtherBoxSwitch;
        }

        public Integer getOverflowDegree() {
            return overflowDegree;
        }

        public void setOverflowDegree(Integer overflowDegree) {
            this.overflowDegree = overflowDegree;
        }

        public String getBoxCode() {
            return boxCode;
        }

        public void setBoxCode(String boxCode) {
            this.boxCode = boxCode;
        }

        public static class TypeList {
            private String backgroundColor;
            private String code;
            private String timePeriodPrice;
            private List<DetailType> detail_type;
            private String icon;
            private String type;
            private String weightRangePrice;
            private String unit;
            private Integer price;
            private String typeOpenTimeArea;
            private String name;
            private Integer dailyPostingWeight;
            private String id;

            public String getBackgroundColor() {
                return backgroundColor;
            }

            public void setBackgroundColor(String backgroundColor) {
                this.backgroundColor = backgroundColor;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getTimePeriodPrice() {
                return timePeriodPrice;
            }

            public void setTimePeriodPrice(String timePeriodPrice) {
                this.timePeriodPrice = timePeriodPrice;
            }

            public List<DetailType> getDetail_type() {
                return detail_type;
            }

            public void setDetail_type(List<DetailType> detail_type) {
                this.detail_type = detail_type;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getWeightRangePrice() {
                return weightRangePrice;
            }

            public void setWeightRangePrice(String weightRangePrice) {
                this.weightRangePrice = weightRangePrice;
            }

            public String getUnit() {
                return unit;
            }

            public void setUnit(String unit) {
                this.unit = unit;
            }

            public Integer getPrice() {
                return price;
            }

            public void setPrice(Integer price) {
                this.price = price;
            }

            public String getTypeOpenTimeArea() {
                return typeOpenTimeArea;
            }

            public void setTypeOpenTimeArea(String typeOpenTimeArea) {
                this.typeOpenTimeArea = typeOpenTimeArea;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Integer getDailyPostingWeight() {
                return dailyPostingWeight;
            }

            public void setDailyPostingWeight(Integer dailyPostingWeight) {
                this.dailyPostingWeight = dailyPostingWeight;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public static class DetailType {
                private String unit;
                private String code;
                private String name;
                private String icon;
                private String typeId;
                private Integer id;

                public String getUnit() {
                    return unit;
                }

                public void setUnit(String unit) {
                    this.unit = unit;
                }

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getIcon() {
                    return icon;
                }

                public void setIcon(String icon) {
                    this.icon = icon;
                }

                public String getTypeId() {
                    return typeId;
                }

                public void setTypeId(String typeId) {
                    this.typeId = typeId;
                }

                public Integer getId() {
                    return id;
                }

                public void setId(Integer id) {
                    this.id = id;
                }
            }
        }
    }
}
