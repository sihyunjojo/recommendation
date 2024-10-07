# 현재


## info
### area_search_term
```bash
{
  "area_search_term": {
    "aliases": {},
    "mappings": {
      "properties": {
        "area_name": {
          "type": "text",
          "fields": {
            "ngram": {
              "type": "text",
              "analyzer": "ngram_analyzer",
              "search_analyzer": "edge_ngram_search_analyzer"
            },
            "nori": {
              "type": "text",
              "analyzer": "nori_analyzer"
            },
            "standard": {
              "type": "text",
              "analyzer": "standard_analyzer"
            }
          }
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
        "routing": {
          "allocation": {
            "include": {
              "_tier_preference": "data_content"
            }
          }
        },
        "number_of_shards": "1",
        "provided_name": "area_search_term",
        "creation_date": "1728321733125",
        "analysis": {
          "analyzer": {
            "ngram_analyzer": {
              "filter": [
                "lowercase"
              ],
              "type": "custom",
              "tokenizer": "edge_ngram_tokenizer"
            },
            "nori_analyzer": {
              "filter": [
                "nori_readingform",
                "nori_part_of_speech",
                "lowercase"
              ],
              "type": "custom",
              "tokenizer": "nori_tokenizer"
            },
            "standard_analyzer": {
              "filter": [
                "lowercase"
              ],
              "type": "custom",
              "tokenizer": "standard"
            },
            "edge_ngram_search_analyzer": {
              "filter": [
                "lowercase"
              ],
              "type": "custom",
              "tokenizer": "edge_ngram_search_tokenizer"
            }
          },
          "tokenizer": {
            "edge_ngram_tokenizer": {
              "token_chars": [
                "letter",
                "digit"
              ],
              "min_gram": "1",
              "type": "edge_ngram",
              "max_gram": "4"
            },
            "edge_ngram_search_tokenizer": {
              "token_chars": [
                "letter",
                "digit"
              ],
              "min_gram": "1",
              "type": "edge_ngram",
              "max_gram": "6"
            }
          }
        },
        "number_of_replicas": "1",
        "uuid": "-BDnRsbcRuyvkLeJ0CFAtw",
        "version": {
          "created": "8080299"
        }
      }
    }
  }
}
```
### area_board_search_term
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
          "fields": {
            "ngram": {
              "type": "text",
              "analyzer": "ngram_analyzer",
              "search_analyzer": "edge_ngram_search_analyzer"
            },
            "nori": {
              "type": "text",
              "analyzer": "nori_analyzer"
            },
            "standard": {
              "type": "text",
              "analyzer": "standard_analyzer"
            }
          }
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
        "routing": {
          "allocation": {
            "include": {
              "_tier_preference": "data_content"
            }
          }
        },
        "number_of_shards": "1",
        "provided_name": "area_board_search_term",
        "creation_date": "1728321870698",
        "analysis": {
          "analyzer": {
            "ngram_analyzer": {
              "filter": [
                "lowercase"
              ],
              "type": "custom",
              "tokenizer": "edge_ngram_tokenizer"
            },
            "nori_analyzer": {
              "filter": [
                "nori_readingform",
                "nori_part_of_speech",
                "lowercase"
              ],
              "type": "custom",
              "tokenizer": "nori_tokenizer"
            },
            "standard_analyzer": {
              "filter": [
                "lowercase"
              ],
              "type": "custom",
              "tokenizer": "standard"
            },
            "edge_ngram_search_analyzer": {
              "filter": [
                "lowercase"
              ],
              "type": "custom",
              "tokenizer": "edge_ngram_search_tokenizer"
            }
          },
          "tokenizer": {
            "edge_ngram_tokenizer": {
              "token_chars": [
                "letter",
                "digit"
              ],
              "min_gram": "1",
              "type": "edge_ngram",
              "max_gram": "4"
            },
            "edge_ngram_search_tokenizer": {
              "token_chars": [
                "letter",
                "digit"
              ],
              "min_gram": "1",
              "type": "edge_ngram",
              "max_gram": "6"
            }
          }
        },
        "number_of_replicas": "1",
        "uuid": "8brQx_xxQQOVbtKVq-apBw",
        "version": {
          "created": "8080299"
        }
      }
    }
  }
}
```

### franchise_search_term
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
          "fields": {
            "edge_ngram": {
              "type": "text",
              "analyzer": "edge_ngram_analyzer",
              "search_analyzer": "edge_ngram_search_analyzer"
            },
            "ngram": {
              "type": "text",
              "analyzer": "ngram_analyzer",
              "search_analyzer": "ngram_search_analyzer"
            },
            "nori": {
              "type": "text",
              "analyzer": "nori_analyzer"
            },
            "standard": {
              "type": "text",
              "analyzer": "standard_analyzer"
            }
          }
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
        "max_ngram_diff": "3",
        "routing": {
          "allocation": {
            "include": {
              "_tier_preference": "data_content"
            }
          }
        },
        "number_of_shards": "1",
        "provided_name": "franchise_board_search_term",
        "creation_date": "1728322011938",
        "analysis": {
          "analyzer": {
            "edge_ngram_analyzer": {
              "filter": [
                "lowercase"
              ],
              "type": "custom",
              "tokenizer": "edge_ngram_tokenizer"
            },
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
                "lowercase"
              ],
              "type": "custom",
              "tokenizer": "nori_tokenizer"
            },
            "standard_analyzer": {
              "filter": [
                "lowercase"
              ],
              "type": "custom",
              "tokenizer": "standard"
            },
            "ngram_search_analyzer": {
              "filter": [
                "lowercase"
              ],
              "type": "custom",
              "tokenizer": "ngram_search_tokenizer"
            },
            "edge_ngram_search_analyzer": {
              "filter": [
                "lowercase"
              ],
              "type": "custom",
              "tokenizer": "edge_ngram_search_tokenizer"
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
              "max_gram": "2"
            },
            "ngram_search_tokenizer": {
              "token_chars": [
                "letter",
                "digit"
              ],
              "min_gram": "1",
              "type": "ngram",
              "max_gram": "3"
            },
            "edge_ngram_tokenizer": {
              "token_chars": [
                "letter",
                "digit"
              ],
              "min_gram": "3",
              "type": "edge_ngram",
              "max_gram": "4"
            },
            "edge_ngram_search_tokenizer": {
              "token_chars": [
                "letter",
                "digit"
              ],
              "min_gram": "3",
              "type": "edge_ngram",
              "max_gram": "6"
            }
          }
        },
        "number_of_replicas": "1",
        "uuid": "F0wkRVb8QBy1r7yHb4F-sA",
        "version": {
          "created": "8080299"
        }
      }
    }
  }
}
```

