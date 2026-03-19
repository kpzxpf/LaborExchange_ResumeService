package com.vlz.laborexchange_resumeservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlz.laborexchange_resumeservice.dto.ResumeIndexEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResumeIndexProducer extends AbstractProducer<ResumeIndexEvent> {

    @Value("${spring.kafka.topics.indexing-resume}")
    private String resumeIndexTopicName;

    public ResumeIndexProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    public void send(ResumeIndexEvent event) {
        super.sendMessage(resumeIndexTopicName, event);
        log.info("Sending resume index event: {}", event);
    }
}
