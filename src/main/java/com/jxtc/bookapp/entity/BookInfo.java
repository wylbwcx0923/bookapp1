package com.jxtc.bookapp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class BookInfo {

    private Integer id;

    private Integer bookId;

    private String bookName;

    private String subtitle;

    private String author;

    private String keywords;

    private Integer words;

    private String category;

    private Integer parentCategoryId;

    private Integer childCategoryId;

    private Integer valid;

    private Integer completeState;

    private String producer;

    private Integer lastChapterId;

    private Integer levelCode;

    private Integer bookType;

    private String copyrightCode;

    private Integer feeUnit;

    private Integer pubType;

    private Integer copyBelong;

    private Float price;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date putawayDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String categoryName;

    private String picUrl;

    private Integer downCount;

    private Integer hitsCount;

    private String cpBookId;

    private String source;

    private Integer openSearch;

    private String recommendUrl;

    private Integer putaway;

    private int isVIP;

    private String description;

    private String searchKeyField;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getSearchKeyField() {
        return searchKeyField;
    }

    public void setSearchKeyField(String searchKeyField) {
        this.searchKeyField = searchKeyField == null ? null : searchKeyField.trim();
    }

    public int getIsVIP() {
        return isVIP;
    }

    public void setIsVIP(int isVIP) {
        this.isVIP = isVIP;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName == null ? null : bookName.trim();
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle == null ? null : subtitle.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author == null ? null : author.trim();
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords == null ? null : keywords.trim();
    }

    public Integer getWords() {
        return words;
    }

    public void setWords(Integer words) {
        this.words = words;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category == null ? null : category.trim();
    }

    public Integer getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Integer parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public Integer getChildCategoryId() {
        return childCategoryId;
    }

    public void setChildCategoryId(Integer childCategoryId) {
        this.childCategoryId = childCategoryId;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    public Integer getCompleteState() {
        return completeState;
    }

    public void setCompleteState(Integer completeState) {
        this.completeState = completeState;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer == null ? null : producer.trim();
    }

    public Integer getLastChapterId() {
        return lastChapterId;
    }

    public void setLastChapterId(Integer lastChapterId) {
        this.lastChapterId = lastChapterId;
    }

    public Integer getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(Integer levelCode) {
        this.levelCode = levelCode;
    }

    public Integer getBookType() {
        return bookType;
    }

    public void setBookType(Integer bookType) {
        this.bookType = bookType;
    }

    public String getCopyrightCode() {
        return copyrightCode;
    }

    public void setCopyrightCode(String copyrightCode) {
        this.copyrightCode = copyrightCode == null ? null : copyrightCode.trim();
    }

    public Integer getFeeUnit() {
        return feeUnit;
    }

    public void setFeeUnit(Integer feeUnit) {
        this.feeUnit = feeUnit;
    }

    public Integer getPubType() {
        return pubType;
    }

    public void setPubType(Integer pubType) {
        this.pubType = pubType;
    }

    public Integer getCopyBelong() {
        return copyBelong;
    }

    public void setCopyBelong(Integer copyBelong) {
        this.copyBelong = copyBelong;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Date getPutawayDate() {
        return putawayDate;
    }

    public void setPutawayDate(Date putawayDate) {
        this.putawayDate = putawayDate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updatsseTime) {
        this.updateTime = updateTime;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName == null ? null : categoryName.trim();
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl == null ? null : picUrl.trim();
    }

    public Integer getDownCount() {
        return downCount;
    }

    public void setDownCount(Integer downCount) {
        this.downCount = downCount;
    }

    public Integer getHitsCount() {
        return hitsCount;
    }

    public void setHitsCount(Integer hitsCount) {
        this.hitsCount = hitsCount;
    }

    public String getCpBookId() {
        return cpBookId;
    }

    public void setCpBookId(String cpBookId) {
        this.cpBookId = cpBookId == null ? null : cpBookId.trim();
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source == null ? null : source.trim();
    }

    public Integer getOpenSearch() {
        return openSearch;
    }

    public void setOpenSearch(Integer openSearch) {
        this.openSearch = openSearch;
    }

    public String getRecommendUrl() {
        return recommendUrl;
    }

    public void setRecommendUrl(String recommendUrl) {
        this.recommendUrl = recommendUrl == null ? null : recommendUrl.trim();
    }

    public Integer getPutaway() {
        return putaway;
    }

    public void setPutaway(Integer putaway) {
        this.putaway = putaway;
    }

    @Override
    public String toString() {
        return "BookInfo{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", bookName='" + bookName + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", author='" + author + '\'' +
                ", keywords='" + keywords + '\'' +
                ", words=" + words +
                ", category='" + category + '\'' +
                ", parentCategoryId=" + parentCategoryId +
                ", childCategoryId=" + childCategoryId +
                ", valid=" + valid +
                ", completeState=" + completeState +
                ", producer='" + producer + '\'' +
                ", lastChapterId=" + lastChapterId +
                ", levelCode=" + levelCode +
                ", bookType=" + bookType +
                ", copyrightCode='" + copyrightCode + '\'' +
                ", feeUnit=" + feeUnit +
                ", pubType=" + pubType +
                ", copyBelong=" + copyBelong +
                ", price=" + price +
                ", putawayDate=" + putawayDate +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", categoryName='" + categoryName + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", downCount=" + downCount +
                ", hitsCount=" + hitsCount +
                ", cpBookId='" + cpBookId + '\'' +
                ", source='" + source + '\'' +
                ", openSearch=" + openSearch +
                ", recommendUrl='" + recommendUrl + '\'' +
                ", putaway=" + putaway +
                ", isVIP=" + isVIP +
                '}';
    }
}