package com.jxtc.bookapp.config;

import com.jcraft.jsch.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * SFTP工厂类，用于获取SFTP的连接
 *
 * @author 不忘初心
 */
@Component
@ConfigurationProperties(prefix = "sftp")
public class SFTPConnectionFactory {
    /**
     * SFTP 登录用户名
     */
    private static String username;
    /**
     * SFTP 登录密码
     */
    private static String password;
    /**
     * 私钥
     */
    private static String privateKey;
    /**
     * SFTP 服务器地址IP地址
     */
    private static String ip;
    /**
     * SFTP 端口
     */
    private static int port;

    private static final SFTPConnectionFactory factory = new SFTPConnectionFactory();
    private ChannelSftp client;
    private Session session;

    private SFTPConnectionFactory() {

    }

    public static SFTPConnectionFactory getInstance() {
        return factory;
    }

    synchronized public ChannelSftp makeConnection() {

        if (client == null || session == null || !client.isConnected() || !session.isConnected()) {
            try {
                JSch jsch = new JSch();
                if (privateKey != null) {
                    jsch.addIdentity(privateKey);// 设置私钥 
                }
                session = jsch.getSession(username, ip, port);
                if (password != null) {
                    session.setPassword(password);
                }
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.connect();
                Channel channel = session.openChannel("sftp");
                channel.connect();
                client = (ChannelSftp) channel;
            } catch (JSchException e) {
                e.printStackTrace();
            }
        }

        return client;
    }

    /**
     * 关闭连接 server
     */
    public void logout() {
        if (client != null) {
            if (client.isConnected()) {
                client.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }


    public static String getUsername() {
        return username;
    }


    public static void setUsername(String username) {
        SFTPConnectionFactory.username = username;
    }


    public static String getPassword() {
        return password;
    }


    public static void setPassword(String password) {
        SFTPConnectionFactory.password = password;
    }


    public static String getPrivateKey() {
        return privateKey;
    }


    public static void setPrivateKey(String privateKey) {
        SFTPConnectionFactory.privateKey = privateKey;
    }


    public static String getIp() {
        return ip;
    }


    public static void setIp(String ip) {
        SFTPConnectionFactory.ip = ip;
    }


    public static int getPort() {
        return port;
    }


    public static void setPort(int port) {
        SFTPConnectionFactory.port = port;
    }
}