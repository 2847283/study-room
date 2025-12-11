package com.example.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.web.entity.AppointRecord;
import com.example.web.entity.Complaint;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预约记录表对应的Mapper
 */
@Mapper
public interface ComplaintMapper extends BaseMapper<Complaint> {

}
