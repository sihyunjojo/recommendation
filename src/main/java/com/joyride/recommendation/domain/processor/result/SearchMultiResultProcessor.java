package com.joyride.recommendation.domain.processor.result;

import com.joyride.recommendation.domain.processor.ResultBalancer;
import com.joyride.recommendation.domain.processor.ResultCollector;
import com.joyride.recommendation.domain.processor.ResultConverter;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SearchMultiResultProcessor {

    private final ResultCollector resultCollector;
    private final ResultBalancer resultBalancer;
    private final ResultConverter resultConverter;

    public String[] parseMultiIndexResults(String responseBody, int topN) {
        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONArray responses = jsonResponse.getJSONArray("responses");

        PriorityQueue<JSONObject> topHits = resultCollector.collectTopHits(responses, topN);
        resultBalancer.balanceResults(responses, topHits, topN);

        return resultConverter.convertToStringArray(topHits);
    }
}


