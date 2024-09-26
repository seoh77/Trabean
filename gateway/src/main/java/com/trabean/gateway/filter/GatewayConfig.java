package com.trabean.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class GatewayConfig {

    private static final Logger logger = LoggerFactory.getLogger(GatewayConfig.class);
    private final DiscoveryClient discoveryClient;

    public GatewayConfig(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("service_id", r -> r.path("/your-path/**")
                        .filters(f -> f.filter((exchange, chain) -> {
                            logRequestDetails(exchange, "your-microservice");
                            return chain.filter(exchange);
                        }))
                        .uri("lb://your-microservice")) // Eureka에서 마이크로서비스를 찾음
                .build();
    }

    private void logRequestDetails(ServerWebExchange exchange, String serviceId) {
        String requestPath = exchange.getRequest().getURI().getPath();
        String requestMethod = String.valueOf(exchange.getRequest().getMethod());

        // Eureka에서 서비스 인스턴스 정보 가져오기
        String applicationName = getApplicationName(serviceId);

        logger.info("요청 경로: {}", requestPath);
        logger.info("HTTP 메서드: {}", requestMethod);
        logger.info("전달될 마이크로서비스 ID: {}", serviceId);
        logger.info("전달될 마이크로서비스 애플리케이션 이름: {}", applicationName);
        logger.info("대상 마이크로서비스 주소: {}", exchange.getRequest().getURI().toString());
    }

    private String getApplicationName(String serviceId) {
        // Eureka에서 서비스의 인스턴스 목록을 가져옴
        return discoveryClient.getServices().stream()
                .filter(service -> service.equals(serviceId))
                .findFirst()
                .orElse("알 수 없는 서비스");
    }
}
