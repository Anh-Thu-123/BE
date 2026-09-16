package com.nagare.common.counter;

import java.time.Year;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Sinh ma nghiep vu tuan tu kieu BK-2026-0001. Dung findAndModify (upsert, returnNew)
 * de tang nguyen tu ngay ca khi nhieu request cung sinh ma trong cung mot khoanh khac -
 * day la ly do khong dung count() hay lay max() roi +1.
 */
@Service
public class SequenceGeneratorService {

    private final MongoTemplate mongoTemplate;

    public SequenceGeneratorService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public String nextCode(String prefix) {
        int year = Year.now().getValue();
        String counterId = prefix + "-" + year;
        Query query = Query.query(where("_id").is(counterId));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);
        Counter counter = mongoTemplate.findAndModify(query, update, options, Counter.class);
        long seq = counter != null ? counter.getSeq() : 1;
        return String.format("%s-%04d", counterId, seq);
    }
}
