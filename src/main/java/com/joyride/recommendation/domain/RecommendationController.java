package com.joyride.recommendation.domain;

import com.joyride.recommendation.dto.RecommendDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendation")
public class RecommendationController {

    private final ElasticsearchService elasticsearchService;

    @GetMapping("/area")
    public ResponseEntity<RecommendDto> getAreaRecommendation(@RequestParam String areaName) {
        System.out.println(areaName);
        return ResponseEntity.ok()
                        .body(elasticsearchService.searchByAreaName(areaName));
    }

    @GetMapping("/board/search-term")
    public ResponseEntity<RecommendDto> getBoardSearchTermRecommendation(@RequestParam String searchTerm) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return ResponseEntity.ok()
                .body(elasticsearchService.searchByAreaNameForMultipleIndexes(searchTerm));
    }
}
