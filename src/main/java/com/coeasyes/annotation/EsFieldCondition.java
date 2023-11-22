package com.coeasyes.annotation;


import com.coeasyes.domain.FieldTypeEnum;
import com.coeasyes.domain.SortTypeEnum;

import java.lang.annotation.*;

/**
 * @program: manage-microservice
 * @description: es字段查询条件
 * @author: jiangchengxuan
 * @created: 2023/11/06 15:45
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EsFieldCondition {
    //字段名称
    String fieldName();

    //查询方式
    FieldTypeEnum fieldType() default FieldTypeEnum.PRECISE_QUERY;

    //排序方式
    SortTypeEnum sortType() default SortTypeEnum.NONE;
}
