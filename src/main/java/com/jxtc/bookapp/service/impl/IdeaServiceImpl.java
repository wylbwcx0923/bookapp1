package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.entity.Idea;
import com.jxtc.bookapp.entity.IdeaExample;
import com.jxtc.bookapp.mapper.IdeaMapper;
import com.jxtc.bookapp.service.IdeaService;
import com.jxtc.bookapp.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class IdeaServiceImpl implements IdeaService {

    @Autowired
    private IdeaMapper ideaMapper;

    @Override
    public void insertIdea(Idea idea) {
        idea.setCreateTime(new Date());
        ideaMapper.insertSelective(idea);
    }

    @Override
    public PageResult<Idea> getIdeaList(int pageIndex, int pageSize) {
        PageResult<Idea> page = new PageResult<>();
        int offset = (pageIndex - 1) * pageSize;
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        IdeaExample example = new IdeaExample();
        example.createCriteria();
        int total = ideaMapper.countByExample(example);
        page.setTotal(total);
        List<Idea> ideaList = ideaMapper.selectIdeasForPage(offset, pageSize);
        page.setPageList(ideaList);
        return page;
    }

    @Override
    public Idea findById(int id) {
        return ideaMapper.selectByPrimaryKey(id);
    }

    @Override
    public void deleteIdea(int[] ids) {
        if (ids != null && ids.length > 0) {
            for (int id : ids) {
                ideaMapper.deleteByPrimaryKey(id);
            }
        }
    }

}
