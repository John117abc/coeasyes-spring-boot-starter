package com.coeasyes.domain;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @program: manage-microservice
 * @description: es查询集合list
 * @author: jiangchengxuan
 * @created: 2023/11/04 20:45
 */
@Data
@Accessors(chain = true)
public class EsSearchSourceBuilderCollection {

    private BoolQuery.Builder queryBuilder;

    private SortOptions.Builder sortBuilder;


}
