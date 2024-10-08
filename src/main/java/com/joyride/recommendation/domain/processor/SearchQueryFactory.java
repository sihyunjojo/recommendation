package com.joyride.recommendation.domain.processor;

import org.springframework.stereotype.Component;

@Component
public class SearchQueryFactory {
    // 요청 본문 생성 메서드
    public String createSearchByAreaNameQuery(String areaName) {
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
                        "tie_breaker": 0.1
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
        """, areaName, areaName, areaName);
    }

    // Multi Index용 요청 본문 생성
    public String createMultiIndexSearchTermQuery(String searchTerm) {
        return String.format("""
                {"index":"area_board_search_term"}
                {"query":{"bool":{"must":[{"function_score":{"query":{"dis_max":{"queries":[{"match":{"area_name.ngram":{"query":"%s","analyzer":"edge_ngram_search_analyzer","boost":1.3}}},{"match":{"area_name.standard":{"query":"%s","boost":4}}},{"match":{"area_name.nori":{"query":"%s","boost":3}}}],"tie_breaker":0}},"functions":[{"field_value_factor":{"field":"post_count","factor":0.8,"modifier":"log1p","missing":1}}],"boost_mode":"sum","score_mode":"sum"}}]}},"size":10,"_source":["area_name","post_count"]}
                {"index":"franchise_board_search_term"}
                {"query":{"bool":{"must":[{"function_score":{"query":{"dis_max":{"queries":[{"dis_max":{"queries":[{"match":{"franchise_name.standard":{"query":"%s","boost":3}}},{"match":{"franchise_name.ngram":{"query":"%s","boost":1}}},{"match":{"franchise_name.edge_ngram":{"query":"%s","boost":2.4}}},{"match":{"franchise_name.nori":{"query":"%s","boost":2}}}],"tie_breaker":0}}],"tie_breaker":0}},"functions":[{"field_value_factor":{"field":"post_count","factor":0.8,"modifier":"log1p","missing":1}}],"boost_mode":"sum","score_mode":"sum"}}]}},"size":10,"_source":["franchise_name","post_count"]}
                """, searchTerm, searchTerm, searchTerm, searchTerm, searchTerm, searchTerm, searchTerm);
    }
}
