package com.example.web.dto;

import com.example.web.enums.*;
import com.example.web.tools.dto.BaseDto;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Date;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.example.web.entity.*;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 投诉记录类
 */
@Data
public class ComplaintDto extends BaseDto {


    /**
     * 流水编号
     */
    @JsonProperty("Id")
    private Integer Id;


    /**
     * 投诉用户
     */
    @JsonProperty("UserId")
    private Integer UserId;


    /**
     * 投诉内容
     */
    @JsonProperty("Content")
    private String Content;


    /**
     * 处理人
     */
    @JsonProperty("AdminId")
    private Integer AdminId;


    /**
     * 处理回复
     */
    @JsonProperty("Response")
    private String Response;



    /**
     * 处理状态
     */
    @JsonProperty("ComplaintStatus")
    private Integer ComplaintStatus;

    public String getComplaintStatusFormat() {
        return ComplaintStatusEnum.GetEnum(ComplaintStatus).toString();
    }

    private String ComplaintStatusFormat;

    @JsonProperty("UserDto")
    private AppUserDto UserDto;

    @JsonProperty("AdminDto")
    private AppUserDto AdminDto;

    /**
     * 把投诉记录传输模型转换成投诉记录实体
     */
    public Complaint MapToEntity() throws InvocationTargetException, IllegalAccessException {
        Complaint Complaint = new Complaint();
        BeanUtils.copyProperties(Complaint, this);
        return Complaint;
    }

}