## 생성문
### area_search_term
```bash
PUT /area_search_term
{
  "settings": {
    "index": {
      "number_of_shards": "1",
      "number_of_replicas": "1",
      "analysis": {
        "analyzer": {
          "ngram_analyzer": {
            "type": "custom",
            "tokenizer": "edge_ngram_tokenizer",
            "filter": [
              "lowercase"
            ]
          },
          "standard_analyzer": {
            "type": "custom",
            "tokenizer": "standard",
            "filter": [
              "lowercase"
            ]
          },
          "nori_analyzer": {
            "type": "custom",
            "tokenizer": "nori_tokenizer",
            "filter": [
              "nori_readingform", // 한자가 포함된 문장에서 한자를 한글 발음으로 변환합니다. 이는 한자와 그 발음이 동일한 의미를 가지면서도 서로 다른 텍스트로 인식되는 문제를 해결하기 위해 사용됩니다.
              "nori_part_of_speech", // 텍스트에서 불필요한 품사(예: 조사, 접속사 등)를 제거하고, 검색어에 적합한 중요한 품사만을 남겨서 색인합니다.
              "lowercase"
            ]
          },
          "edge_ngram_search_analyzer": {
            "type": "custom",
            "tokenizer": "edge_ngram_search_tokenizer",
            "filter": [
              "lowercase"
            ]
          }
        },
        "tokenizer": {
          "edge_ngram_tokenizer": {
            "type": "edge_ngram",
            "min_gram": "1",
            "max_gram": "4",
            "token_chars": [
              "letter",
              "digit"
            ]
          },
          "edge_ngram_search_tokenizer": {
            "type": "edge_ngram",
            "min_gram": "1",
            "max_gram": "6",
            "token_chars": [
              "letter",
              "digit"
            ]
          }
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "area_name": {
        "type": "text",
        "fields": {
          "ngram": {
            "type": "text",
            "analyzer": "ngram_analyzer",
            "search_analyzer": "edge_ngram_search_analyzer"
          },
          "standard": {
            "type": "text",
            "analyzer": "standard_analyzer"
          },
          "nori": {
            "type": "text",
            "analyzer": "nori_analyzer"
          }
        }
      },
      "id": {
        "type": "long"
      },
      "view": {
        "type": "integer"
      }
    }
  }
}
```

