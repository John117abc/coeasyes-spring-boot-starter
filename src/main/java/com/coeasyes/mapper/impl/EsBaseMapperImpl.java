package com.coeasyes.mapper.impl;

import com.coeasyes.domain.EsBaseData;
import com.coeasyes.domain.EsBaseDto;
import com.coeasyes.domain.EsPage;
import com.coeasyes.exception.CoEsException;
import com.coeasyes.mapper.EsBaseMapper;
import com.coeasyes.core.EsUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @program: manage-microservice
 * @description: es基础查询
 * @author: jiangchengxuan
 * @created: 2023/11/06 17:30
 */
@Slf4j
@Component
public class EsBaseMapperImpl implements EsBaseMapper {

    @Resource
    private EsUtils esUtils;

    @Override
    public <T extends EsBaseDto> List<?> select(T esDto, Class<?> clazz) {
        List<?> list = null;
        try {
            list = esUtils.selectDataList(esDto.getIndexName(),esDto.getInitializeEsFields(),clazz);
            return list;
        } catch (IllegalAccessException e) {
            log.error("获取查询条件失败",e);
            return null;
        }
    }

    @Override
    public <T extends EsBaseDto> EsPage<?> selectPage(T esBaseDto, Class<?> clazz) {
        EsPage<?> esPage = null;
        try {
            esPage = esUtils.selectDataPage(esBaseDto.getIndexName(),
                    esBaseDto.getPageNum(),
                    esBaseDto.getPageSize(),
                    esBaseDto.getInitializeEsFields(),
                    clazz);
            return esPage;
        } catch (IllegalAccessException e) {
            log.error("获取查询条件失败",e);
            return null;
        }
    }

    @Override
    public <T extends EsBaseData> boolean insert(T esBaseEntity) {
        return esUtils.addData(esBaseEntity);
    }

    @Override
    public <T extends EsBaseData> boolean insertBatch(List<T> esBaseEntityList) {
        return esUtils.addBatchData(esBaseEntityList);
    }

    @Override
    public <T extends EsBaseData> boolean update(T esBaseEntity, Class<?> clazz) {
        return esUtils.updateData(esBaseEntity,clazz);
    }

    @Override
    public <T extends EsBaseData> T selectById(T esBaseData,Class<?> clazz) {
        return esUtils.selectDataById(esBaseData.getIndexName(),esBaseData.getEsId(),clazz);
    }

    @Override
    public <T extends EsBaseData> boolean deleteById(T esBaseData) {
        return esUtils.deleteDataById(esBaseData.getIndexName(),esBaseData.getEsId());
    }

    @Override
    public boolean deleteAll(String indexName) {
        return esUtils.deleteAllData(indexName);
    }

    @Override
    public <T extends EsBaseDto> Long deleteByQuery(T esBaseDto) {
        try {
            return esUtils.deleteDataByCondition(esBaseDto.getIndexName(),esBaseDto.getInitializeEsFields());
        } catch (IllegalAccessException e) {
            throw new CoEsException("获取查询条件失败",e);
        }
    }

    @Override
    public boolean createIndex(String indexName) {
        return esUtils.addIndex(indexName);
    }

    @Override
    public boolean deleteIndex(String indexName) {
        if (!esUtils.isIndexExist(indexName)) {
            return true; // 如果索引不存在，无需进行删除操作，直接返回 true
        }
        return esUtils.deleteAllData(indexName) && esUtils.deleteIndex(indexName);
    }

    @Override
    public <T extends EsBaseData> boolean upsert(T esBaseEntity, Class<?> clazz) {
        return esUtils.addOrUpdateData(esBaseEntity.getIndexName(),esBaseEntity.getEsId(),esBaseEntity,clazz);
    }

}
