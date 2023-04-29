package ru.ptkom.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ControlPanelBackConfiguration {

    @Bean(name="RestTemplateWithoutAuth")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
