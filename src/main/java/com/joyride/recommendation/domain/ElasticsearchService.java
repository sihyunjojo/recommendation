package com.joyride.recommendation.domain;

import com.joyride.recommendation.dto.RecommendDto;
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
import org.springframework.stereotype.Service;


import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


@Slf4j
@Service
public class ElasticsearchService {

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

            // 요청 본문 생성
            String requestBody = createSearchByAreaNameQuery(areaName);

            // 요청 본문을 설정
            httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                // 응답 파싱 및 DTO로 변환
                return new RecommendDto(parseTopAreaNames(responseBody, 5));
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

            String requestBody = createMultiIndexSearchTermQuery(searchTerm);
            log.debug("requestBody = " + requestBody);

            httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
            httpPost.setHeader("Content-Type", "application/x-ndjson");

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                log.debug("Elasticsearch response: {}", responseBody);
                return new RecommendDto(parseMultiIndexResults(responseBody, 5));
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

    // 요청 본문 생성 메서드
    private String createSearchByAreaNameQuery(String areaName) {
        return String.format("""
        {
          "size": 10,
          "_source": ["area_name"],
          "query": {
            "bool": {
              "must": [
                {
                  "function_score": {
                    "query": {
                      "dis_max": {
                        "queries": [
                          {
                            "match": {
                              "area_name.standard": {
                                "query": "%s",
                                "boost": 2.63,
                                "analyzer": "edge_ngram_search_analyzer"
                              }
                            }
                          },
                          {
                            "match": {
                              "area_name.nori": {
                                "query": "%s",
                                "boost": 1.62,
                                "analyzer": "edge_ngram_search_analyzer"
                              }
                            }
                          },
                          {
                            "match": {
                              "area_name.ngram": {
                                "query": "%s",
                                "boost": 1,
                                "analyzer": "edge_ngram_search_analyzer"
                              }
                            }
                          }
                        ],
                        "tie_breaker": 0
                      }
                    },
                    "functions": [
                      {
                        "field_value_factor": {
                          "field": "view",
                          "factor": 0.3,
                          "modifier": "log1p",
                          "missing": 1
                        }
                      }
                    ],
                    "boost_mode": "sum",
                    "score_mode": "sum",
                    "min_score": 0.1
                  }
                }
              ]
            }
          },
          "sort": [
            "_score",
            {"view": "desc"}
          ]
        }
        """, areaName);
    }

    // String[]**는 고정된 수의 데이터에 대해 빠른 접근이 필요할 때 유용
    // 응답을 파싱하고 상위 N개의 area_name을 반환
    private String[] parseTopAreaNames(String responseBody, int topN) {
        // 응답 본문을 JSON 객체로 변환
        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONArray hits = jsonResponse.getJSONObject("hits").getJSONArray("hits");

        String[] topAreaNames = new String[Math.min(topN, hits.length())];
        for (int i = 0; i < topAreaNames.length; i++) {
            topAreaNames[i] = hits.getJSONObject(i).getJSONObject("_source").getString("area_name");
        }

        return topAreaNames;
    }

    /**
     * 점수 계산 로직:
     * 1. 기본 점수 (baseScore): nori 분석기를 통한 텍스트 일치 점수. 범위: 0-11 (예상)
     * 2. 게시물 수 가중치 (postCountFactor): 게시물 수에 따른 추가 점수
     * Math.log(postCount + 1) / Math.log(2)를 사용하여 게시물 수의 로그값을 계산합니다.
     * 이 값에 0.6을 곱하여 게시물 수의 영향력을 조절합니다.
     * 3. 최종 점수: 기본 점수 + 게시물 수 가중치
     * @param searchTerm
     * @return
     */
    // Multi Index용 요청 본문 생성
    private String createMultiIndexSearchTermQuery(String searchTerm) {
//        return String.format("""
        return String.format("""
                {"index":"franchise_board_search_term"}
                {"query":{"bool":{"must":[{"function_score":{"query":{"dis_max":{"queries":[{"dis_max":{"queries":[{"match":{"franchise_name.standard":{"query":"%s"}}},{"match":{"franchise_name.ngram":{"query":"이태원"}}},{"match":{"franchise_name.edge_ngram":{"query":"이태원"}}},{"match":{"franchise_name.nori":{"query":"이태원"}}}],"tie_breaker":0.3}}],"tie_breaker":0.3}},"functions":[{"field_value_factor":{"field":"post_count","factor":0.6,"modifier":"log1p","missing":1}}],"boost_mode":"sum","score_mode":"sum"}}]}},"size":10,"_source":["franchise_name"]}
                {"index":"area_board_search_term"}
                {"query":{"bool":{"must":[{"function_score":{"query":{"dis_max":{"queries":[{"match":{"area_name.ngram":{"query":"%s","analyzer":"edge_ngram_search_analyzer"}}},{"match":{"area_name.standard":{"query":"이태원"}}},{"match":{"area_name.nori":{"query":"이태원"}}}],"tie_breaker":0}},"functions":[{"field_value_factor":{"field":"post_count","factor":0.6,"modifier":"log1p","missing":1}}],"boost_mode":"sum","score_mode":"sum"}}]}},"size":10,"_source":["area_name"]}
                """, searchTerm);
    }


    private String[] parseMultiIndexResults(String responseBody, int topN) {
        // JSON 응답 파싱
        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONArray responses = jsonResponse.getJSONArray("responses");

        // 모든 인덱스의 hits를 모아 하나의 리스트에 저장
        List<JSONObject> allHits = new ArrayList<>();
        for (int i = 0; i < responses.length(); i++) {
            JSONObject response = responses.getJSONObject(i);
            JSONArray hits = response.getJSONObject("hits").getJSONArray("hits");

            // 각 hit를 allHits 리스트에 추가
            for (int j = 0; j < hits.length(); j++) {
                allHits.add(hits.getJSONObject(j));
            }
        }

        // 점수를 기준으로 정렬
        allHits.sort((hit1, hit2) -> Double.compare(hit2.getDouble("_score"), hit1.getDouble("_score")));

        String[] topNames = new String[Math.min(topN, allHits.size())];
        for (int i = 0; i < topNames.length; i++) {
            JSONObject source = allHits.get(i).getJSONObject("_source");
            topNames[i] = source.has("area_name") ? source.getString("area_name") : source.getString("franchise_name");
        }
        return topNames;
    }
}

// SSL 인증서를 신뢰할 수 있도록 설정된 HTTP 클라이언트를 생성합니다. (개발용)
//    private CloseableHttpClient createHttpClient() throws Exception {
//        // SSL 인증서 설정을 위한 빌더 생성 (기본 키스토어를 사용하여 신뢰할 수 있는 인증서 설정)
//        SSLContextBuilder sslBuilder = new SSLContextBuilder();
//        sslBuilder.loadTrustMaterial(
//                // 기본 키스토어 형식을 사용하여 인증서 가져오기
//                KeyStore.getInstance(KeyStore.getDefaultType()),
//                // 신뢰할 수 있는 자체 서명된 인증서 설정
//                new TrustSelfSignedStrategy()
//        );
//
//        // HTTP 클라이언트 인증 정보 설정
//        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                // 사용자 이름과 비밀번호를 사용한 자격 증명 설정
//                new UsernamePasswordCredentials(elasticsearchUserName, elasticsearchPassword));
//
//        // HTTP 클라이언트 생성 및 반환
//        return HttpClients.custom()
//                .setSSLContext(sslBuilder.build())
//                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
//                .setDefaultCredentialsProvider(credentialsProvider)
//                .build();
//    }
