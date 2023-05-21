package com.gm.webbackend.service.impl;

import com.gm.webbackend.dao.TestDao;
import com.gm.webbackend.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServiceImpl implements TestService{

    @Autowired
    TestDao testDao;

    @Override
    public List getAllList() {
        return testDao.getAllTest();
    }
}
