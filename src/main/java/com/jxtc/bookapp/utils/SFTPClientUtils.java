package com.jxtc.bookapp.utils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.jxtc.bookapp.config.SFTPConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.concurrent.TimeUnit;

@Component
@ConfigurationProperties(prefix = "sftp")
public class SFTPClientUtils {
    private static int downloadSleep;
    private static int downloadRetry;
    private static int uploadSleep;
    private static int uploadRettry;
    private static Logger LOGGER = LoggerFactory.getLogger(SFTPClientUtils.class);

    /**
     * 文件上传
     * * 将文件对象上传到sftp作为文件。文件完整路径=basePath+directory
     * * 目录不存在则会上传文件夹
     * * @param basePath服务器的基础路径
     * * @param directory上传到该目录
     * * @param sftpFileName sftp端文件名
     * * @param file文件对象
     */
    public synchronized static boolean upload(String basePath, String directory, String filePath) {
        boolean result = false;
        Integer i = 0;
        while (!result) {
            ChannelSftp sftp = SFTPConnectionFactory.getInstance().makeConnection();
            try {
                sftp.cd(basePath);
                sftp.cd(directory);
            } catch (SftpException e) {
                LOGGER.info("sftp文件上传，目录不存在开始创建");
                String[] dirs = directory.split("/");
                String tempPath = basePath;
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) continue;
                    tempPath += "/" + dir;
                    try {
                        sftp.cd(tempPath);
                    } catch (SftpException ex) {
                        try {
                            sftp.mkdir(tempPath);
                            sftp.cd(tempPath);
                        } catch (SftpException e1) {
                            LOGGER.error("sftp文件上传，目录创建失败，错误信息:" + e1.getMessage() + ex.getMessage());
                        }
                    }
                }
            }
            try {
                File file = new File(filePath);
                sftp.put(new FileInputStream(file), file.getName());
                if (i > 0) {
                    LOGGER.info("sftp重试文件上传成功，ftp路径:" + basePath + directory + ",文件名称:" + file.getName());
                } else {
                    LOGGER.info("sftp文件上传成功，ftp路径为" + basePath + directory + ",文件名称:" + file.getName());
                }
                result = true;
            } catch (Exception e) {
                i++;
                LOGGER.error("sftp文件上传失败，重试中。。。第" + i + "次，错误信息" + e.getMessage());
                if (i > uploadRettry) {
                    LOGGER.error("sftp文件上传失败，超过重试次数结束重试，错误信息" + e.getMessage());
                    return result;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(uploadSleep);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

        }

        return result;
    }

    /**
     * 下载文件。
     *
     * @param directory    下载目录
     * @param downloadFile 下载的文件
     * @param saveFile     存在本地的路径
     */
    public synchronized static boolean download(String directory, String downloadFile, String saveFile) {
        boolean result = false;
        Integer i = 0;
        while (!result) {
            ChannelSftp sftp = SFTPConnectionFactory.getInstance().makeConnection();
            if (directory != null && !"".equals(directory)) {
                try {
                    sftp.cd(directory);
                } catch (SftpException e) {
                    LOGGER.error("sftp文件下载，目录不存在，错误信息" + e.getMessage());
                }
            }
            File file = new File(saveFile + downloadFile);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e1) {
                LOGGER.error("sftp文件下载失败，本地目录不存在" + e1.getMessage());
            }
            try {
                sftp.get(downloadFile, fileOutputStream);
                if (i > 0) {
                    LOGGER.info("sftp文件重试下载成功,sftp地址:" + directory + ",本地文件地址:" + saveFile);
                } else {
                    LOGGER.info("sftp文件下载成功,sftp地址:" + directory + ",本地文件地址:" + saveFile);
                }
                result = true;
            } catch (SftpException e1) {
                i++;
                LOGGER.error("sftp文件下载失败，重试中。。。第" + i + "次，错误信息" + e1.getMessage());
                if (i > downloadRetry) {
                    LOGGER.error("ftp文件下载失败，超过重试次数结束重试，错误信息" + e1.getMessage());
                    return result;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(downloadSleep);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 删除文件
     *
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     */
    public synchronized static boolean delete(String directory, String deleteFile) {
        boolean result = false;
        ChannelSftp sftp = SFTPConnectionFactory.getInstance().makeConnection();
        try {
            sftp.cd(directory);
            sftp.rm(deleteFile);
        } catch (SftpException e) {
            e.printStackTrace();
        }
        result = true;
        return result;
    }

    public static int getDownloadSleep() {
        return downloadSleep;
    }

    public static void setDownloadSleep(int downloadSleep) {
        SFTPClientUtils.downloadSleep = downloadSleep;
    }

    public static int getDownloadRetry() {
        return downloadRetry;
    }

    public static void setDownloadRetry(int downloadRetry) {
        SFTPClientUtils.downloadRetry = downloadRetry;
    }

    public static int getUploadSleep() {
        return uploadSleep;
    }

    public static void setUploadSleep(int uploadSleep) {
        SFTPClientUtils.uploadSleep = uploadSleep;
    }

    public static int getUploadRettry() {
        return uploadRettry;
    }

    public static void setUploadRettry(int uploadRettry) {
        SFTPClientUtils.uploadRettry = uploadRettry;
    }
}