package com.jxtc.bookapp.config;

/**
 * 本类用于生成Redis的Key
 */
public class RedisKey {
    /**
     * 获得章节详情的key
     *
     * @param bookId
     * @param chapterId
     * @return
     */
    public static String getChapterInfoKey(int bookId, int chapterId) {
        return "GET_CHAPTER_INFO" + bookId + "_" + chapterId;
    }

    public static String getChapterContentKey(int bookId, int chapterId) {
        return "GET_CHAPTER_CONTENT" + bookId + "_" + chapterId;
    }

    public static String getUserInfoKey(String userId) {
        return "GET_USERINFO" + userId;
    }
}
