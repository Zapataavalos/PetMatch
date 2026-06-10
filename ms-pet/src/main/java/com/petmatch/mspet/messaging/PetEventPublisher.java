package com.petmatch.mspet.messaging;

public interface PetEventPublisher {

    void publish(String action, String entityType, Object data);
}
