package com.example.web.controller;

import com.example.web.SysConst;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.entity.*;
import com.example.web.mapper.*;
import com.example.web.service.*;
import com.example.web.tools.dto.*;
import com.example.web.tools.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 投诉记录控制器
 */
@RestController()
@RequestMapping("/Complaint")
public class ComplaintController {
    @Autowired()
    private ComplaintService _ComplaintService;
    @Autowired()
    private ComplaintMapper _ComplaintMapper;

    /**
     * 投诉记录分页查询
     */
    @RequestMapping(value = "/List", method = RequestMethod.POST)
    @SneakyThrows
    public PagedResult<ComplaintDto> List(@RequestBody ComplaintPagedInput input) {
        return _ComplaintService.List(input);
    }

    /**
     * 单个投诉记录查询接口
     */
    @RequestMapping(value = "/Get", method = RequestMethod.POST)
    @SneakyThrows
    public ComplaintDto Get(@RequestBody ComplaintPagedInput input) {

        return _ComplaintService.Get(input);
    }

    /**
     * 投诉记录创建或则修改
     */
    @RequestMapping(value = "/CreateOrEdit", method = RequestMethod.POST)
    public ComplaintDto CreateOrEdit(@RequestBody ComplaintDto input) throws Exception {
        return _ComplaintService.CreateOrEdit(input);
    }

    /**
     * 投诉记录删除
     */
    @RequestMapping(value = "/Delete", method = RequestMethod.POST)
    public void Delete(@RequestBody IdInput input) {
        _ComplaintService.Delete(input);
    }


    /**
     * 投诉记录的处理
     */
    @RequestMapping(value = "/Handle", method = RequestMethod.POST)
    public void Handle(@RequestBody ComplaintDto input) {_ComplaintService.Handle(input);}
}
