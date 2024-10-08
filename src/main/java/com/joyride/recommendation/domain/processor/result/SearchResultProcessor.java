package com.joyride.recommendation.domain.processor.result;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class SearchResultProcessor {
    // String[]**는 고정된 수의 데이터에 대해 빠른 접근이 필요할 때 유용
    // 응답을 파싱하고 상위 N개의 area_name을 반환
    public String[] parseTopAreaNames(String responseBody, int topN) {
        // 응답 본문을 JSON 객체로 변환
        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONArray hits = jsonResponse.getJSONObject("hits").getJSONArray("hits");

        String[] topAreaNames = new String[Math.min(topN, hits.length())];
        for (int i = 0; i < topAreaNames.length; i++) {
            topAreaNames[i] = hits.getJSONObject(i).getJSONObject("_source").getString("area_name");
        }

        return topAreaNames;
    }
}
