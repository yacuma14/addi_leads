package com.addi.crm.adapter;

import com.addi.crm.adapters.external.http.RestCriminalAdapter;
import com.addi.crm.domain.model.CriminalExternalResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class RestCriminalAdapterTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private RestCriminalAdapter restCriminalAdapter;

  private final String baseUrl = "http://localhost:8080/stub/criminal/check";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    restCriminalAdapter.setBaseUrl(baseUrl);
  }

  @Test
  void verify_ShouldReturnCriminalExternalResponse() {
    String nationalId = "123456789";
    URI expectedUri = UriComponentsBuilder
            .fromHttpUrl(baseUrl)
            .queryParam("nationalId", nationalId)
            .build()
            .toUri();

    CriminalExternalResponse expectedResponse = new CriminalExternalResponse();
    expectedResponse.setHasRecords(true);

    when(restTemplate.getForObject(expectedUri, CriminalExternalResponse.class))
            .thenReturn(expectedResponse);
    CriminalExternalResponse actualResponse = restCriminalAdapter.verify(nationalId);

    assertThat(actualResponse).isNotNull();
    assertThat(actualResponse.isHasRecords()).isTrue();
    verify(restTemplate, times(1)).getForObject(expectedUri, CriminalExternalResponse.class);
  }
}
