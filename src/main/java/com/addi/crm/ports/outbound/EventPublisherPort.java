package com.addi.crm.ports.outbound;

public interface EventPublisherPort {
    void publish(String topic, Object event);
}
