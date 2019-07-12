package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.UserEmpirical;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface UserEmpiricalService {

    /**
     * 用户经验值增加和升级
     *
     * @param userId
     * @param empirical
     */
    @Transactional
    void upgrade(String userId, int empirical);

    /**
     * 通过用户Id查询经验值对象
     *
     * @param userId
     * @return
     */
    UserEmpirical findEmpiricalByUserId(String userId);

    /**
     * 做任务
     *
     * @param userId
     * @param taskType
     */
    @Transactional
    void doTask(String userId, int taskType);

    /**
     * 检查今天用户的任务是否完成
     *
     * @param userId
     * @return
     */
    Map<String, Boolean> checkTaskStatus(String userId);
}
