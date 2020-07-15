package com.example.jmockitdemo;

import java.net.URI;
import java.util.List;

import org.springframework.web.reactive.function.client.WebClient;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Slf4j
public class TestServiceImpl implements TestService {

  public static final String FIRST_URL = "http://somewhere.com/first-endpoint";
  public static final String SECOND_URL = "http://somewhere.com/second-endpoint";

  private final WebClient webClient;
  private final ConsumerService consumerService;

  @Override
  public List<IntegrationDto> callIntegration() {
    return getFirstResults().collectList()
      .flatMap(firstEndpointResult1 -> {
        log.debug("having first endpoint 1 results {}", firstEndpointResult1);
        consumerService.consume(firstEndpointResult1);
        return getFirstResults().collectList();
      })
      .flatMap(firstEndpointResult2 -> {
        log.debug("having first endpoint 2 results {}", firstEndpointResult2);
        consumerService.consume(firstEndpointResult2);
        return getSecondResults().collectList();
      })
      .flatMap(secondEndpointResult1 -> {
        log.debug("having second endpoint 1 result {}", secondEndpointResult1);
        consumerService.consume(secondEndpointResult1);
        return getSecondResults().collectList();
      })
      .flatMap(secondEndpointResult2 -> {
        log.debug("having second endpoint 2 result {}", secondEndpointResult2);
        consumerService.consume(secondEndpointResult2);
        return getSecondResults().collectList();
      })
      .block();
  }

  private Flux<IntegrationDto> getFirstResults() {
    return webClient.get()
      .uri(URI.create(FIRST_URL))
      .retrieve()
      .bodyToFlux(IntegrationDto.class);
  }

  private Flux<IntegrationDto> getSecondResults() {
    return webClient.get()
      .uri(URI.create(SECOND_URL))
      .retrieve()
      .bodyToFlux(IntegrationDto.class);
  }

  @Data
  public static class IntegrationDto {
    private final String name;
  }

}
