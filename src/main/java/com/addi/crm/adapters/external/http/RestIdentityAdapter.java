package com.addi.crm.adapters.external.http;
import com.addi.crm.domain.model.IdentityExternalResponse;
import com.addi.crm.ports.outbound.ExternalIdentityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
@Component
@RequiredArgsConstructor
public class RestIdentityAdapter implements ExternalIdentityPort {

    private final RestTemplate restTemplate;

    @Value("${stub.identity.base-url}") private String baseUrl;
    @Override
    public IdentityExternalResponse verify(String nationalId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl).queryParam("nationalId", nationalId).build().toUri();
        return restTemplate.getForObject(uri, IdentityExternalResponse.class);
    }
}
