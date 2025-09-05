package com.kh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration // 이 클래스가 스프링 설정 클래스임을 나타냅니다.
public class AppConfig {

    /**
     * RestTemplate 빈을 생성하여 스프링 컨테이너에 등록하는 메서드
     * @return 생성된 RestTemplate 객체
     */
    @Bean // 이 메서드가 반환하는 객체를 스프링 빈으로 등록합니다.
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}