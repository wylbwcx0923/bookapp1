package com.jxtc.bookapp;

import java.io.File;

public class MyFile {
    public static void main(String[] args) {
        //定义查找文件的路径
        String filePath = "/Users/wangyuliang";
        //定义文件
        File file = new File(filePath);
        getPictures(file);
    }

    private static void getPictures(File file) {
        if (!file.exists()) {
            return;
        }
        //程序执行至此,说明文件路径一定存在
        for (File f : file.listFiles()) {
            //文件夹的话递归
            if (f != null && f.isDirectory()) {
                getPictures(f);
            } else {
                //如果是文件,判断它是否是.jpg或者.png结尾
                String fileName = f.getName();
                if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
                    System.out.println(fileName);
                }
            }
        }
    }
}
