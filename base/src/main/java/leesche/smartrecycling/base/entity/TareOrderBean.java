package leesche.smartrecycling.base.entity;

public  class TareOrderBean {
        /**
         * buckleMoney : null
         * grossWeight : 2.78
         * money : null
         * unitName
         * tareId : 2
         * orderId : 202001111206201578715580211
         * tare : null
         * price : null
         * buckleWater : null
         * typeName : null
         * count : null
         * bucklePoint : null
         * typeCode : null
         */

        private String orderId;
        private String typeCode;
        private String typeName;
        private double grossWeight;
        private double tare;
        private double count;
        private double money;
        private String unitName;
        private int tareId;
        private int price;
        private double buckleWater;
        private int bucklePoint;
        private String status;
        private int buckleMoney;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(String typeCode) {
            this.typeCode = typeCode;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public double getGrossWeight() {
            return grossWeight;
        }

        public void setGrossWeight(double grossWeight) {
            this.grossWeight = grossWeight;
        }

        public double getTare() {
            return tare;
        }

        public void setTare(double tare) {
            this.tare = tare;
        }

        public double getCount() {
            return count;
        }

        public void setCount(double count) {
            this.count = count;
        }

        public double getMoney() {
            return money;
        }

        public void setMoney(double money) {
            this.money = money;
        }

        public int getTareId() {
            return tareId;
        }

        public void setTareId(int tareId) {
            this.tareId = tareId;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public double getBuckleWater() {
            return buckleWater;
        }

        public void setBuckleWater(double buckleWater) {
            this.buckleWater = buckleWater;
        }

        public int getBucklePoint() {
            return bucklePoint;
        }

        public void setBucklePoint(int bucklePoint) {
            this.bucklePoint = bucklePoint;
        }

        public int getBuckleMoney() {
            return buckleMoney;
        }

        public void setBuckleMoney(int buckleMoney) {
            this.buckleMoney = buckleMoney;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUnitName() {
            return unitName;
        }

        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }
    }
