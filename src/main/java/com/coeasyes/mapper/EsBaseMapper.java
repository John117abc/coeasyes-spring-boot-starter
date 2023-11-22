package com.coeasyes.mapper;


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
    //列表查询
    <T extends EsBaseDto> List<?> select(T esBaseDto, Class<?> clazz);

    //分页查询
    <T extends EsBaseDto> EsPage<?> selectPage(T esBaseDto, Class<?> clazz);
}
