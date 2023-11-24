package com.coeasyes.core;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.security.ChangePasswordResponse;
import co.elastic.clients.elasticsearch.security.IndicesPrivileges;
import co.elastic.clients.elasticsearch.security.PutRoleResponse;
import co.elastic.clients.elasticsearch.security.PutUserResponse;
import com.coeasyes.exception.CoEsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: manage-microservice
 * @description: es用户工具
 * @author: jiangchengxuan
 * @created: 2023/11/06 15:59
 */
@Slf4j
@Component
public class EsUserUtils {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    //创建elasticsearch用户
    public boolean createUser(String username, String password, List<String> roles) {
        Assert.hasLength(username, "Elasticsearch exception username null");
        Assert.hasLength(password, "Elasticsearch exception password null");
        boolean result = false;
        try {
            //1.使用client获取操作索引对象
            //2.具体操作获取返回值
            //2.1 设置索引名称
            PutUserResponse putUserResponse = elasticsearchClient.security().putUser(p -> p.username(username).password(password).roles(roles));
            result = putUserResponse.created();
        } catch (IOException e) {
            log.error("elasticsearch createUser error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);

        }
        return result;
    }

    //创建elasticsearch角色
    public boolean createRole(String roleName, List<String> privileges,List<String> names) {
        Assert.hasLength(roleName, "Elasticsearch exception roleName null");
        List<IndicesPrivileges> privilegesList = new ArrayList<>();
        for (String privilege : privileges) {
            IndicesPrivileges.Builder builder = new IndicesPrivileges.Builder();
            builder.privileges(privilege);
            builder.names(names);
            privilegesList.add(builder.build());
        }
        boolean result = false;
        try {
            //1.使用client获取操作索引对象
            //2.具体操作获取返回值
            //2.1 设置索引名称
            PutRoleResponse putRoleResponse = elasticsearchClient.security().putRole(p -> p.name(roleName).indices(privilegesList));
            result = putRoleResponse.role().created();
        } catch (IOException e) {
            log.error("elasticsearch createRole error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new CoEsException("elasticsearch createRole error , meassage=" + e.getMessage());
        }
        return result;
    }

    //更改elasticsearch用户密码
    public boolean changePassword(String username, String password) {
        Assert.hasLength(username, "Elasticsearch exception username null");
        Assert.hasLength(password, "Elasticsearch exception password null");
        boolean result = true;
        try {
            //1.使用client获取操作索引对象
            //2.具体操作获取返回值
            //2.1 设置索引名称
            ChangePasswordResponse changePasswordResponse = elasticsearchClient.security().changePassword(p -> p.username(username).password(password));
        } catch (IOException e) {
            log.error("elasticsearch changePassword error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new CoEsException("elasticsearch changePassword error , meassage=" + e.getMessage());
        }
        return result;
    }
}
