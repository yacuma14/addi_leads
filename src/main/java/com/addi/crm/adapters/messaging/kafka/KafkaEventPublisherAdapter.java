package com.addi.crm.adapters.messaging.kafka;

import com.addi.crm.ports.outbound.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisherAdapter implements EventPublisherPort {
    private final KafkaTemplate<String, Object> kafka;
    @Override
    public void publish(String topic, Object event) {
        kafka.send(topic, event);
    }
}