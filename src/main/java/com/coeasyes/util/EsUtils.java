package com.coeasyes.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.util.ObjectBuilder;
import com.coeasyes.domain.*;
import com.coeasyes.exception.UtilException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * @program: manage-microservice
 * @description: es工具类
 * @author: jiangchengxuan
 * @created: 2023/09/21 23:46
 */
@Slf4j
@Component
public class EsUtils {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    /**
     * 判断索引是否存在
     */
    public boolean isIndexExist(String indexName) {
        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
        boolean exists = false;
        try {
            //1.使用client获取操作索引对象
            //2.具体操作获取返回值
            //2.1 设置索引名称
            BooleanResponse response = elasticsearchClient.indices().exists(c -> c.index(indexName));
            //3.根据返回值判断结果
            exists = response.value();
        } catch (IOException e) {
            log.error("elasticsearch isIndexExist error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new UtilException("elasticsearch isIndexExist error , meassage=" + e.getMessage());
        }
        return exists;
    }

    /**
     * 添加索引
     *
     * @param indexName
     * @return
     * @throws IOException
     */
    public boolean addIndex(String indexName){
        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
        CreateIndexResponse createIndexResponse = null;
        try {
            //1.使用client获取操作索引对象
            //2.具体操作获取返回值
            //2.1 设置索引名称
            createIndexResponse = elasticsearchClient.indices().create(c -> c.index(indexName));
        } catch (IOException e) {
            log.error("elasticsearch addindex error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new UtilException("elasticsearch addindex error , meassage=" + e.getMessage());
        }

        //3.根据返回值判断结果
        return createIndexResponse.acknowledged();
    }

    /**
     * 删除索引
     *
     * @param indexName
     * @return
     * @throws IOException
     */
    public boolean deleteIndex(String indexName) {
        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
        AcknowledgedResponse deleteRespone = null;
        try {
            //1.使用client获取操作索引对象
            //2.具体操作获取返回值
            //2.1 设置索引名称
            deleteRespone = elasticsearchClient.indices().delete(c -> c.index(indexName));
        } catch (IOException e) {
//            e.printStackTrace();
            log.error("elasticsearch deleteIndex error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new UtilException("elasticsearch deleteIndex error , meassage=" + e.getMessage());
        }
        //3.根据返回值判断结果
        return deleteRespone.acknowledged();
    }

    /**
     * 添加文档
     * @param data     数据
     * @param <T>     泛型
     * @return 是否成功
     */
    public <T extends EsBaseData> boolean addData(T data) {
        Assert.notNull(data, "Elasticsearch exception data null");
        Assert.hasLength(data.indexName, "Elasticsearch exception indexName null");
        IndexResponse response = null;
        try {
            //创建请求
            response = elasticsearchClient.index(i -> i
                    .index(data.getIndexName())
                    .id(data.getEsId())
                    .document(data)
            );
        } catch (Exception e) {
            log.error("elasticsearch addOrUpdateDoc error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new UtilException("elasticsearch addOrUpdateDoc error , meassage=" + e.getMessage());
        }
        return response.result().equals(Result.Created);
    }

    /**
     * 创建文档id存在则更新文档
     *
     * @param indexName
     * @param id
     * @param data
     * @throws IOException
     */
    public <T> boolean addOrUpdateData(String indexName, String id, T data,Class<T> clazz) {
        Assert.notNull(data, "Elasticsearch exception data null");
        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
        UpdateResponse<T> request = null;
        try {
            request = elasticsearchClient.update(
                    e -> e.index(indexName).id(id).refresh(Refresh.True).doc(data), clazz);
        } catch (Exception e) {
            log.error("elasticsearch addOrUpdateDoc error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new UtilException("elasticsearch addOrUpdateDoc error , meassage=" + e.getMessage());
        }
        return request.result().equals(Result.Updated);
    }

    public <T extends EsBaseData> boolean updateData(T data,Class<?> clazz){
        Assert.notNull(data, "Elasticsearch exception data null");
        Assert.hasLength(data.getIndexName(), "Elasticsearch exception indexName null");
        Assert.hasLength(data.getEsId(), "Elasticsearch exception id null");
        UpdateResponse<?> request = null;
        try {
            request = elasticsearchClient.update(
                    e -> e.index(data.getIndexName()).id(data.getEsId()).refresh(Refresh.True).doc(data), clazz);
        } catch (Exception e) {
            log.error("elasticsearch updateDoc error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new UtilException("elasticsearch updateDoc error , meassage=" + e.getMessage());
        }
        return request.result().equals(Result.Updated);
    }

    /**
     * 批量新增数据
     *
     * @param datas
     * @return
     */
    public boolean addBatchData(List<? extends EsBaseData> datas) {
        Assert.notEmpty(datas, "addBatchData elastaicsearch exception datas is null");
        if (datas.size() > 100000) {
            log.error("es add batch data too large{}", datas.size());
            throw new UtilException("es add batch data too large" + datas.size());
        }
        BulkResponse result = null;
        BulkRequest bulkRequest = null;
        try {
            BulkRequest.Builder br = new BulkRequest.Builder();
            datas.forEach(data -> br.operations(op -> op
                    .index(idx -> idx
                            .index(data.getIndexName())
                            .id(data.getEsId())
                            .document(data)
                    )
            ));
            bulkRequest = br.build();
            result = elasticsearchClient.bulk(bulkRequest);
        }catch (Exception e){
            log.error("elasticsearch addBatchData error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new UtilException("elasticsearch addBatchData error , meassage=" + e.getMessage());
        }
        // Log errors, if any
        if (result.errors()) {
            log.error("Bulk had errors");
            for (BulkResponseItem item: result.items()) {
                if (item.error() != null) {
                    log.error(item.error().reason());
                }
            }
        }
        return !result.errors();
    }


    //删除某个索引下的所有数据
    public boolean deleteAllData(String indexName) {
        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
        DeleteByQueryResponse response = null;
        if (!isIndexExist(indexName)) {
            log.error("elasticsearch deleteAllData error , indexName = {} is not exist", indexName);
            throw new UtilException("elasticsearch deleteAllData error , indexName = " + indexName + " is not exist");
        }
        try {
            Query.Builder queryBuilder = new Query.Builder();
            //默认全查询
            queryBuilder.matchAll(new MatchAllQuery.Builder().queryName("match_all").build());
            response = elasticsearchClient.deleteByQuery(
                    d -> d.index(indexName).query(queryBuilder.build()));
        } catch (Exception e) {
            log.error("elasticsearch deleteAllData error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new UtilException("elasticsearch deleteAllData error , meassage=" + e.getMessage());
        }
        return response.deleted() > 0;
    }
//    /**
//     * 通过id删除数据
//     *
//     * @param indexName
//     * @param id
//     * @return
//     */
//    public boolean deleteDataById(String indexName, String id) {
//        DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
//        DeleteResponse response = null;
//        try {
//            response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
//        } catch (IOException e) {
//            log.error("elasticsearch deleteDocById error , meassage = {}", e.getMessage());
//            //打印轨迹
//            log.error(e.getMessage(), e);
//            throw new UtilException("elasticsearch deleteDataById error , meassage=" + e.getMessage());
//        }
//        return response.getResult().equals(DocWriteResponse.Result.DELETED);
//    }
//
//
//    /**
//     * 通过条件删除数据
//     *
//     * @param indexName
//     * @param conditionFileds
//     * @return
//     */
//    public boolean deleteDataByCondition(String indexName, List<EsField> conditionFileds) {
//        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
//        Assert.notEmpty(conditionFileds, "Elasticsearch exception conditionFileds null");
//        BulkByScrollResponse resp = null;
//        try {
//            DeleteByQueryRequest request = new DeleteByQueryRequest(indexName);
////            SearchSourceBuilder searchSourceBuilder = buildSearchSourceBuilder(conditionFileds);
////            request.getSearchRequest().source(searchSourceBuilder);
//            // 更新时版本冲突
//            request.setConflicts("proceed");
//            //构建条件
//            setDeletCondition(conditionFileds, request);
//            // 刷新索引
//            request.setRefresh(true);
//
//            resp = restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
//        } catch (Exception e) {
//            log.error("elasticsearch deleteDataByCondition error , meassage = {}", e.getMessage());
//            //打印轨迹
//            log.error(e.getMessage(), e);
//            throw new UtilException("elasticsearch deleteDataByCondition error , meassage=" + e.getMessage());
//        }
//        return resp.getStatus().getDeleted() > 0;
//    }
//
//
//    /**
//     * 通过条件更新数据
//     *
//     * @param indexName
//     * @param conditionFileds
//     * @return
//     */
//    public boolean updateDataByCondition(String indexName, List<EsField> conditionFileds, Object data) {
//        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
//        Assert.notEmpty(conditionFileds, "Elasticsearch exception conditionFileds null");
//        Assert.notNull(data, "elasticsearch updateDataByCondition data is null ");
//        BulkByScrollResponse resp = null;
//        try {
//            UpdateByQueryRequest request = new UpdateByQueryRequest(indexName);
//            //设置分片并行
//            request.setSlices(2);
//            //设置版本冲突时继续执行
//            request.setConflicts("proceed");
//            //构建条件
//            setUpdateConfition(conditionFileds, request);
//            //设置更新完成后刷新索引 ps很重要如果不加可能数据不会实时刷新
//            request.setRefresh(true);
//            StringBuffer scriptContext = buildScriptContext(data);
//            //设置要修改的内容可以多个值多个用；隔开
//            request.setScript(new Script(scriptContext.toString()));
//            resp = restHighLevelClient.updateByQuery(request, RequestOptions.DEFAULT);
//        } catch (Exception e) {
//            log.error("elasticsearch updateDataByCondition error , meassage = {}", e.getMessage());
//            //打印轨迹
//            log.error(e.getMessage(), e);
//            throw new UtilException("elasticsearch updateDataByCondition error , meassage=" + e.getMessage());
//        }
//        return resp.getStatus().getUpdated() > 0;
//    }
//
//    /**
//     * 根据id查询文档
//     */
//    public <T> T selectDataById(String indexName, String id, Class<T> c) {
//        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
//        Assert.hasLength(id, "Elasticsearch exception id null");
//        GetResponse response = null;
//        try {
//            //设置查询的索引、文档
//            GetRequest indexRequest = new GetRequest(indexName, id);
//            response = restHighLevelClient.get(indexRequest, RequestOptions.DEFAULT);
//        } catch (Exception e) {
//            log.error("elasticsearch selectDataById error , meassage = {}", e.getMessage());
//            //打印轨迹
//            log.error(e.getMessage(), e);
//            throw new UtilException("elasticsearch selectDataById error , meassage=" + e.getMessage());
//        }
//        String res = response.getSourceAsString();
//        return JSONObject.parseObject(res, c);
//    }
//
//
    /**
     * 条件查询
     *
     * @param indexName
     * @param conditionFileds 条件
     * @param c               返回对象类型
     * @return
     */
    public <T> List<T> selectDataList(String indexName, List<EsField> conditionFileds, Class<T> c) {
        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
        Assert.notNull(c, "Class<T>  is null ");
        List<T> res = null;
        try {
            // 创建查询条件
            EsSearchSourceBuilderCollection searchSourceBuilderCollection = buildSearchSourceBuilder(conditionFileds);
            SearchResponse<T> response = elasticsearchClient.search(s -> s
                            .index(indexName)
                            .query(new Query(searchSourceBuilderCollection.getQueryBuilder().build()))
                            .sort(Arrays.asList(searchSourceBuilderCollection.getSortBuilder().build()))
                    ,c);
            List<Hit<T>> hits = response.hits().hits();
            //分析结果
            res = new ArrayList<>();
            for (Hit<T> hit: hits) {
                T data = hit.source();
                res.add(data);
                log.debug("Found data " + res + ", score " + hit.score());
            }
        } catch (Exception e) {
            log.error("elasticsearch selectDataList error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new UtilException("elasticsearch selectDataList error , meassage=" + e.getMessage());
        }
        return res;
    }
        /**
         * 条件查询
         *
         * @param indexName
         * @param conditionFileds 条件
         * @param c               返回对象类型
         * @return
         */
    public <T> EsPage<T> selectDataPage(String indexName, Integer pageNum, Integer pageSize, List<EsField> conditionFileds, Class<T> c) {
        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
        Assert.notNull(c, "Class<T>  is null ");
        List<T> res = null;
        //总记录数
        int total = 0;
        try {
            // 创建查询条件
            EsSearchSourceBuilderCollection searchSourceBuilderCollection = buildSearchSourceBuilder(conditionFileds);
            //打印DLS查询语句
//            log.info("DSL:{}", new Query(searchSourceBuilderCollection.getQueryBuilder().build()));
            SearchResponse<T> response = elasticsearchClient.search(s -> s
                            .index(indexName)
                            .query(new Query(searchSourceBuilderCollection.getQueryBuilder().build()))
                            .sort(Arrays.asList(searchSourceBuilderCollection.getSortBuilder().build()))
                            .from(((pageNum - 1) * pageSize))
                            .size(pageSize)
                    ,c);
            //分析结果
            List<Hit<T>> hits = response.hits().hits();
            res = new ArrayList<>();
            for (Hit<T> hit: hits) {
                T data = hit.source();
                res.add(data);
                log.debug("Found data " + res + ", score " + hit.score());
            }
            total = (int)response.hits().total().value();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("elasticsearch selectDataPage error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            e.printStackTrace();
            throw new UtilException("elasticsearch selectDataPage error , meassage=" + e.getMessage());
        }
        return new EsPage<>(pageNum, pageSize, total, res);
    }

    /**
     * 创建搜索条件
     *
     * @param conditionFileds
     * @return
     */
    private static EsSearchSourceBuilderCollection buildSearchSourceBuilder(List<EsField> conditionFileds) {
        return new EsSearchSourceBuilderCollection()
                .setQueryBuilder(buildQuery(conditionFileds)).setSortBuilder(buildSort(conditionFileds));
    }

    //排序条件
    private static SortOptions.Builder buildSort(List<EsField> sortFileds) {
        SortOptions.Builder sortBuilder = new SortOptions.Builder();
        sortBuilder.doc(doc -> doc.order(SortOrder.Asc));
//        sortBuilder.field(t->t.field("id").order(SortOrder.Asc));
        if (!CollectionUtils.isEmpty(sortFileds)) {
            for (EsField sortFiled : sortFileds) {
                switch (sortFiled.getSortTypeEnum()) {
                    case ORDER_ASC:
                        // 升序
                        sortBuilder.field(t->t.field(sortFiled.getField()).order(SortOrder.Asc));
                        break;
                    case ORDER_DESC:
                        // 降序
                        sortBuilder.field(t->t.field(sortFiled.getField()).order(SortOrder.Desc));
                        break;
                    default:
                        break;
                }
            }
        }
        return sortBuilder;
    }
    /**
     * 创建查询条件
     * @param conditionFileds 条件
     * @return Query.Builder
     */
    private static BoolQuery.Builder buildQuery(List<EsField> conditionFileds) {
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        // 构建 BoolQuery
        if (!CollectionUtils.isEmpty(conditionFileds)) {
            for (EsField condFiled : conditionFileds) {
                if (condFiled.getValue() != null) {
                    switch (condFiled.getFieldTypeEnum()) {
                        case VAGUE_QUERY:
                            // 模糊查询
                            boolQueryBuilder.must(new MatchQuery.Builder().query(condFiled.getValue().toString()).field(condFiled.getField()).operator(Operator.And).build()._toQuery());
                            break;
                        case PRECISE_QUERY:
                            //精确查询
                            boolQueryBuilder.must(new TermQuery.Builder().value(condFiled.getValue().toString()).field(condFiled.getField()).build()._toQuery());
                            break;
                        case GREATER_THAN_EQUAL:
                            //大于等于
                            boolQueryBuilder.must(new RangeQuery.Builder().gte(JsonData.of(condFiled.getValue())).field(condFiled.getField()).build()._toQuery());
                            break;
                        case LESS_THAN_EQUAL:
                            //小于等于
                            boolQueryBuilder.must(new RangeQuery.Builder().lte(JsonData.of(condFiled.getValue())).field(condFiled.getField()).build()._toQuery());
                            break;
                        default:
                            boolQueryBuilder.must(new MatchAllQuery.Builder().queryName("match_all").build()._toQuery());
                            break;
                    }
                }
            }
        }
        return boolQueryBuilder;
    }

}
