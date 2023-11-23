package com.coeasyes;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.coeasyes.config.CoEasyEsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * @program: manage-microservice
 * @description: es配置加载
 * @author: jiangchengxuan
 * @created: 2023/09/20 17:59
 */
@Slf4j
@Configuration
@ComponentScan("com.coeasyes")
@ConditionalOnProperty(prefix = "coeasyes",name = "enable",havingValue = "true",matchIfMissing = true)
@EnableConfigurationProperties({CoEasyEsProperties.class})
public class ElasticsearchConfig {

    @Autowired
    private CoEasyEsProperties coEasyEsProperties;

    private final String SSL_SCHEME_NAME = "https";

    //配置高级客户端
    @Bean(name = "client")
    public ElasticsearchClient initClient() {
        log.info("==========初始化ES客户端==========");
        HttpHost[] httpHosts = Arrays.stream(coEasyEsProperties.getHosts().split(",")).map(
                host -> {
                    String[] hostParts = host.split(":");
                    String hostName = hostParts[0];
                    int port = Integer.parseInt(hostParts[1]);
                    return new HttpHost(hostName, port, coEasyEsProperties.isEnableSsl() ? SSL_SCHEME_NAME : HttpHost.DEFAULT_SCHEME_NAME);
                }).toArray(HttpHost[]::new);
        RestClient restClient = coEasyEsProperties.isEnableSsl() ? sslConnect(httpHosts) : noSslConnect(httpHosts);
        return new ElasticsearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));
    }

    /**
     * 初始化ES客户端ssl连接
     * @param httpHosts es连接地址
     * @return RestClient
     */
    private RestClient sslConnect(HttpHost[] httpHosts) {
        try {
            SSLContext sslContext = TransportUtils
                    .sslContextFromHttpCaCrt(Paths.get(coEasyEsProperties.getCrtFile()).toFile());
            BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
            credsProv.setCredentials(
                    AuthScope.ANY, new UsernamePasswordCredentials(coEasyEsProperties.getUsername(), coEasyEsProperties.getPassword())
            );

            return RestClient
                    .builder(httpHosts)
                    .setHttpClientConfigCallback(hc -> hc
                            .setSSLContext(sslContext)
                            .setDefaultCredentialsProvider(credsProv)

                    ).build();
        }catch (Exception e) {
            log.error("初始化ES客户端失败", e);
            throw new BeanInitializationException("初始化ES客户端失败", e);
        }
    }

    /**
     * 初始化ES客户端无ssl连接
     * @param httpHosts es连接地址
     * @return RestClient
     */
    private RestClient noSslConnect(HttpHost[] httpHosts) {
        try {
            BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
            credsProv.setCredentials(
                    AuthScope.ANY, new UsernamePasswordCredentials(coEasyEsProperties.getUsername(), coEasyEsProperties.getPassword())
            );
            return RestClient
                    .builder(httpHosts)
                    .setHttpClientConfigCallback(hc -> hc
                            .setDefaultCredentialsProvider(credsProv)
                    ).build();
        }catch (Exception e) {
            log.error("初始化ES客户端失败", e);
            throw new BeanInitializationException("初始化ES客户端失败", e);
        }
    }
}
