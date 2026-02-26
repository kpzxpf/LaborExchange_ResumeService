package com.vlz.laborexchange_resumeservice.producer;

public interface KafkaProducer<T> {
    void send(T event);
}