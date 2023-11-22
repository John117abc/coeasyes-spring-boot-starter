package com.coeasyes.domain;


import com.coeasyes.annotation.EsId;
import com.coeasyes.annotation.EsIndex;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @program: manage-microservice
 * @description: es基础数据类，所有es数据类都需要继承该类，用于统一管理es数据类的索引名称和类型名称，以及id字段的名称，id字段名称默认为id，如果不是id字段，需要在子类中重写该字段名称
 * @author: jiangchengxuan
 * @created: 2023/09/22 00:11
 */
public class EsBaseData {
    //索引
    public String indexName;
    //id
    public String esId;

    public EsBaseData() {
        initializeIdFromIndexName();
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getEsId() {
        //获取esDto中的EsFieldCondition注解
        Class<?> myClass = this.getClass();
        Field[] fields = myClass.getDeclaredFields();
        String esId = null;
        try{
            for (Field field : fields) {
                if (field.isAnnotationPresent(EsId.class)) {
                    EsId annotation = field.getAnnotation(EsId.class);
                    field.setAccessible(true); // 允许访问私有字段
                    if (annotation == null || field.get(this) == null) {
                        continue;
                    }
                    esId = field.get(this).toString();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        this.esId = esId;
        return esId;
    }

    public void setEsId(String esId) {
        this.esId = esId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EsBaseData that = (EsBaseData) o;
        return Objects.equals(indexName, that.indexName) && Objects.equals(esId, that.esId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indexName, esId);
    }

    @Override
    public String toString() {
        return "EsBaseData{" +
                "indexName='" + indexName + '\'' +
                ", esId='" + esId + '\'' +
                '}';
    }

    private void initializeIdFromIndexName() {
        EsIndex esIndexAnnotation = getClass().getAnnotation(EsIndex.class);
        if (esIndexAnnotation != null) {
            this.indexName = esIndexAnnotation.indexName();
        }
    }
}
