package com.joyride.recommendation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;


@Configuration
@OpenAPIDefinition(
        servers = {
                @Server(url = "/", description = "API 서버")
        },
        tags = {
                @Tag(name = "추천"),
        }
)
public class SwaggerConfig {
    /**
     * 이 코드는 ObjectMapper의 네이밍 전략을 snake_case로 설정한 후,
     * 해당 ObjectMapper를 사용하는 **ModelResolver**를 Spring 빈으로 등록합니다.
     * 이렇게 설정하면, Swagger나 다른 API 문서화 도구에서 snake_case를 사용하여 모델의 필드 이름을 처리하게 됩니다.
     * @param objectMapper
     * @return
     */
    @Bean
    public ModelResolver modelResolver(ObjectMapper objectMapper) {
        return new ModelResolver(objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE));
    }
}
