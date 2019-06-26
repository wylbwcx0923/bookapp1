package com.jxtc.bookapp.config;

/**
 * 本类用于生成OSS文件访问对应的KEY
 * @author wyl
 */
public class OSSCacheKey {
    public static String buildDownloadChapterKey(String bookId, String chapterId) {
        StringBuilder sb = new StringBuilder();
        return sb.append("chapter/").append(bookId).append("/").append(chapterId).append(".txt").toString();
    }

    public static String buildChapterFileKey(String bookId) {
        StringBuilder sb = new StringBuilder();
        return sb.append("chapter/").append(bookId).append("/").append("chapterInfo.json").toString();
    }
}
