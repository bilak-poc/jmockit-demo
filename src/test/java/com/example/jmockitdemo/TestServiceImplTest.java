package com.example.jmockitdemo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import reactor.core.publisher.Flux;

class TestServiceImplTest {

  private static final String INTEGRATION_1_1 = "int_1_1";
  private static final String INTEGRATION_1_2 = "int_1_2";
  private static final String INTEGRATION_2_1 = "int_2_1";
  private static final String INTEGRATION_2_2 = "int_2_2";

  @Tested
  private TestServiceImpl tested;
  @Injectable
  private WebClient webClient;
  @Injectable
  private ConsumerService consumerService;

  @Test
  void testExpectationsReturnsAfterEachMock() {
    new Expectations() {
      {
        webClient.get()
          .uri(URI.create(TestServiceImpl.FIRST_URL))
          .retrieve()
          .bodyToFlux(TestServiceImpl.IntegrationDto.class);
        returns(
          Flux.just(mockIntegrationDto(INTEGRATION_1_1)),
          Flux.just(mockIntegrationDto(INTEGRATION_1_2))
        );

        webClient.get()
          .uri(URI.create(TestServiceImpl.SECOND_URL))
          .retrieve()
          .bodyToFlux(TestServiceImpl.IntegrationDto.class);
        returns(
          Flux.just(mockIntegrationDto(INTEGRATION_2_1)),
          Flux.just(mockIntegrationDto(INTEGRATION_2_2))
        );
      }
    };

    final List<TestServiceImpl.IntegrationDto> result = tested.callIntegration();

    new Verifications(){{
      consumerService.consume(List.of(mockIntegrationDto(INTEGRATION_1_1)));
      consumerService.consume(List.of(mockIntegrationDto(INTEGRATION_1_2)));
      consumerService.consume(List.of(mockIntegrationDto(INTEGRATION_2_1)));
      consumerService.consume(List.of(mockIntegrationDto(INTEGRATION_2_2)));
    }};

    assertThat(result.size(), is(1));
    assertThat(result.get(0).getName(), is(INTEGRATION_2_2));
  }


  @Test
  void testExpectationsResultsAfterLastMock(){
    new Expectations(){{
      webClient.get()
        .uri(URI.create(TestServiceImpl.FIRST_URL))
        .retrieve()
        .bodyToFlux(TestServiceImpl.IntegrationDto.class);

      webClient.get()
        .uri(URI.create(TestServiceImpl.SECOND_URL))
        .retrieve()
        .bodyToFlux(TestServiceImpl.IntegrationDto.class);

      returns(
        Flux.just(mockIntegrationDto(INTEGRATION_1_1)),
        Flux.just(mockIntegrationDto(INTEGRATION_1_2)),
        Flux.just(mockIntegrationDto(INTEGRATION_2_1)),
        Flux.just(mockIntegrationDto(INTEGRATION_2_2))
      );
    }};

    final List<TestServiceImpl.IntegrationDto> result = tested.callIntegration();

    new Verifications(){{
      consumerService.consume(List.of(mockIntegrationDto(INTEGRATION_1_1)));
      consumerService.consume(List.of(mockIntegrationDto(INTEGRATION_1_2)));
      consumerService.consume(List.of(mockIntegrationDto(INTEGRATION_2_1)));
      consumerService.consume(List.of(mockIntegrationDto(INTEGRATION_2_2)));
    }};

    assertThat(result.size(), is(1));
    assertThat(result.get(0).getName(), is(INTEGRATION_2_2));
  }

  private TestServiceImpl.IntegrationDto mockIntegrationDto(String name) {
    return new TestServiceImpl.IntegrationDto(name);
  }

}