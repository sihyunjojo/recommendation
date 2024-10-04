# area_search_term
```bash
{
  "area_search_term": {
    "aliases": {},
    "mappings": {
      "properties": {
        "area_name": {
          "type": "text",
          "analyzer": "ngram_analyzer"
        },
        "id": {
          "type": "long"
        },
        "view": {
          "type": "integer"
        }
      }
    },
    "settings": {
      "index": {
        "max_ngram_diff": "2",
        "routing": {
          "allocation": {
            "include": {
              "_tier_preference": "data_content"
            }
          }
        },
        "number_of_shards": "1",
        "provided_name": "area_search_term",
        "creation_date": "1728050776926",
        "analysis": {
          "analyzer": {
            "ngram_analyzer": {
              "type": "custom",
              "tokenizer": "ngram_tokenizer"
            }
          },
          "tokenizer": {
            "ngram_tokenizer": {
              "token_chars": [
                "letter",
                "digit"
              ],
              "min_gram": "1",
              "type": "ngram",
              "max_gram": "3"
            }
          }
        },
        "number_of_replicas": "1",
        "uuid": "WPlraNAmRPWAqgJ2WDJWWA",
        "version": {
          "created": "8080299"
        }
      }
    }
  }
}
```

# area_board_search_term
```bash
{
  "area_board_search_term": {
    "aliases": {},
    "mappings": {
      "properties": {
        "area_id": {
          "type": "long"
        },
        "area_name": {
          "type": "text",
          "analyzer": "nori_analyzer",
          "search_analyzer": "ngram_analyzer"
        },
        "max_updated_at": {
          "type": "date"
        },
        "post_count": {
          "type": "integer"
        }
      }
    },
    "settings": {
      "index": {
        "max_ngram_diff": "2",
        "routing": {
          "allocation": {
            "include": {
              "_tier_preference": "data_content"
            }
          }
        },
        "number_of_shards": "1",
        "provided_name": "area_board_search_term",
        "creation_date": "1728057175844",
        "analysis": {
          "filter": {
            "ngrammer": {
              "type": "ngram",
              "min_gram": "1",
              "max_gram": "3"
            }
          },
          "analyzer": {
            "ngram_analyzer": {
              "filter": [
                "lowercase"
              ],
              "type": "custom",
              "tokenizer": "ngram_tokenizer"
            },
            "nori_analyzer": {
              "filter": [
                "nori_readingform",
                "nori_part_of_speech",
                "lowercase",
                "ngrammer"
              ],
              "type": "custom",
              "tokenizer": "nori_tokenizer"
            }
          },
          "tokenizer": {
            "ngram_tokenizer": {
              "token_chars": [
                "letter",
                "digit"
              ],
              "min_gram": "1",
              "type": "ngram",
              "max_gram": "3"
            }
          }
        },
        "number_of_replicas": "1",
        "uuid": "6bmNjCTVS6GPt-6yiW5ngw",
        "version": {
          "created": "8080299"
        }
      }
    }
  }
}
```

# franchise_board_search_term
```bash
{
  "franchise_board_search_term": {
    "aliases": {},
    "mappings": {
      "properties": {
        "franchise_id": {
          "type": "long"
        },
        "franchise_name": {
          "type": "text",
          "analyzer": "nori_analyzer",
          "search_analyzer": "ngram_analyzer"
        },
        "max_updated_at": {
          "type": "date"
        },
        "post_count": {
          "type": "integer"
        }
      }
    },
    "settings": {
      "index": {
        "max_ngram_diff": "2",
        "routing": {
          "allocation": {
            "include": {
              "_tier_preference": "data_content"
            }
          }
        },
        "number_of_shards": "1",
        "provided_name": "franchise_board_search_term",
        "creation_date": "1728057178586",
        "analysis": {
          "filter": {
            "ngrammer": {
              "type": "ngram",
              "min_gram": "1",
              "max_gram": "3"
            }
          },
          "analyzer": {
            "ngram_analyzer": {
              "filter": [
                "lowercase"
              ],
              "type": "custom",
              "tokenizer": "ngram_tokenizer"
            },
            "nori_analyzer": {
              "filter": [
                "nori_readingform",
                "nori_part_of_speech",
                "lowercase",
                "ngrammer"
              ],
              "type": "custom",
              "tokenizer": "nori_tokenizer"
            }
          },
          "tokenizer": {
            "ngram_tokenizer": {
              "token_chars": [
                "letter",
                "digit"
              ],
              "min_gram": "1",
              "type": "ngram",
              "max_gram": "3"
            }
          }
        },
        "number_of_replicas": "1",
        "uuid": "Q25BlsxRQQWLRBR3OWHcjg",
        "version": {
          "created": "8080299"
        }
      }
    }
  }
}
```
### 생성문
ngram_tokenizer: 텍스트를 지정된 길이의 n-gram 단위로 나누어 색인 및 검색 성능을 개선하는 토크나이저
nori 분석기에는 토크나이저가 포함되어 있습니다.

