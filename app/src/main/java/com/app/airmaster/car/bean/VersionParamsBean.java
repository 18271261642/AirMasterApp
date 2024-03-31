package com.app.airmaster.car.bean;

import java.util.List;

/**
 * 用于从后台获取固件信息的bean
 */
public class VersionParamsBean {

    private String matchCode;
    private List<ParamsListBean> mcuList;

    public String getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(String matchCode) {
        this.matchCode = matchCode;
    }

    public List<ParamsListBean> getMcuList() {
        return mcuList;
    }

    public void setMcuList(List<ParamsListBean> mcuList) {
        this.mcuList = mcuList;
    }

    public static class ParamsListBean{
        private String identificationCode;
        private String broadcastId;

        private String versionCode;

        public ParamsListBean() {
        }

        public ParamsListBean(String identificationCode, String broadcastId, String versionCode) {
            this.identificationCode = identificationCode;
            this.broadcastId = broadcastId;
            this.versionCode = versionCode;
        }

        public String getIdentificationCode() {
            return identificationCode;
        }

        public void setIdentificationCode(String identificationCode) {
            this.identificationCode = identificationCode;
        }

        public String getBroadcastId() {
            return broadcastId;
        }

        public void setBroadcastId(String broadcastId) {
            this.broadcastId = broadcastId;
        }

        public String getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(String versionCode) {
            this.versionCode = versionCode;
        }
    }
}
