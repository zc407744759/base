package com.wutos.base.domain.entity;

public class SeaweedFsUploadResponseEntity {
    private String fid;//文件唯一id
    private String fileName;//上传文件名
    private String fileUrl;//文件地址（指定了从节点地址，可能不可用）
    private String size;//文件大小

    private String error;//文件服务器内部出错时的回执信息;

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
