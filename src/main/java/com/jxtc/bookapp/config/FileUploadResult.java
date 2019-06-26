package com.jxtc.bookapp.config;

/**
 * @author wyl
 * 文件上传结果类
 */
public class FileUploadResult {
    // 文件唯一标识
    private String uid;
    // 文件名
    private String name;
    // 状态有：uploading done error removed
    private String status;
    // 服务端响应内容，如：'{"status": "success"}'
    private String response;

    public FileUploadResult() {
    }

    public FileUploadResult(String uid, String name, String status, String response) {
        this.uid = uid;
        this.name = name;
        this.status = status;
        this.response = response;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "FileUploadResult{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