max_ngram_diff는 Elasticsearch에서 **min_gram**과 **max_gram**의 차이를 제한하는 설정입니다. 
기본적으로 n-gram 분석기(토크나이저)를 사용할 때, min_gram과 max_gram의 차이가 너무 커지면 색인 작업이 무겁고 성능 저하를 초래할 수 있기 때문에 Elasticsearch는 이 차이를 기본적으로 1로 제한합니다.

```bash
PUT /area_search_term
{
  "settings": {
    "index": {
      "max_ngram_diff": 2  // min_gram과 max_gram의 차이를 2로 허용
    },
    "analysis": {
      "analyzer": {
        "ngram_analyzer": {  // 사용자 정의 분석기로, 색인 및 검색에서 사용할 분석기 설정
          "type": "custom",  // 사용자 정의 분석기임을 명시
          "tokenizer": "ngram_tokenizer"  // n-gram 토크나이저 사용
        }
      },
      "tokenizer": {
        "ngram_tokenizer": {  // n-gram 토크나이저 정의
          "type": "ngram",  // n-gram 방식으로 토큰화
          "min_gram": 1,  // 최소 1글자 단위로 토큰화
          "max_gram": 3,  // 최대 3글자 단위로 토큰화
          "token_chars": [  // 토큰으로 인식할 문자 종류 설정
            "letter",  // 알파벳 문자 (한글 포함)
            "digit"  // 숫자
          ]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "id": {
        "type": "long"  // id 필드는 숫자로, long 타입으로 정의
      },
      "area_name": {
        "type": "text",  // area_name 필드는 텍스트로 저장
        "analyzer": "ngram_analyzer",  // 색인 시 n-gram 분석기 사용
        "search_analyzer": "ngram_analyzer"  // 검색 시에도 n-gram 분석기 사용
      },
      "view": {
        "type": "integer"  // view 필드는 정수로 저장
      }
    }
  }
}
```

```bash
PUT franchise_board_search_term
{
  "settings": {
    "index": {
      "max_ngram_diff": "2",
      "number_of_shards": "1",
      "number_of_replicas": "1",
      "analysis": {
        "analyzer": {
          "nori_analyzer": {
            "type": "custom",
            "tokenizer": "nori_tokenizer",
            "filter": ["nori_readingform", "nori_part_of_speech", "lowercase", "ngrammer"]
          },
          "ngram_analyzer": {
            "type": "custom",
            "tokenizer": "ngram_tokenizer",
            "filter": ["lowercase"]
          }
        },
        "tokenizer": {
          "ngram_tokenizer": {
            "type": "ngram",
            "min_gram": "1",
            "max_gram": "3",
            "token_chars": ["letter", "digit"]
          }
        },
        "filter": {
          "ngrammer": {
            "type": "ngram",
            "min_gram": 1,
            "max_gram": 3
          }
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "franchise_id": {
        "type": "long"
      },
      "franchise_name": {
        "type": "text",
        "analyzer": "nori_analyzer",
        "search_analyzer": "ngram_analyzer"
      },
      "max_updated_at": {
        "type": "date"
      },
      "post_count": {
        "type": "integer"
      }
    }
  }
}

PUT area_board_search_term
{
  "settings": {
    "index": {
      "max_ngram_diff": "2",
      "number_of_shards": "1",
      "number_of_replicas": "1",
      "analysis": {
        "analyzer": {
          "nori_analyzer": {
            "type": "custom",
            "tokenizer": "nori_tokenizer",
            "filter": ["nori_readingform", "nori_part_of_speech", "lowercase", "ngrammer"]
          },
          "ngram_analyzer": {
            "type": "custom",
            "tokenizer": "ngram_tokenizer",
            "filter": ["lowercase"]
          }
        },
        "tokenizer": {
          "ngram_tokenizer": {
            "type": "ngram",
            "min_gram": "1",
            "max_gram": "3",
            "token_chars": ["letter", "digit"]
          }
        },
        "filter": {
          "ngrammer": {
            "type": "ngram",
            "min_gram": 1,
            "max_gram": 3
          }
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "area_id": {
        "type": "long"
      },
      "area_name": {
        "type": "text",
        "analyzer": "nori_analyzer",
        "search_analyzer": "ngram_analyzer"
      },
      "max_updated_at": {
        "type": "date"
      },
      "post_count": {
        "type": "integer"
      }
    }
  }
}


```
type:

keyword 필드는 분석하지 않고 원본 그대로 저장됩니다.
분석 없이 태그 전체 값을 하나의 값으로 저장하기 때문에, 정확한 매칭이 필요할 때 유용합니다.

text는 Elasticsearch에서 분석된 텍스트로 저장됩니다.
기본적으로 텍스트 필드는 검색할 때 토큰화되어 부분 검색이 가능합니다.
