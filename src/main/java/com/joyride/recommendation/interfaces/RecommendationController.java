package com.joyride.recommendation.interfaces;

import com.joyride.recommendation.domain.service.ElasticsearchService;
import com.joyride.recommendation.interfaces.dto.RecommendDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "추천")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendation")
public class RecommendationController {

    private final ElasticsearchService elasticsearchService;

    @GetMapping("/area")
    public ResponseEntity<RecommendDto> getAreaRecommendation(@RequestParam String areaName) throws IOException {
        return ResponseEntity.ok()
                        .body(elasticsearchService.searchByAreaName(areaName));
    }

    @GetMapping("/board/search-term")
    public ResponseEntity<RecommendDto> getBoardSearchTermRecommendation(@RequestParam String searchTerm) throws IOException{
        return ResponseEntity.ok()
                .body(elasticsearchService.recommendBoardSearchTerm(searchTerm));
    }
}
