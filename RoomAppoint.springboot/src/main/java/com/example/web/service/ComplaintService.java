package com.example.web.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.entity.*;
import com.example.web.tools.dto.*;
import com.example.web.enums.*;
import java.lang.reflect.InvocationTargetException;

import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
/**
 * 投诉功能的Service接口的定义清单
 */
public interface ComplaintService extends IService<Complaint> {

    /**
     *  投诉记录的分页查询方法接口定义
     */
    public PagedResult<ComplaintDto> List(ComplaintPagedInput input) ;
    /**
     * 投诉记录的新增或者修改方法接口定义
     */
    public ComplaintDto CreateOrEdit(ComplaintDto input);
    /**
     * 获取投诉记录信息
     */
    public ComplaintDto Get(ComplaintPagedInput input);
    /**
     * 预约记录删除
     */
    public void Delete(IdInput input);
    /**
     *  投诉记录的处理
     */
    @SneakyThrows
    void Handle(ComplaintDto input);
}
