# 현재 방식
multi_match 쿼리에 "minimum_should_match": "1" 파라미터를 추가했습니다. 이는 적어도 하나의 필드에서 매치가 있어야 함을 의미합니다.

## 상권 검색어 자동완성
```bash
GET /area_search_term/_search
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
                        "query": "명동",
                        "boost": 2.63,
                        "analyzer": "edge_ngram_search_analyzer"
                      }
                    }
                  },
                  {
                    "match": {
                      "area_name.nori": {
                        "query": "명동",
                        "boost": 1.62,
                        "analyzer": "edge_ngram_search_analyzer"
                      }
                    }
                  },
                  {
                    "match": {
                      "area_name.ngram": {
                        "query": "명동",
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
```

## 게시판 검색어 자동완성
```bash
POST /_msearch
{"index":"franchise_board_search_term"}
{"query":{"bool":{"must":[{"function_score":{"query":{"dis_max":{"queries":[{"dis_max":{"queries":[{"match":{"franchise_name.standard":{"query":"이태원"}}},{"match":{"franchise_name.ngram":{"query":"이태원"}}},{"match":{"franchise_name.edge_ngram":{"query":"이태원"}}},{"match":{"franchise_name.nori":{"query":"이태원"}}}],"tie_breaker":0.3}}],"tie_breaker":0.3}},"functions":[{"field_value_factor":{"field":"post_count","factor":0.6,"modifier":"log1p","missing":1}}],"boost_mode":"sum","score_mode":"sum"}}]}},"size":10,"_source":["franchise_name"]}
{"index":"area_board_search_term"}
{"query":{"bool":{"must":[{"function_score":{"query":{"dis_max":{"queries":[{"match":{"area_name.ngram":{"query":"이태원","analyzer":"edge_ngram_search_analyzer"}}},{"match":{"area_name.standard":{"query":"이태원"}}},{"match":{"area_name.nori":{"query":"이태원"}}}],"tie_breaker":0}},"functions":[{"field_value_factor":{"field":"post_count","factor":0.6,"modifier":"log1p","missing":1}}],"boost_mode":"sum","score_mode":"sum"}}]}},"size":10,"_source":["area_name"]}

# 부스트 없는 DIS-MAX 
POST /_msearch
{"index":"franchise_board_search_term"}
{"query":{"bool":{"must":[{"function_score":{"query":{"dis_max":{"queries":[{"dis_max":{"queries":[{"match":{"franchise_name.standard":{"query":"이태원"}}},{"match":{"franchise_name.ngram":{"query":"이태원"}}},{"match":{"franchise_name.edge_ngram":{"query":"이태원"}}},{"match":{"franchise_name.nori":{"query":"이태원"}}}],"tie_breaker":0.3}}],"tie_breaker":0.3}},"functions":[{"field_value_factor":{"field":"post_count","factor":0.6,"modifier":"log1p","missing":1}}],"boost_mode":"sum","score_mode":"sum"}}]}},"size":10,"_source":["franchise_name"]}
{"index":"area_board_search_term"}
{"query":{"bool":{"must":[{"function_score":{"query":{"dis_max":{"queries":[{"match":{"area_name.ngram":{"query":"이태원","analyzer":"edge_ngram_search_analyzer"}}},{"match":{"area_name.standard":{"query":"이태원"}}},{"match":{"area_name.nori":{"query":"이태원"}}}],"tie_breaker":0}},"functions":[{"field_value_factor":{"field":"post_count","factor":0.6,"modifier":"log1p","missing":1}}],"boost_mode":"sum","score_mode":"sum"}}]}},"size":10,"_source":["area_name"]}

# 부스트 있는 DIS-MAX 
POST /_msearch
{"index":"franchise_board_search_term"}
{"query":{"bool":{"must":[{"function_score":{"query":{"dis_max":{"queries":[{"dis_max":{"queries":[{"match":{"franchise_name.standard":{"query":"이태원","boost":2}}},{"match":{"franchise_name.ngram":{"query":"이태원","boost":1.5}}},{"match":{"franchise_name.edge_ngram":{"query":"이태원","boost":1.3}}},{"match":{"franchise_name.nori":{"query":"이태원","boost":1}}}],"tie_breaker":0.3}}],"tie_breaker":0.3}},"functions":[{"field_value_factor":{"field":"post_count","factor":0.6,"modifier":"log1p","missing":1}}],"boost_mode":"sum","score_mode":"sum"}}]}},"size":10,"_source":["franchise_name"]}
{"index":"area_board_search_term"}
{"query":{"bool":{"must":[{"function_score":{"query":{"dis_max":{"queries":[{"match":{"area_name.ngram":{"query":"이태원","analyzer":"edge_ngram_search_analyzer"}}},{"match":{"area_name.standard":{"query":"이태원","boost":2}}},{"match":{"area_name.nori":{"query":"이태원","boost":1.5}}}],"tie_breaker":0}},"functions":[{"field_value_factor":{"field":"post_count","factor":0.6,"modifier":"log1p","missing":1}}],"boost_mode":"sum","score_mode":"sum"}}]}},"size":10,"_source":["area_name"]}

```

# 과거 방식
## 상권 검색어 자동완성
```bash
POST /area_search_term/_search
{
  "query": {
    "function_score": {
      "query": {
        "match": {
          "area_name": {
            "query": "서울",
            "analyzer": "nori_analyzer"
          }
        }
      },
      "functions": [
        {
          "script_score": {
            "script": {
              "source": """
                double baseScore = _score;
                double maxView = 100.0;  // 미리 계산된 최대 view 값
                double currentView = doc['view'].value != null ? doc['view'].value : 1;
                
                // 현재 view 값 비율 계산 (0~1 사이)
                double viewRatio = currentView / maxView;
                
                // 최대 가중치가 baseScore의 30%가 되도록 비율 조정
                double maxAllowedFactor = baseScore * 0.3; // 기본 점수의 30%
                double adjustedFactor = viewRatio * maxAllowedFactor; // 비율 맞춰서 조정
                
                return baseScore + adjustedFactor;
              """
            }
          }
        }
      ],
      "score_mode": "sum",
      "boost_mode": "multiply"
    }
  },
  "size": 10,
  "_source": ["area_name"]
}

```
## 게시판 검색어 자동완성
```bash
POST /_msearch
{"index":"area_board_search_term"}
{"query":{"function_score":{"query":{"match":{"area_name":{"query":"서울","analyzer":"nori_analyzer"}}},"functions":[{"script_score":{"script":{"source":"double baseScore = _score; double maxPostCount = 100.0; double currentPostCount = doc['post_count'].value != null ? doc['post_count'].value : 1; double postCountRatio = currentPostCount / maxPostCount; double maxAllowedFactor = baseScore * 0.3; double adjustedFactor = postCountRatio * maxAllowedFactor; return baseScore + adjustedFactor;"}}}],"score_mode":"sum","boost_mode":"multiply"}},"size":10,"_source":["area_name"]}
{"index":"franchise_board_search_term"}
{"query":{"function_score":{"query":{"match":{"franchise_name":{"query":"서울","analyzer":"nori_analyzer"}}},"functions":[{"script_score":{"script":{"source":"double baseScore = _score; double maxPostCount = 100.0; double currentPostCount = doc['post_count'].value != null ? doc['post_count'].value : 1; double postCountRatio = currentPostCount / maxPostCount; double maxAllowedFactor = baseScore * 0.3; double adjustedFactor = postCountRatio * maxAllowedFactor; return baseScore + adjustedFactor;"}}}],"score_mode":"sum","boost_mode":"multiply"}},"size":10,"_source":["franchise_name"]}
```
