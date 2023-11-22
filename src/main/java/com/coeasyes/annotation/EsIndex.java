package com.coeasyes.annotation;

import java.lang.annotation.*;

/**
 * @program: manage-microservice
 * @description:
 * @author: jiangchengxuan
 * @created: 2023/11/06 19:13
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR})
public @interface EsIndex {
    String indexName();
}
