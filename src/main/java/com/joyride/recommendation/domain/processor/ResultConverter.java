package com.joyride.recommendation.domain.processor;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

@Component
public class ResultConverter {
    public String[] convertToStringArray(PriorityQueue<JSONObject> topHits) {
        String[] result = new String[topHits.size()];
        List<JSONObject> sortedHits = new ArrayList<>(topHits);

        for (int i = 0; i < sortedHits.size(); i++) {
            JSONObject hit = sortedHits.get(i);
            JSONObject source = hit.optJSONObject("_source");
            if (source != null) {
                result[i] = source.has("area_name") ? source.optString("area_name") : source.optString("franchise_name");
            } else {
                result[i] = "";
            }
        }

        return result;
    }
}
