package com.coeasyes.annotation;

import java.lang.annotation.*;

/**
 * @program: manage-microservice
 * @description: es数据id标识
 * @author: jiangchengxuan
 * @created: 2023/11/06 16:01
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface EsId {
}
