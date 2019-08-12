package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.entity.Manager;
import com.jxtc.bookapp.entity.ManagerExample;
import com.jxtc.bookapp.mapper.app.ManagerMapper;
import com.jxtc.bookapp.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private ManagerMapper managerMapper;

    @Override
    public void register(Manager manager) {
        managerMapper.insertSelective(manager);
    }

    @Override
    public Manager login(String username, String password) {
        System.out.println(username + "\t" + password);
        ManagerExample example = new ManagerExample();
        example.createCriteria().andUsernameEqualTo(username).andPasswordEqualTo(password);
        List<Manager> managers = managerMapper.selectByExample(example);
        if (managers != null && managers.size() > 0) {
            return managers.get(0);
        }
        return null;
    }

    @Override
    public boolean isExistUser(String username) {
        ManagerExample example = new ManagerExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<Manager> managers = managerMapper.selectByExample(example);
        if (managers != null && managers.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Manager> getList() {
        ManagerExample example = new ManagerExample();
        List<Manager> managers = managerMapper.selectByExample(example);
        return managers;
    }

    @Override
    public void delManagerById(int id) {
        managerMapper.deleteByPrimaryKey(id);
    }
}
