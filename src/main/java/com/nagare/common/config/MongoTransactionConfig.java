package com.nagare.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

/**
 * Atlas M0 la replica set nen ho tro transaction day du. Ba thao tac BAT BUOC dung transaction
 * theo CLAUDE.md: giu cho (booking hold), sua danh sach khach (pax update), xac nhan don (booking confirm).
 */
@Configuration
public class MongoTransactionConfig {

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}
