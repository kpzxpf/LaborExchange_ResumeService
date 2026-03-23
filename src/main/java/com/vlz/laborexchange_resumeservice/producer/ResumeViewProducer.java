package com.vlz.laborexchange_resumeservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlz.laborexchange_resumeservice.dto.ResumeViewEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResumeViewProducer extends AbstractProducer<ResumeViewEvent> {

    @Value("${spring.kafka.topics.resume-views}")
    private String resumeViewsTopicName;

    public ResumeViewProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    public void send(ResumeViewEvent event) {
        super.sendMessage(resumeViewsTopicName, event);
        log.debug("Sent resume view event for resume {}", event.getResumeId());
    }
}
