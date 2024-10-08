package com.joyride.recommendation.domain.service;

import com.joyride.recommendation.interfaces.dto.RecommendDto;
import org.springframework.stereotype.Service;

import java.io.IOException;

public interface ElasticsearchService {

    /**
     * 지역 이름을 기준으로 Elasticsearch에서 검색을 수행합니다.
     * @param areaName 검색할 지역 이름
     * @return 추천된 지역 이름과 관련된 정보가 담긴 DTO
     * @throws IOException Elasticsearch 검색 실패 시 발생
     */
    RecommendDto searchByAreaName(String areaName) throws IOException;

    /**
     * 검색어를 기준으로 multi-index 검색을 수행합니다.
     * area_board_search_term, franchise_board_search_term 인덱스를 기준으로 검색하여 추천합니다.
     * @param searchTerm 사용자가 입력한 검색어
     * @return 추천된 검색어와 관련된 정보가 담긴 DTO
     * @throws IOException Elasticsearch 검색 실패 시 발생
     */
    RecommendDto recommendBoardSearchTerm(String searchTerm) throws IOException;

}
