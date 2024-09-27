package com.joyride.recommendation.domain;

import com.joyride.recommendation.dto.RecommendDto;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
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
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.json.JSONArray;
import org.json.JSONObject;


@Service
public class ElasticsearchService {

    @Value("${elasticsearch.url}")
    private String elasticsearchUrl;

    @Value("${elasticsearch.username}")
    private String elasticsearchUserName;

    @Value("${elasticsearch.password}")
    private String elasticsearchPassword;

    private final RestTemplate restTemplate;

    public ElasticsearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

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

    public RecommendDto searchByAreaNameForMultipleIndexes(String areaName) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String elasticsearchHost = "es01";
        int elasticsearchPort = 9200;
        String elasticsearchScheme = "https";
        String username = "elastic";
        String password = "safe0113";
        String caCertPath = "~/certs/ca/ca.crt";

        // SSL 컨텍스트 생성
        SSLContextBuilder sslBuilder = new SSLContextBuilder();
        sslBuilder.loadTrustMaterial(new File(caCertPath), null);

        // 인증 제공자 설정
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));

        // HttpClient 생성
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslBuilder.build())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();

        // 요청 URL 구성
        String url = elasticsearchScheme + "://" + elasticsearchHost + ":" + elasticsearchPort + "/_cat/indices?v";

        // GET 요청 생성
        HttpGet httpGet = new HttpGet(url);

        // 요청 실행
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("Response: " + responseBody);

            // 응답 처리 (예: 상위 5개 area_name 파싱)
            return new RecommendDto(parseTopAreaNames(responseBody, 5));
        } finally {
            httpClient.close();
        }
    }

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