### area_board_search_term
```bash
PUT /area_board_search_term
{
  "settings": {
    "index": {
      "number_of_shards": "1",
      "number_of_replicas": "1",
      "analysis": {
        "analyzer": {
          "ngram_analyzer": {
            "type": "custom",
            "tokenizer": "edge_ngram_tokenizer",
            "filter": [
              "lowercase"
            ]
          },
          "standard_analyzer": {
            "type": "custom",
            "tokenizer": "standard",
            "filter": [
              "lowercase"
            ]
          },
          "nori_analyzer": {
            "type": "custom",
            "tokenizer": "nori_tokenizer",
            "filter": [
              "nori_readingform",
              "nori_part_of_speech",
              "lowercase"
            ]
          },
          "edge_ngram_search_analyzer": {
            "type": "custom",
            "tokenizer": "edge_ngram_search_tokenizer",
            "filter": [
              "lowercase"
            ]
          }
        },
        "tokenizer": {
          "edge_ngram_tokenizer": {
            "type": "edge_ngram",
            "min_gram": "1",
            "max_gram": "4",
            "token_chars": [
              "letter",
              "digit"
            ]
          },
          "edge_ngram_search_tokenizer": {
            "type": "edge_ngram",
            "min_gram": "1",
            "max_gram": "6",
            "token_chars": [
              "letter",
              "digit"
            ]
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
        "fields": {
          "ngram": {
            "type": "text",
            "analyzer": "ngram_analyzer",
            "search_analyzer": "edge_ngram_search_analyzer"
          },
          "standard": {
            "type": "text",
            "analyzer": "standard_analyzer"
          },
          "nori": {
            "type": "text",
            "analyzer": "nori_analyzer"
          }
        }
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
### franchise_board_search_term
```bash
PUT /franchise_board_search_term
{
  "settings": {
    "index": {
      "number_of_shards": "1",
      "number_of_replicas": "1",
      "max_ngram_diff": "3",  // NGram 차이 제한을 3으로 설정
      "analysis": {
        "analyzer": {
          "ngram_analyzer": {
            "type": "custom",
            "tokenizer": "ngram_tokenizer",
            "filter": [
              "lowercase"
            ]
          },
          "standard_analyzer": {
            "type": "custom",
            "tokenizer": "standard",
            "filter": [
              "lowercase"
            ]
          },
          "nori_analyzer": {
            "type": "custom",
            "tokenizer": "nori_tokenizer",
            "filter": [
              "nori_readingform",
              "lowercase"
            ]
          },
          "edge_ngram_analyzer": {
            "type": "custom",
            "tokenizer": "edge_ngram_tokenizer",
            "filter": [
              "lowercase"
            ]
          },
          "ngram_search_analyzer": {
            "type": "custom",
            "tokenizer": "ngram_search_tokenizer",
            "filter": [
              "lowercase"
            ]
          },
          "edge_ngram_search_analyzer": {
            "type": "custom",
            "tokenizer": "edge_ngram_search_tokenizer",
            "filter": [
              "lowercase"
            ]
          }
        },
        "tokenizer": {
          "ngram_tokenizer": {
            "type": "ngram",
            "min_gram": "1",
            "max_gram": "2",
            "token_chars": [
              "letter",
              "digit"
            ]
          },
          "edge_ngram_tokenizer": {
            "type": "edge_ngram",
            "min_gram": "3",
            "max_gram": "4",
            "token_chars": [
              "letter",
              "digit"
            ]
          },
          "ngram_search_tokenizer": {
            "type": "ngram",
            "min_gram": "1",
            "max_gram": "3",
            "token_chars": [
              "letter",
              "digit"
            ]
          },
          "edge_ngram_search_tokenizer": {
            "type": "edge_ngram",
            "min_gram": "3",
            "max_gram": "6",
            "token_chars": [
              "letter",
              "digit"
            ]
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
        "fields": {
          "ngram": {
            "type": "text",
            "analyzer": "ngram_analyzer",
            "search_analyzer": "ngram_search_analyzer"
          },
          "edge_ngram": {
            "type": "text",
            "analyzer": "edge_ngram_analyzer",
            "search_analyzer": "edge_ngram_search_analyzer"
          },
          "standard": {
            "type": "text",
            "analyzer": "standard_analyzer"
          },
          "nori": {
            "type": "text",
            "analyzer": "nori_analyzer"
          }
        }
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
##
# 과거
## area_search_term
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

## area_board_search_term
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

## franchise_board_search_term
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
