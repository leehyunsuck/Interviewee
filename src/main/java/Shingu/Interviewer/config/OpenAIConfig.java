package Shingu.Interviewer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

//OpenAI API에 필요한 설정을 제공 (API키, HHTP 요청 헤더 설정 등)
@Configuration
public class OpenAIConfig {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Bean
    public RestTemplate template() {
        RestTemplate resetTemplate = new RestTemplate();
        resetTemplate.getInterceptors().add((request, body, execution) -> {
           request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);
           return execution.execute(request, body);
        });
        return resetTemplate;
    }
}
