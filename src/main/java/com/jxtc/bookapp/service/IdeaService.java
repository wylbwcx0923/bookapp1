package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.Idea;
import com.jxtc.bookapp.utils.PageResult;

/**
 * @author wyl
 * 意见反馈服务
 */
public interface IdeaService {
    /**
     * 添加意见
     *
     * @param idea
     */
    void insertIdea(Idea idea);

    /**
     * 获得意见的列表
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageResult<Idea> getIdeaList(int pageIndex, int pageSize);

    /**
     * 通过id获得意见的详情
     *
     * @param id
     * @return
     */
    Idea findById(int id);

    /**
     * 删除意见
     *
     * @param ids
     */
    void deleteIdea(int[] ids);
}
