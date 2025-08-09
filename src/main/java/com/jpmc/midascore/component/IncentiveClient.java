package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IncentiveClient {
    private final RestTemplate restTemplate;

    public IncentiveClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public float fetchIncentiveAmount(Transaction transaction) {
        ResponseEntity<Incentive> response = restTemplate.postForEntity("http://localhost:8080/incentive", transaction, Incentive.class);
        Incentive body = response.getBody();
        return body == null ? 0f : Math.max(0f, body.getAmount());
    }
}


