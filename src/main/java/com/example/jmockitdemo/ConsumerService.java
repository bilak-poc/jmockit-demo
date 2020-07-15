package com.example.jmockitdemo;

import java.util.List;

public interface ConsumerService {

  void consume(List<TestServiceImpl.IntegrationDto> integrationDtos);
}
