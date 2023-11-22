package com.coeasyes.domain;

import java.util.Objects;

/**
 * @program: manage-microservice
 * @description: 查询条件对象 用于封装查询条件，查询条件包括字段名称，字段值，字段类型，查询方式，查询方式包括等于，不等于，大于，小于，大于等于，小于等于，模糊查询，范围查询，in查询，not in查询，查询方式默认为等于，如果不是等于，需要在子类中重写该字段名称
 * @author: jiangchengxuan
 * @created: 2023/09/22 00:13
 */
public class EsField<T> {
    //字段对应查询功能
    private FieldTypeEnum fieldTypeEnum;
    //排序方式
    private SortTypeEnum sortTypeEnum;
    //字段名称
    private String field;
    //字段对应的值
    private T value;
    //Gte范围查询使用
    private String gte;
    //lte范围查询使用
    private String lte;
    public EsField(String field ,T value , FieldTypeEnum fieldTypeEnum){
        this.value = value;
        this.field = field;
        this.fieldTypeEnum  = fieldTypeEnum;
    }

    public EsField(String field ,T value , FieldTypeEnum fieldTypeEnum,SortTypeEnum sortTypeEnum){
        this.value = value;
        this.field = field;
        this.fieldTypeEnum  = fieldTypeEnum;
        this.sortTypeEnum = sortTypeEnum;
    }


    public SortTypeEnum getSortTypeEnum() {
        return sortTypeEnum;
    }

    public void setSortTypeEnum(SortTypeEnum sortTypeEnum) {
        this.sortTypeEnum = sortTypeEnum;
    }

    public FieldTypeEnum getFieldTypeEnum() {
        return fieldTypeEnum;
    }

    public void setFieldTypeEnum(FieldTypeEnum fieldTypeEnum) {
        this.fieldTypeEnum = fieldTypeEnum;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getGte() {
        return gte;
    }

    public void setGte(String gte) {
        this.gte = gte;
    }

    public String getLte() {
        return lte;
    }

    public void setLte(String lte) {
        this.lte = lte;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EsField esField = (EsField) o;
        return fieldTypeEnum == esField.fieldTypeEnum && Objects.equals(field, esField.field) && Objects.equals(value, esField.value) && Objects.equals(gte, esField.gte) && Objects.equals(lte, esField.lte);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldTypeEnum, field, value, gte, lte);
    }

    @Override
    public String toString() {
        return "EsField{" +
                "fieldTypeEnum=" + fieldTypeEnum +
                ", field='" + field + '\'' +
                ", value='" + value + '\'' +
                ", gte='" + gte + '\'' +
                ", lte='" + lte + '\'' +
                '}';
    }
}
