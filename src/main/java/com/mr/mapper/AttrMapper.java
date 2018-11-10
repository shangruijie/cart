package com.mr.mapper;

import com.mr.model.OBJECTTMallAttr;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by shangruijie on 2018/11/6.
 */
public interface AttrMapper {
    List<OBJECTTMallAttr> findAttrClass2(@Param("flbh2") Integer flbh2);
}
