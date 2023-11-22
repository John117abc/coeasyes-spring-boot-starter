package com.coeasyes.util;

/**
 * @program: manage-microservice
 * @description: es分页工具
 * @author: jiangchengxuan
 * @created: 2023/11/06 14:58
 */
public class EsPageUtil {
    /**
     * 计算分页起始位置
     * @param page
     * @param size
     * @return
     */
    public static int getFrom(int page, int size) {
        return (page - 1) * size;
    }
}
