package com.addi.crm.adapter;

import com.addi.crm.adapters.external.http.RestIdentityAdapter;
import com.addi.crm.domain.model.IdentityExternalResponse;
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

class RestIdentityAdapterTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private RestIdentityAdapter restIdentityAdapter;

  private final String baseUrl = "http://localhost:8080/stub/identity/check";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    try {
      var field = RestIdentityAdapter.class.getDeclaredField("baseUrl");
      field.setAccessible(true);
      field.set(restIdentityAdapter, baseUrl);
    } catch (Exception e) {
      throw new RuntimeException("Failed to set baseUrl", e);
    }
  }

  @Test
  void verify_ShouldReturnIdentityExternalResponse() {
    String nationalId = "987654321";
    URI expectedUri = UriComponentsBuilder
            .fromHttpUrl(baseUrl)
            .queryParam("nationalId", nationalId)
            .build()
            .toUri();

    IdentityExternalResponse expectedResponse = new IdentityExternalResponse();
    expectedResponse.setMatches(true);

    when(restTemplate.getForObject(expectedUri, IdentityExternalResponse.class))
            .thenReturn(expectedResponse);

    IdentityExternalResponse actualResponse = restIdentityAdapter.verify(nationalId);

    assertThat(actualResponse).isNotNull();
    assertThat(actualResponse.isMatches()).isTrue();
    verify(restTemplate, times(1)).getForObject(expectedUri, IdentityExternalResponse.class);
  }
}
