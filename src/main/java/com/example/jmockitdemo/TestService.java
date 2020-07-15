package com.example.jmockitdemo;

import java.util.List;

public interface TestService {

  List<TestServiceImpl.IntegrationDto> callIntegration();
}
