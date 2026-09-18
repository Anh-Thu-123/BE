package com.nagare.common.storage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

/**
 * Luu anh tour va tai lieu visa truc tiep trong MongoDB qua GridFS (fs.files/fs.chunks),
 * thay cho Cloudinary - theo yeu cau chuyen sang khong dung dich vu ngoai.
 * Luu y: Atlas M0 gioi han 512MB, anh/tep nhieu se day nhanh hon so voi dung CDN rieng.
 */
@Configuration
public class GridFsConfig {

    @Bean
    public GridFsTemplate gridFsTemplate(MongoDatabaseFactory dbFactory, MongoConverter converter) {
        return new GridFsTemplate(dbFactory, converter);
    }
}
