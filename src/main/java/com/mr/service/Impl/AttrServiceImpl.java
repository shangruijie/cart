package com.mr.service.Impl;

import com.mr.mapper.AttrMapper;
import com.mr.model.OBJECTTMallAttr;
import com.mr.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by shangruijie on 2018/11/6.
 */
@Service
public class AttrServiceImpl implements AttrService{
    @Autowired
    private AttrMapper attrMapper;

    @Override
    public List<OBJECTTMallAttr> findAttrClass2(Integer flbh2) {
        return attrMapper.findAttrClass2(flbh2);
    }
}
