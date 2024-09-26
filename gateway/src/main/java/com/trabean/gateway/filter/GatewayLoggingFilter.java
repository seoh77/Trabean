package com.trabean.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class GatewayLoggingFilter extends AbstractGatewayFilterFactory<GatewayLoggingFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(GatewayLoggingFilter.class);

    public GatewayLoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String requestPath = exchange.getRequest().getURI().toString();
            logger.info("Request Path: {}", requestPath);
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                String responsePath = exchange.getResponse().getHeaders().getLocation() != null ?
                        exchange.getResponse().getHeaders().getLocation().toString() : "No response location";
                logger.info("Response Path: {}", responsePath);
            }));
        };
    }

    public static class Config {
        // Add any configuration parameters if needed
    }
}
