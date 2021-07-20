package com.megait.myhome.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("app")  // 설정 중 app으로 시작하는 설정은 여기서 받겠다.
public class AppProperties {
    private String host;
}
