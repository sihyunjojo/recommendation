package com.joyride.recommendation.domain.service;

import com.joyride.recommendation.domain.processor.SearchQueryFactory;
import com.joyride.recommendation.domain.processor.result.SearchMultiResultProcessor;
import com.joyride.recommendation.domain.processor.result.SearchResultProcessor;
import com.joyride.recommendation.interfaces.dto.RecommendDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
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
import java.security.KeyStore;

import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchServiceDevImpl implements ElasticsearchService {

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

    @Override
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

    @Override
    public RecommendDto recommendBoardSearchTerm(String searchTerm) throws IOException {
        try (CloseableHttpClient httpClient = createHttpClient()) {
            String url = "https://" + elasticsearchHost + ":" + elasticsearchPort + "/_msearch";
            HttpPost httpPost = new HttpPost(url);

            String requestBody = searchQueryFactory.createMultiIndexSearchTermQuery(searchTerm);
            httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
            httpPost.setHeader("Content-Type", "application/x-ndjson");

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                log.debug("Elasticsearch response: {}", responseBody);
                return new RecommendDto(searchMultiResultProcessor.parseMultiIndexResults(responseBody, 5));
            }
        } catch (Exception e) {
            log.error("Error occurred while searching Elasticsearch", e);
            log.error("CA cert path: {}", elasticsearchCaCertPath);
            throw new IOException("Failed to search Elasticsearch", e);
        }
    }

    // SSL 인증서를 신뢰할 수 있도록 설정된 HTTP 클라이언트를 생성합니다. (개발용)
    private CloseableHttpClient createHttpClient() throws Exception {
        // SSL 인증서 설정을 위한 빌더 생성 (기본 키스토어를 사용하여 신뢰할 수 있는 인증서 설정)
        SSLContextBuilder sslBuilder = new SSLContextBuilder();
        sslBuilder.loadTrustMaterial(
                KeyStore.getInstance(KeyStore.getDefaultType()), // 기본 키스토어 사용
                new TrustSelfSignedStrategy() // 신뢰할 수 있는 자체 서명된 인증서 설정
        );

        // HTTP 클라이언트 인증 정보 설정
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(elasticsearchUserName, elasticsearchPassword));

        // 개발용으로 호스트 이름 검증을 비활성화 (NoopHostnameVerifier 사용)
        return HttpClients.custom()
                .setSSLContext(sslBuilder.build())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
    }

    /**
     * 점수 계산 로직:
     * 1. 기본 점수 (baseScore): nori 분석기를 통한 텍스트 일치 점수. 범위: 0-11 (예상)
     * 2. 게시물 수 가중치 (postCountFactor): 게시물 수에 따른 추가 점수
     * Math.log(postCount + 1) / Math.log(2)를 사용하여 게시물 수의 로그값을 계산합니다.
     * 이 값에 0.8을 곱하여 게시물 수의 영향력을 조절합니다.
     * 3. 최종 점수: 기본 점수 + 게시물 수 가중치
     * @param searchTerm
     * @return
     */


}
