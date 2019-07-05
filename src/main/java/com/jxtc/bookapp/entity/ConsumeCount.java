package com.jxtc.bookapp.entity;

public class ConsumeCount {
    private int bookId;//图书id
    private String bookName;//图书名字
    private int chapterId;//章节id
    private String chapterName;//章节名
    private int consumeCount;//消费笔数
    private int consumeAmount;//消费阅币数
    private String copyRight;//书籍来源方
    private String category;//书籍类型

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getConsumeCount() {
        return consumeCount;
    }

    public void setConsumeCount(int consumeCount) {
        this.consumeCount = consumeCount;
    }

    public int getConsumeAmount() {
        return consumeAmount;
    }

    public void setConsumeAmount(int consumeAmount) {
        this.consumeAmount = consumeAmount;
    }

    public String getCopyRight() {
        return copyRight;
    }

    public void setCopyRight(String copyRight) {
        this.copyRight = copyRight;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
