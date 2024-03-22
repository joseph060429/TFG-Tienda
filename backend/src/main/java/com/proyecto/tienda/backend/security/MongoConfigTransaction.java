package com.proyecto.tienda.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfigTransaction {

    private final MongoTemplate mongoTemplate;

    public MongoConfigTransaction(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Bean
    MongoTransactionManager transactionManager() {
        return new MongoTransactionManager(mongoTemplate.getMongoDatabaseFactory());
    }
}