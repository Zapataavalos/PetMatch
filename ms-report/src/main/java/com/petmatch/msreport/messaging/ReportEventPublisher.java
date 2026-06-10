package com.petmatch.msreport.messaging;

public interface ReportEventPublisher {

    void publish(String action, String entityType, Object data);
}
