package com.joyride.recommendation.domain.processor;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.PriorityQueue;

@Component
public class ResultBalancer {
    public void balanceResults(JSONArray responses, PriorityQueue<JSONObject> topHits, int topN) {
        int minResultsPerIndex = (int) Math.ceil(topN * 0.2);
        int areaCount = countResultsByType(topHits, true);
        int franchiseCount = countResultsByType(topHits, false);

        ensureMinimumRepresentation(responses, topHits, areaCount, minResultsPerIndex, topN, true);
        ensureMinimumRepresentation(responses, topHits, franchiseCount, minResultsPerIndex, topN, false);
    }

    private int countResultsByType(PriorityQueue<JSONObject> topHits, boolean isArea) {
        return (int) topHits.stream()
                .filter(hit -> isArea == hit.optJSONObject("_source").has("area_name"))
                .count();
    }

    private void ensureMinimumRepresentation(JSONArray responses, PriorityQueue<JSONObject> topHits,
                                             int count, int minResultsPerIndex, int topN, boolean isArea) {
        while (count < minResultsPerIndex && topHits.size() < topN) {
            for (int i = 0; i < responses.length(); i++) {
                JSONArray hits = responses.getJSONObject(i).optJSONObject("hits").optJSONArray("hits");
                if (hits == null) continue;

                for (int j = 0; j < hits.length(); j++) {
                    JSONObject hit = hits.optJSONObject(j);
                    if (hit == null || hit.optJSONObject("_source") == null) continue;

                    if ((isArea == hit.optJSONObject("_source").has("area_name")) && !topHits.contains(hit)) {
                        topHits.offer(hit);
                        count++;
                        if (count == minResultsPerIndex || topHits.size() == topN) return;
                    }
                }
            }
            if (topHits.size() == topN) return;
        }
    }
}
