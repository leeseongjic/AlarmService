package com.context.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableTransactionManagement
@Lazy
public class RestTemplateConfig {

    @Bean(name = "restTemplate")
    public RestTemplate restTemplate() {

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(300); // 읽기타임아웃(milliseconds) - 5분동안 연결은 되었지만 처리 속도가 느려 응답 없으면 타임아웃
        factory.setConnectTimeout(
                200); // 연결타임아웃(milliseconds) - 5초동안 연결이 되지 않으면 타임아웃(잘못된 주소 연결 혹은 서비스 다운)

        // 커넥션 풀 설정
        CloseableHttpClient httpClient =
                HttpClientBuilder.create() //
                        .setMaxConnTotal(300) // 커넥션풀적용(최대 오픈되는 커넥션 수, IP:PORT 관계없는 전체 커넥션 수)
                        .setMaxConnPerRoute(50) // 커넥션풀적용(IP:포트 1쌍에 대해 수행 할 연결 수제한, IP:PORT 마다 커넥션 수)
                        .evictIdleConnections(
                                2000L,
                                TimeUnit
                                        .MILLISECONDS) // 서버에서 keepalive 시간동안 미 사용한 커넥션을 죽이는 등의 케이스 방어로 idle커넥션을 주기적으로
                        // 지움
                        .build();
        factory.setHttpClient(httpClient);
        // 커넥션 풀 설정

        return new RestTemplate(factory);
    }

    @Bean(name = "longRestTemplate")
    public RestTemplate longRestTemplate() {

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(500); // 읽기타임아웃(milliseconds) - 5분동안 연결은 되었지만 처리 속도가 느려 응답 없으면 타임아웃
        factory.setConnectTimeout(
                500); // 연결타임아웃(milliseconds) - 5초동안 연결이 되지 않으면 타임아웃(잘못된 주소 연결 혹은 서비스 다운)

        // 커넥션 풀 설정
        CloseableHttpClient httpClient =
                HttpClientBuilder.create() //
                        .setMaxConnTotal(1000) // 커넥션풀적용(최대 오픈되는 커넥션 수, IP:PORT 관계없는 전체 커넥션 수)
                        .setMaxConnPerRoute(500) // 커넥션풀적용(IP:포트 1쌍에 대해 수행 할 연결 수제한, IP:PORT 마다 커넥션 수)
                        .evictIdleConnections(
                                2000L,
                                TimeUnit
                                        .MILLISECONDS) // 서버에서 keepalive 시간동안 미 사용한 커넥션을 죽이는 등의 케이스 방어로 idle커넥션을 주기적으로
                        // 지움
                        .build();
        factory.setHttpClient(httpClient);
        // 커넥션 풀 설정

        return new RestTemplate(factory);
    }

    @Bean(name = "retryRestTemplate")
    public RestTemplate retryRestTemplate() throws Exception {

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(8000); // 읽기타임아웃(milliseconds)
        factory.setConnectTimeout(8000); // 연결타임아웃(milliseconds)

        // 커넥션 풀 설정
        HttpClient httpClient =
                HttpClientBuilder.create() //
                        .setMaxConnTotal(3000) // 커넥션풀적용(최대 오픈되는 커넥션 수, IP:PORT 관계없는 전체 커넥션 수)
                        .setMaxConnPerRoute(3000) // 커넥션풀적용(IP:포트 1쌍에 대해 수행 할 연결 수제한, IP:PORT 마다 커넥션 수)
                        .evictIdleConnections(
                                1000L,
                                TimeUnit
                                        .MILLISECONDS) // 서버에서 keepalive 시간동안 미 사용한 커넥션을 죽이는 등의 케이스 방어로 idle커넥션을 주기적으로
                        // 지움
                        .build();
        factory.setHttpClient(httpClient);
        // 커넥션 풀 설정

        return new RestTemplate(factory) {
            @Override
            @Retryable(
                    value = ResourceAccessException.class,
                    maxAttempts = 2,
                    backoff = @Backoff(delay = 1000))
            public <T> ResponseEntity<T> exchange(
                    URI url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType)
                    throws RestClientException {
                return super.exchange(url, method, requestEntity, responseType);
            }
        };
    }
}
