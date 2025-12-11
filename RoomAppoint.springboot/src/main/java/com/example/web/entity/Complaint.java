package com.example.web.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.web.dto.ComplaintDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * 投诉信息表
 */
@Data
@TableName("`Complaint`")
public class Complaint extends BaseEntity {


    /**
     * 流水编号
     */
    @JsonProperty("SId")
    @TableField(value = "Id", updateStrategy = FieldStrategy.IGNORED)
    private Integer SId;

    /**
     * 用户
     */
    @JsonProperty("UserId")
    @TableField(value = "UserId", updateStrategy = FieldStrategy.IGNORED)
    private Integer UserId;

    /**
     * 投诉内容
     */
    @JsonProperty("Content")
    @TableField(value = "Content", updateStrategy = FieldStrategy.IGNORED)
    private String Content;

    /**
     * 处理人
     */
    @JsonProperty("AdminId")
    @TableField(value = "AdminId", updateStrategy = FieldStrategy.IGNORED)
    private Integer AdminId;

    /**
     * 处理状态
     */
    @JsonProperty("ComplaintStatus")
    @TableField(value = "ComplaintStatus", updateStrategy = FieldStrategy.IGNORED)
    private Integer ComplaintStatus;


    /**
     * 处理回复
     */

    @JsonProperty("Response")
    @TableField(value = "Response", updateStrategy = FieldStrategy.IGNORED)
    private String Response;

   /**
    * 把投诉记录实体转换成投诉记录传输模型
    */
  public ComplaintDto MapToDto() throws InvocationTargetException, IllegalAccessException {
      ComplaintDto ComplaintDto = new ComplaintDto();
       BeanUtils.copyProperties(ComplaintDto, this);
        return ComplaintDto;
    }

}
