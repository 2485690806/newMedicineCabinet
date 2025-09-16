package leesche.smartrecycling.base.entity;

import com.google.gson.annotations.SerializedName;

public class OssEntity {


    /**
     * bucket : youyicloud-app
     * endpoint : oss-cn-shanghai.aliyuncs.com
     * host : https://youyicloud-app.oss-cn-shanghai.aliyuncs.com
     * uri
     * sts_config : {"SecurityToken":"CAISmwJ1q6Ft5B2yfSjIr5bPec3+nZ1x7pi9WEX3jjcsTcpigfDKhTz2IHFLfXBpAesXtvQ1nmhX5vodlrh+W4NIX0rNaY5t9ZlN9wqkbtJ0PAgdZfpW5qe+EE2/VjTJvqaLEdibIfrZfvCyESem8gZ43br9cxi7QlWhKufnoJV7b9MRLGbaAD1dH4UUXEgAzvUXLnzML/2gHwf3i27LdipStxF7lHl05NbYoKiV4QGMi0bhmK1H5dazAOD9NZYzYcgnC4nogrEpLPWR6kMKtUgWrpURpbdf5DLKsuuaB1Rs+BicO4LWiIY+c1YgP/BhSvYU9qKkyq0k5fagnoD22gtLOvpOTyPcSYavzc3JAuq1McwjcrL2K6J+exdw4FOfGoABm9d/lj3dfrdrLivZQuh1kNVZi/y001piqqWxz8L2ihjJn9EwOOMyCYNr1W0oNqoME2eAMtYT6v1dyon6IUqCKaAm9PUCL8SNrq8seCtAKX+29gzOxUoEpA3SChJp2Wul0pbE2LfvaW4ubRUxcrM936IrulO/CJI26xLJLB3trO4=","AccessKeyId":"STS.NUz2wJpBPYZVZcFj7yAENn5ag","AccessKeySecret":"BTKdKf7p827Fr8PbY4up5PzwTUwEMtPouiibvELGSwtX","RequestId:":"526E6CD9-3CBB-4474-AA04-D3CE1EAB236E","Expiration":"2020-11-27T04:52:01Z","StatusCode":200}
     */

    private String bucket;
    private String endpoint;
    private String host;
    private String region;
    private String uri;
    private StsConfigBean sts_config;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public StsConfigBean getSts_config() {
        return sts_config;
    }

    public void setSts_config(StsConfigBean sts_config) {
        this.sts_config = sts_config;
    }

    public static class StsConfigBean {
        /**
         * SecurityToken : CAISmwJ1q6Ft5B2yfSjIr5bPec3+nZ1x7pi9WEX3jjcsTcpigfDKhTz2IHFLfXBpAesXtvQ1nmhX5vodlrh+W4NIX0rNaY5t9ZlN9wqkbtJ0PAgdZfpW5qe+EE2/VjTJvqaLEdibIfrZfvCyESem8gZ43br9cxi7QlWhKufnoJV7b9MRLGbaAD1dH4UUXEgAzvUXLnzML/2gHwf3i27LdipStxF7lHl05NbYoKiV4QGMi0bhmK1H5dazAOD9NZYzYcgnC4nogrEpLPWR6kMKtUgWrpURpbdf5DLKsuuaB1Rs+BicO4LWiIY+c1YgP/BhSvYU9qKkyq0k5fagnoD22gtLOvpOTyPcSYavzc3JAuq1McwjcrL2K6J+exdw4FOfGoABm9d/lj3dfrdrLivZQuh1kNVZi/y001piqqWxz8L2ihjJn9EwOOMyCYNr1W0oNqoME2eAMtYT6v1dyon6IUqCKaAm9PUCL8SNrq8seCtAKX+29gzOxUoEpA3SChJp2Wul0pbE2LfvaW4ubRUxcrM936IrulO/CJI26xLJLB3trO4=
         * AccessKeyId : STS.NUz2wJpBPYZVZcFj7yAENn5ag
         * AccessKeySecret : BTKdKf7p827Fr8PbY4up5PzwTUwEMtPouiibvELGSwtX
         * RequestId: : 526E6CD9-3CBB-4474-AA04-D3CE1EAB236E
         * Expiration : 2020-11-27T04:52:01Z
         * StatusCode : 200
         */

        private String SecurityToken;
        private String AccessKeyId;
        private String AccessKeySecret;
        @SerializedName("RequestId:")
        private String _$RequestId115; // FIXME check this code
        private String Expiration;
        private int StatusCode;

        public String getSecurityToken() {
            return SecurityToken;
        }

        public void setSecurityToken(String SecurityToken) {
            this.SecurityToken = SecurityToken;
        }

        public String getAccessKeyId() {
            return AccessKeyId;
        }

        public void setAccessKeyId(String AccessKeyId) {
            this.AccessKeyId = AccessKeyId;
        }

        public String getAccessKeySecret() {
            return AccessKeySecret;
        }

        public void setAccessKeySecret(String AccessKeySecret) {
            this.AccessKeySecret = AccessKeySecret;
        }

        public String get_$RequestId115() {
            return _$RequestId115;
        }

        public void set_$RequestId115(String _$RequestId115) {
            this._$RequestId115 = _$RequestId115;
        }

        public String getExpiration() {
            return Expiration;
        }

        public void setExpiration(String Expiration) {
            this.Expiration = Expiration;
        }

        public int getStatusCode() {
            return StatusCode;
        }

        public void setStatusCode(int StatusCode) {
            this.StatusCode = StatusCode;
        }
    }
}
