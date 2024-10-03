# 상권 검색어 자동완성
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
# 게시판 검색어 자동완성
```bash
POST /_msearch
{"index":"area_board_search_term"}
{"query":{"function_score":{"query":{"match":{"area_name":{"query":"서울","analyzer":"nori_analyzer"}}},"functions":[{"script_score":{"script":{"source":"double baseScore = _score; double maxPostCount = 100.0; double currentPostCount = doc['post_count'].value != null ? doc['post_count'].value : 1; double postCountRatio = currentPostCount / maxPostCount; double maxAllowedFactor = baseScore * 0.3; double adjustedFactor = postCountRatio * maxAllowedFactor; return baseScore + adjustedFactor;"}}}],"score_mode":"sum","boost_mode":"multiply"}},"size":10,"_source":["area_name"]}
{"index":"franchise_board_search_term"}
{"query":{"function_score":{"query":{"match":{"franchise_name":{"query":"서울","analyzer":"nori_analyzer"}}},"functions":[{"script_score":{"script":{"source":"double baseScore = _score; double maxPostCount = 100.0; double currentPostCount = doc['post_count'].value != null ? doc['post_count'].value : 1; double postCountRatio = currentPostCount / maxPostCount; double maxAllowedFactor = baseScore * 0.3; double adjustedFactor = postCountRatio * maxAllowedFactor; return baseScore + adjustedFactor;"}}}],"score_mode":"sum","boost_mode":"multiply"}},"size":10,"_source":["franchise_name"]}
```
