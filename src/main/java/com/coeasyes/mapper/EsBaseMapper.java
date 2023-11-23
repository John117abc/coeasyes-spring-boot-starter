package com.coeasyes.mapper;


import com.coeasyes.domain.EsBaseData;
import com.coeasyes.domain.EsBaseDto;
import com.coeasyes.domain.EsPage;

import java.util.List;

/**
 * @program: manage-microservice
 * @description: es基础mapper方法
 * @author: jiangchengxuan
 * @created: 2023/11/06 17:00
 */
public interface EsBaseMapper {

    /**
     * 列表查询
     * @param esBaseDto es基础dto
     * @param clazz es实体类
     * @param <T> es基础dto类型
     * @return 列表数据
     */
    <T extends EsBaseDto> List<?> select(T esBaseDto, Class<?> clazz);

    /**
     * 分页查询
     * @param esBaseDto es基础dto
     * @param clazz es实体类
     * @param <T> es基础dto类型
     * @return 分页数据
     */
    <T extends EsBaseDto> EsPage<?> selectPage(T esBaseDto, Class<?> clazz);

    /**
     * 新增数据
     * @param esBaseEntity es实体
     * @param <T> es实体类型
     * @return 是否成功
     */
    <T extends EsBaseData> boolean insert(T esBaseEntity);

    /**
     * 批量新增数据
     * @param esBaseEntityList
     * @param <T>
     * @return
     */
    <T extends EsBaseData> boolean insertBatch(List<T> esBaseEntityList);

    /**
     * 修改单条数据
     * @param esBaseEntity  es实体
     * @param clazz es实体类
     * @param <T> es实体类型
     * @return 是否成功
     */
    <T extends EsBaseData> boolean update(T esBaseEntity, Class<?> clazz);
}
