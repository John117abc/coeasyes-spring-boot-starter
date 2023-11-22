package com.coeasyes.mapper.impl;

import com.coeasyes.annotation.EsFieldCondition;
import com.coeasyes.domain.EsBaseDto;
import com.coeasyes.domain.EsField;
import com.coeasyes.domain.EsPage;
import com.coeasyes.mapper.EsBaseMapper;
import com.coeasyes.util.EsUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
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


    /**
     * 获取查询条件
     * @param esDto 查询条件
     * @param <T> 查询条件类型
     * @return 查询条件集合
     */
    @Deprecated
    private <T extends EsBaseDto> List<EsField>  getEsField(T esDto) throws IllegalAccessException {
        //获取esDto中的EsFieldCondition注解
        Class<?> myClass = esDto.getClass();
        Field[] fields = myClass.getDeclaredFields();
        List<EsField> esFields = new ArrayList<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(EsFieldCondition.class)) {
                EsFieldCondition annotation = field.getAnnotation(EsFieldCondition.class);
                field.setAccessible(true); // 允许访问私有字段
                if (annotation == null || field.get(esDto) == null) {
                    continue;
                }
                EsField esField = new EsField(annotation.fieldName(),field.get(esDto).toString(), annotation.fieldType(),annotation.sortType());
                esFields.add(esField);
            }
        }
        return esFields;
    }
}
