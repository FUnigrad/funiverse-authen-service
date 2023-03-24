package com.unigrad.funiverseauthenservice.service.impl;

import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.entity.Workspace;
import com.unigrad.funiverseauthenservice.service.IAppCommunicateService;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Service
public class AppCommunicateService implements IAppCommunicateService {

    //need to save Token in some where and use it when call to another service;

    private final Logger LOG = LoggerFactory.getLogger(AppCommunicateService.class);

    private WebClient webClient;

    @Override
    public boolean saveUser(User user, String domain) {
        setUrl(domain);

        Mono<Boolean> isSuccessful =  webClient.post()
                .uri("/api/user")
                .bodyValue(user)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                });

        if (Boolean.TRUE.equals(isSuccessful.block())) {
            LOG.info("Save Workspace Admin into %s successful".formatted(domain));
            return true;
        } else {
            LOG.error("An error occurs when calling to %s. Can not save Workspace Admin".formatted(domain));
            return false;
        }
    }

    @Override
    public boolean saveWorkspace(Workspace workspace, String domain) {
        setUrl(domain);

        Mono<Boolean> isSuccessful =  webClient.post()
                .uri("/api/workspace")
                .bodyValue(workspace)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                });

        if (Boolean.TRUE.equals(isSuccessful.block())) {
            LOG.info("Save Workspace Information into %s successful".formatted(domain));
            return true;
        } else {
            LOG.error("An error occurs when calling to %s. Can not save Workspace Information".formatted(domain));
            return false;
        }
    }

    private void setUrl(String url) {
        try {
            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();

            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

            webClient = WebClient.builder()
                    .baseUrl("http://" + ("localhost:8080".equals(url) ? url : "api." + url))
                    .defaultHeader("Origin", "3.1.47.236:30001")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();

        } catch (SSLException e) {
            throw new RuntimeException(e);
        }
    }
}