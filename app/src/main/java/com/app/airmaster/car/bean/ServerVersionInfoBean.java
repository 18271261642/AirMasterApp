package com.app.airmaster.car.bean;

import java.util.List;


public class ServerVersionInfoBean {



    //是否是旋钮屏配套的手表
    private boolean isCarWatch;

    private String appVo;

    private List<FirmwareListDTO> firmwareList;


    public boolean isCarWatch() {
        return isCarWatch;
    }

    public void setCarWatch(boolean carWatch) {
        isCarWatch = carWatch;
    }

    public String getAppVo() {
        return appVo;
    }

    public void setAppVo(String appVo) {
        this.appVo = appVo;
    }

    public List<FirmwareListDTO> getFirmwareList() {
        return firmwareList;
    }

    public void setFirmwareList(List<FirmwareListDTO> firmwareList) {
        this.firmwareList = firmwareList;
    }


    public static class FirmwareListDTO {

        private String fileName;

        private String ota;

        private Integer versionCode;

        private Boolean forceUpdate;

        private String content;

        private String fileSize;

        private String platform;

        private String identificationCode;

        private String fileType;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getOta() {
            return ota;
        }

        public void setOta(String ota) {
            this.ota = ota;
        }

        public Integer getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(Integer versionCode) {
            this.versionCode = versionCode;
        }

        public Boolean isForceUpdate() {
            return forceUpdate;
        }

        public void setForceUpdate(Boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getIdentificationCode() {
            return identificationCode;
        }

        public void setIdentificationCode(String identificationCode) {
            this.identificationCode = identificationCode;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }
    }
}
