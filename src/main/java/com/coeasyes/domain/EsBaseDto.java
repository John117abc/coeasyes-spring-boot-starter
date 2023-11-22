package com.coeasyes.domain;


import com.coeasyes.annotation.EsFieldCondition;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @program: manage-microservice
 * @description: es基础dto
 * @author: jiangchengxuan
 * @created: 2023/11/06 16:00
 */
public class EsBaseDto extends EsBaseData{

    /**
     * 当前页
     */
    private int pageNum;

    /**
     * 每页显示条数
     */
    private int pageSize;

    public EsBaseDto(){
        super();
        this.pageNum = 1;
        this.pageSize = 10;
    }

    //es字段

    private List<EsField> esFields;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<EsField> getEsFields() {
        return esFields;
    }

    public void setEsFields(List<EsField> esFields) {
        this.esFields = esFields;
    }

    public List<EsField> getInitializeEsFields() throws IllegalAccessException {
        //获取esDto中的EsFieldCondition注解
        Class<?> myClass = this.getClass();
        Field[] fields = myClass.getDeclaredFields();
        List<EsField> esFields = new ArrayList<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(EsFieldCondition.class)) {
                EsFieldCondition annotation = field.getAnnotation(EsFieldCondition.class);
                field.setAccessible(true); // 允许访问私有字段
                if (annotation == null) {
                    continue;
                }
                EsField esField = new EsField(annotation.fieldName(),getFieldTypeValue(field,this), annotation.fieldType(),annotation.sortType());
                esFields.add(esField);
            }
        }
        this.esFields = esFields;
        return esFields;
    }

    //判断field类型
    public <T> T getFieldTypeValue(Field field,Object o) throws IllegalAccessException {
            Object value = field.get(o);
            if (value == null || Objects.equals(value, "")) {
                return null;
            }

            if (field.getType() == Date.class) {
                // 把 Date 类型转化为 long
                return (T) String.valueOf(((Date) value).getTime());
            }

            if (field.getType().isPrimitive()
                    || field.getType().equals(String.class)
                    || field.getType().equals(Integer.class)
                    || field.getType().equals(Long.class)
                    || field.getType().equals(Double.class)
                    || field.getType().equals(Float.class)
                    || field.getType().equals(Boolean.class)) {
                return (T) value;
            }

            if (field.getType().equals(List.class)) {
                // 处理 List 类型
                return (T) value;
            }
            return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EsBaseDto esBaseDto = (EsBaseDto) o;
        return pageNum == esBaseDto.pageNum && pageSize == esBaseDto.pageSize && Objects.equals(esFields, esBaseDto.esFields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pageNum, pageSize, esFields);
    }

    @Override
    public String toString() {
        return "EsBaseDto{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", esFields=" + esFields +
                '}';
    }
}
