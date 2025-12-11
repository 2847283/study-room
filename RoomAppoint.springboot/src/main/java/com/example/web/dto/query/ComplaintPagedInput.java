package com.example.web.dto.query;

import com.example.web.tools.dto.PagedInput;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Complaint查询模型
 */
@NoArgsConstructor
@Data
public class ComplaintPagedInput extends PagedInput {

    /**
     * Id主键
     */
    @JsonProperty("Id")
    private Integer Id;
    /**
     * 投诉内容模糊查询条件
     */
    @JsonProperty("ContentLike")
    private String ContentLike;
    /**
     * 处理人
     */
    @JsonProperty("AdminId")
    private Integer AdminId;
    /**
     * 用户
     */
    @JsonProperty("UserId")
    private Integer UserId;


    /**
     * 处理状态
     */
    @JsonProperty("ComplaintStatus")
    private Integer ComplaintStatus;
}
