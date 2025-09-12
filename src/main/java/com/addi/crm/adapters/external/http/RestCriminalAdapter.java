package com.addi.crm.adapters.external.http;
import com.addi.crm.domain.model.CriminalExternalResponse;
import com.addi.crm.ports.outbound.ExternalCriminalPort;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
@Component
@RequiredArgsConstructor
@Getter
@Setter
public class RestCriminalAdapter implements ExternalCriminalPort {
    private final RestTemplate restTemplate;
    @Value("${stub.criminal.base-url}")
    private String baseUrl;

    @Override
    public CriminalExternalResponse verify(String nationalId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl).queryParam("nationalId", nationalId).build().toUri();
        return restTemplate.getForObject(uri, CriminalExternalResponse.class);
    }
}
