package com.joyride.recommendation.domain;

import com.joyride.recommendation.dto.RecommendDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


@Slf4j
@Service
public class ElasticsearchService {

    @Value("${elasticsearch.url}")
    private String elasticsearchUrl;

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

    private final RestTemplate restTemplate;

    public ElasticsearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // TODO: 아래처럼 수정해야함
    public RecommendDto searchByAreaName(String areaName) {
        String index = "area_search_term";
        String searchUrl = elasticsearchUrl + "/" + index + "/_search";

        String requestBody = """
                {
                  "query": {
                    "function_score": {
                      "query": {
                        "match": {
                          "area_name": "%s"
                        }
                      },
                      "field_value_factor": {
                        "field": "post_count",
                        "factor": 1.5,
                        "modifier": "sqrt",
                        "missing": 1
                      },
                      "boost_mode": "multiply"
                    }
                  }
                }
                """.formatted(areaName);

        // 요청 본문만 포함하여 HTTP POST 요청 보내기
        ResponseEntity<String> response = restTemplate.postForEntity(searchUrl, requestBody, String.class);

        return new RecommendDto(parseTopAreaNames(response.getBody(), 5));
    }

    public RecommendDto recommendBoardSearchTerm(String searchTerm) throws IOException {
        try (CloseableHttpClient httpClient = createHttpClient()) {
//            String url = "https://" + elasticsearchHost + ":" + elasticsearchPort + "/area_search_term/_search";
            String url = "https://" + elasticsearchHost + ":" + elasticsearchPort + "/_msearch";
            HttpPost httpPost = new HttpPost(url);

            String requestBody = createMultiIndexSearchTermQuery(searchTerm);
            log.debug("requestBody = " + requestBody);
            httpPost.setEntity(new StringEntity(requestBody));
            httpPost.setHeader("Content-Type", "application/json");

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

    private CloseableHttpClient createHttpClient() throws Exception {
        // KeyStore 유형: 위의 방법이 작동하지 않으면 KeyStore 유형을 명시적으로 지정해야 할 수도 있습니다.
        // 호출을 수정해 보세요 loadTrustMaterial.
        SSLContextBuilder sslBuilder = new SSLContextBuilder();
        sslBuilder.loadTrustMaterial(
                KeyStore.getInstance(KeyStore.getDefaultType()),
                new TrustSelfSignedStrategy()
        );

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(elasticsearchUserName, elasticsearchPassword));

        return HttpClients.custom()
                .setSSLContext(sslBuilder.build())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
    }


    // Multi Index용 요청 본문 생성
    private String createMultiIndexSearchTermQuery(String searchTerm) {
        // area_board_search_term 인덱스 쿼리
        String areaBoardSearchQuery = String.format("""
                { "index": "area_board_search_term" }
                {
                  "query": {
                    "function_score": {
                      "query": {
                        "match": {
                          "area_name": "%s"
                        }
                      },
                      "functions": [
                        {
                          "field_value_factor": {
                            "field": "post_count",
                            "factor": 1.5,
                            "modifier": "sqrt",
                            "missing": 1
                          }
                        },
                        {
                          "random_score": {}
                        }
                      ],
                      "score_mode": "sum",
                      "boost_mode": "multiply"
                    }
                  }
                }
                """, searchTerm);

        // franchise_board_search_term 인덱스 쿼리
        String franchiseBoardSearchQuery = String.format("""
                { "index": "franchise_board_search_term" }
                {
                  "query": {
                    "function_score": {
                      "query": {
                        "match": {
                          "franchise_name": "%s"
                        }
                      },
                      "functions": [
                        {
                          "field_value_factor": {
                            "field": "post_count",
                            "factor": 1.5,
                            "modifier": "sqrt",
                            "missing": 1
                          }
                        },
                        {
                          "random_score": {}
                        }
                      ],
                      "score_mode": "sum",
                      "boost_mode": "multiply"
                    }
                  }
                }
                """, searchTerm);

        // 두 쿼리를 NDJSON 형식으로 묶어서 반환
        return areaBoardSearchQuery + "\n" + franchiseBoardSearchQuery;
    }

//
//    private String createBoardSearchTermRequestBody(String areaName) {
//        return String.format("""
//            {
//              "query": {
//                "function_score": {
//                  "query": {
//                    "match": {
//                      "area_name": "%s"
//                    }
//                  },
//                  "field_value_factor": {
//                    "field": "post_count",
//                    "factor": 1.5,
//                    "modifier": "sqrt",
//                    "missing": 1
//                  },
//                  "boost_mode": "multiply"
//                }
//              }
//            }
//            """, areaName);
//    }

//
//    public RecommendDto searchByAreaNameForMultipleIndexes(String areaName){
//        String index = "area_board_search_term,franchise_board_search_term";
//        String searchUrl = elasticsearchUrl + "/" + index + "/_search";
//
//        // 요청 본문 생성 (텍스트 블록 사용)
//        String requestBody = """
//            {
//              "query": {
//                "function_score": {
//                  "query": {
//                    "match": {
//                      "area_name": "%s"
//                    }
//                  },
//                  "field_value_factor": {
//                    "field": "post_count",
//                    "factor": 1.5,
//                    "modifier": "sqrt",
//                    "missing": 1
//                  },
//                  "boost_mode": "multiply"
//                }
//              }
//            }
//            """.formatted(areaName);
//
//        // 요청 본문만 포함하여 HTTP POST 요청 보내기
//        ResponseEntity<String> response = restTemplate.postForEntity(searchUrl, requestBody, String.class);
//
//        return new RecommendDto(parseTopAreaNames(response.getBody(), 5));
//    }


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
        allHits.sort((hit1, hit2) -> {
            double score1 = hit1.getDouble("_score");
            double score2 = hit2.getDouble("_score");
            return Double.compare(score2, score1); // 내림차순 정렬
        });

        // 결과를 String[] 배열로 변환하여 반환
        String[] topAreaNames = new String[Math.min(topN, allHits.size())];
        for (int i = 0; i < topAreaNames.length; i++) {
            topAreaNames[i] = allHits.get(i).getJSONObject("_source").getString("area_name");
        }

        return topAreaNames;
    }


