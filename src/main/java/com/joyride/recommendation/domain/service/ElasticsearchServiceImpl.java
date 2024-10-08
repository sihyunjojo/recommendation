package com.joyride.recommendation.domain.service;

import com.joyride.recommendation.domain.processor.result.SearchMultiResultProcessor;
import com.joyride.recommendation.domain.processor.SearchQueryFactory;
import com.joyride.recommendation.domain.processor.result.SearchResultProcessor;
import com.joyride.recommendation.interfaces.dto.RecommendDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;


import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchServiceImpl implements ElasticsearchService{

    private final SearchResultProcessor searchResultProcessor;
    private final SearchMultiResultProcessor searchMultiResultProcessor;
    private final SearchQueryFactory searchQueryFactory;

    @Value("${elasticsearch.username}")
    private String elasticsearchUserName;

    @Value("${elasticsearch.password}")
    private String elasticsearchPassword;

    @Value("${elasticsearch.host}")
    private String elasticsearchHost;

    @Value("${elasticsearch.port}")
    private String elasticsearchPort;

    @Value("${elasticsearch.ca-cert-path}")
    private String elasticsearchCaCertPath;

    public RecommendDto searchByAreaName(String areaName) throws IOException {
        try (CloseableHttpClient httpClient = createHttpClient()) {
            String index = "area_search_term";
            String searchUrl = "https://" + elasticsearchHost + ":" + elasticsearchPort + "/" + index + "/_search";
            HttpPost httpPost = new HttpPost(searchUrl);

            String requestBody = searchQueryFactory.createSearchByAreaNameQuery(areaName);

            httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                return new RecommendDto(searchResultProcessor.parseTopAreaNames(responseBody, 5));
            }
        } catch (Exception e) {
            log.error("Error occurred while searching Elasticsearch", e);
            throw new IOException("Failed to search Elasticsearch", e);
        }
    }


    public RecommendDto recommendBoardSearchTerm(String searchTerm) throws IOException {
        try (CloseableHttpClient httpClient = createHttpClient()) {
            String url = "https://" + elasticsearchHost + ":" + elasticsearchPort + "/_msearch";
            HttpPost httpPost = new HttpPost(url);

            String requestBody = searchQueryFactory.createMultiIndexSearchTermQuery(searchTerm);

            httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
            httpPost.setHeader("Content-Type", "application/x-ndjson");

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                return new RecommendDto(searchMultiResultProcessor.parseMultiIndexResults(responseBody, 5));
            }
        } catch (Exception e) {
            log.error("Error occurred while searching Elasticsearch", e);
            log.error("CA cert path: {}", elasticsearchCaCertPath);
            throw new IOException("Failed to search Elasticsearch", e);
        }
    }

    // sudo keytool -importcert -file /home/sihyun/certs/ca/ca.crt -alias elasticsearch -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit
    // 1. 자체 서명된 인증서 사용 대신 신뢰할 수 있는 CA 인증서 사용
    // 2. 호스트 이름 검증 활성화
    // 3. 특정 AuthScope로 제한
    // SSL 인증서를 신뢰할 수 있도록 설정된 HTTP 클라이언트를 생성합니다. (서비스용)
    private CloseableHttpClient createHttpClient() throws Exception {
        SSLContextBuilder sslBuilder = new SSLContextBuilder();
        sslBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());

        return HttpClients.custom()
                .setSSLContext(sslBuilder.build())
                .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                .setDefaultCredentialsProvider(getCredentialsProvider())
                .build();
    }

    private CredentialsProvider getCredentialsProvider() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(elasticsearchHost, Integer.parseInt(elasticsearchPort)),
                new UsernamePasswordCredentials(elasticsearchUserName, elasticsearchPassword));
        return credentialsProvider;
    }


}

