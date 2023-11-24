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
     * 根据主键查询数据
     * @param <T> es实体类型
     * @param clazz es实体类
     * @return es实体
     */
    <T extends EsBaseData> T selectById(T esBaseData,Class<?> clazz);

    /**
     * 根据主键删除数据
     * @param esBaseData es实体
     * @param <T> es实体类型
     * @return 是否成功
     */
    <T extends EsBaseData> boolean deleteById(T esBaseData);

    /**
     * 删除某个索引下的所有数据
     * @param indexName 索引名称
     * @return 是否成功
     */
    boolean deleteAll(String indexName);

    /**
     * 根据条件删除数据
     * @param <T> es基础dto类型
     * @param esBaseDto es基础dto
     * @return 是否成功
     */
    <T extends EsBaseDto> Long deleteByQuery(T esBaseDto);

    /**
     * 创建索引
     * @param indexName 索引名称
     * @return 是否成功
     */
    boolean createIndex(String indexName);

    /**
     * 删除索引以及下面所有数据
     * @param indexName 索引名称
     * @return 是否成功
     */
    boolean deleteIndex(String indexName);

    /**
     * 更新或新增数据
     * @param esBaseEntity es实体
     * @param clazz es实体类
     * @param <T> es实体类型
     * @return 是否成功
     */
    <T extends EsBaseData> boolean upsert(T esBaseEntity, Class<?> clazz);

    /**
     * 修改单条数据
     * @param esBaseEntity  es实体
     * @param clazz es实体类
     * @param <T> es实体类型
     * @return 是否成功
     */
    <T extends EsBaseData> boolean update(T esBaseEntity, Class<?> clazz);
}