    // String[]**는 고정된 수의 데이터에 대해 빠른 접근이 필요할 때 유용
    private String[] parseTopAreaNames(String responseBody, int topN) {
        // JSON 응답 파싱
        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONArray hits = jsonResponse.getJSONObject("hits").getJSONArray("hits");

        // 점수가 높은 topN 개의 area_name 추출
        String[] topAreaNames = new String[Math.min(topN, hits.length())];
        for (int i = 0; i < topAreaNames.length; i++) {
            topAreaNames[i] = hits.getJSONObject(i).getJSONObject("_source").getString("area_name");
        }

        return topAreaNames;
    }

    // 헤더를 설정하는 메소드
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
//
//// ... 기존 import 문 유지 ...
//
//@Service
//public class ElasticsearchService {
//    // ... 기존 필드 유지 ...
//
//    private CloseableHttpClient createHttpClient() throws Exception {
//        SSLContextBuilder sslBuilder = new SSLContextBuilder();
//        // 주의: 이 설정은 개발 환경에서만 사용해야 합니다!
//        sslBuilder.loadTrustMaterial(null, new TrustAllStrategy());
//
//        return HttpClients.custom()
//                .setSSLContext(sslBuilder.build())
//                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
//                .setDefaultCredentialsProvider(getCredentialsProvider())
//                .build();
//    }
//
//    private CredentialsProvider getCredentialsProvider() {
//        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                new UsernamePasswordCredentials(elasticsearchUserName, elasticsearchPassword));
//        return credentialsProvider;
//    }
//
//    // ... 기존 메서드 유지 ...
//}
