package com.coeasyes.domain;

/**
 * @program: manage-microservice
 * @description: 条件数据类型枚举 用于封装查询条件，查询条件包括字段名称，字段值，字段类型，查询方式，查询方式包括等于，不等于，大于，小于，大于等于，小于等于，模糊查询，范围查询，in查询，not in查询，查询方式默认为等于，如果不是等于，需要在子类中重写该字段名称
 * @author: jiangchengxuan
 * @created: 2023/09/22 00:14
 */
public enum FieldTypeEnum {
    //精准查询
    PRECISE_QUERY,
    //模糊查询
    VAGUE_QUERY
    //大于查询
    ,GREATER_THAN
    //大于等于查询
    ,GREATER_THAN_EQUAL
    //小于查询
    ,LESS_THAN
    //小于等于查询
    ,LESS_THAN_EQUAL
    //范围查询
    ,RANGE_QUERY
    //in查询
    ,IN_QUERY
    //not in查询
    ,NOT_IN_QUERY
    //不等于查询
    ,NOT_EQUAL_QUERY
}
