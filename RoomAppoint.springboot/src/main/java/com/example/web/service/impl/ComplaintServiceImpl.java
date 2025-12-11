package com.example.web.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.example.web.dto.ComplaintDto;
import com.example.web.dto.query.ComplaintPagedInput;
import com.example.web.enums.ComplaintStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.web.dto.AppointRecordDto;
import com.example.web.dto.query.AppointRecordPagedInput;
import com.example.web.dto.query.AppointRoomAppointStatusDataQueryInput;
import com.example.web.dto.query.AppointRoomUseRateQueryInput;
import com.example.web.entity.AppUser;
import com.example.web.entity.AppointRecord;
import com.example.web.entity.Integral;
import com.example.web.entity.Room;
import com.example.web.entity.Seat;
import com.example.web.entity.Complaint;
import com.example.web.enums.AppointDateTypeEnum;
import com.example.web.enums.AppointStatusEnum;
import com.example.web.mapper.AppUserMapper;
import com.example.web.mapper.ComplaintMapper;
import com.example.web.mapper.IntegralMapper;
import com.example.web.mapper.RoomMapper;
import com.example.web.mapper.SeatMapper;
import com.example.web.service.ComplaintService;
import com.example.web.tools.Extension;
import com.example.web.tools.dto.IdInput;
import com.example.web.tools.dto.IdsInput;
import com.example.web.tools.dto.PagedResult;
import com.example.web.tools.exception.CustomException;


import lombok.SneakyThrows;

/**
 * 投诉记录功能实现类
 */
@Service
public class ComplaintServiceImpl extends ServiceImpl<ComplaintMapper, Complaint>
        implements ComplaintService {

    /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper _AppUserMapper;
    /**
     * 操作数据库的Complaint表mapper对象
     */
    @Autowired
    private ComplaintMapper _ComplaintMapper;


    /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<Complaint> BuilderQuery(ComplaintPagedInput input) {
        // 声明一个支持投诉记录查询的(拉姆达)表达式
        LambdaQueryWrapper<Complaint> queryWrapper = Wrappers.<Complaint>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, Complaint::getId, input.getId())
                .eq(input.getCreatorId() != null, Complaint::getCreatorId, input.getCreatorId());
        // 如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getContentLike())) {
            queryWrapper = queryWrapper.like(Complaint::getContent, input.getContentLike());
        }

        if (input.getAdminId() != null) {
        queryWrapper = queryWrapper.eq(Complaint::getAdminId, input.getAdminId());
        }

        if (input.getUserId() != null) {
            queryWrapper = queryWrapper.eq(Complaint::getUserId, input.getUserId());
        }

        if (input.getComplaintStatus() != null) {
            queryWrapper = queryWrapper.eq(Complaint::getComplaintStatus, input.getComplaintStatus());
        }

        return queryWrapper;
    }

    /**
     * 处理投诉记录对于的外键数据
     */
    private List<ComplaintDto> DispatchItem(List<ComplaintDto> items)
            throws InvocationTargetException, IllegalAccessException {

        for (ComplaintDto item : items) {
            // 查询出关联的创建用户信息

            AppUser AdminAppUserEntity = _AppUserMapper
                    .selectList(Wrappers.<AppUser>lambdaQuery().eq(AppUser::getId, item.getAdminId())).stream()
                    .findFirst().orElse(new AppUser());
            item.setAdminDto(AdminAppUserEntity.MapToDto());

            // 查询出关联的AppUser表信息
            AppUser UserEntity = _AppUserMapper
                    .selectList(Wrappers.<AppUser>lambdaQuery().eq(AppUser::getId, item.getUserId())).stream()
                    .findFirst().orElse(new AppUser());
            item.setUserDto(UserEntity.MapToDto());

        }
        return items;
    }

    /**
     * 投诉记录分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<ComplaintDto> List(ComplaintPagedInput input) {
        // 构建where条件+排序
        LambdaQueryWrapper<Complaint> queryWrapper = BuilderQuery(input);

        // 按创建时间从大到小排序 最新的显示在最前面
        queryWrapper = queryWrapper.orderByDesc(Complaint::getId);
        // 构建一个分页查询的model
        Page<Complaint> page = new Page<>(input.getPage(), input.getLimit());
        // 从数据库进行分页查询获取投诉记录数据
        IPage<Complaint> pageRecords = _ComplaintMapper.selectPage(page, queryWrapper);
        // 获取所有满足条件的数据行数
        Long totalCount = _ComplaintMapper.selectCount(queryWrapper);
        // 把Complaint实体转换成Complaint传输模型
        List<ComplaintDto> items = Extension.copyBeanList(pageRecords.getRecords(), ComplaintDto.class);
        // 计算表的数据
        items = DispatchItem(items);

        // 返回一个分页结构给前端
        return PagedResult.GetInstance(items, totalCount);

    }

    /**
     * 单个投诉记录查询
     */
    @SneakyThrows
    @Override
    public ComplaintDto Get(ComplaintPagedInput input) {
        if (input.getId() == null) {
            return new ComplaintDto();
        }
        PagedResult<ComplaintDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new ComplaintDto());
    }

    /**
     * 投诉记录创建或者修改
     */
    @SneakyThrows
    @Override
    public ComplaintDto CreateOrEdit(ComplaintDto input) {
        // 声明一个投诉记录实体
        Complaint Complaint = input.MapToEntity();
        // 调用数据库的增加或者修改方法
        saveOrUpdate(Complaint);
        // 把传输模型返回给前端
        return Complaint.MapToDto();
    }

    /**
     * 投诉记录删除
     */
    @Override
    public void Delete(IdInput input) {
        Complaint entity = _ComplaintMapper.selectById(input.getId());
        _ComplaintMapper.deleteById(entity);
    }

    /**
     * 评论评分
     */
    @SneakyThrows
    @Override
    public void Handle(ComplaintDto input) {
        input.setComplaintStatus(ComplaintStatusEnum.处理完成.index());
        Complaint Complaint = input.MapToEntity();
        saveOrUpdate(Complaint);

    }


}
