package com.nagare.common.storage;

import com.mongodb.client.gridfs.model.GridFSFile;
import java.io.IOException;
import java.io.InputStream;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** Luu/doc tep qua GridFS (fs.files, fs.chunks trong MongoDB) - thay cho Cloudinary. */
@Service
public class FileStorageService {

    private final GridFsTemplate gridFsTemplate;

    public FileStorageService(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    /** Luu tep, tra ve id (hex ObjectId) dung lam publicId/docId trong cac model khac. */
    public String store(MultipartFile file, String folder) throws IOException {
        Document metadata = new Document("folder", folder).append("originalFilename", file.getOriginalFilename());
        ObjectId id = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(),
                file.getContentType(), metadata);
        return id.toHexString();
    }

    public record StoredFile(InputStream content, String contentType, String filename) {}

    public StoredFile load(String id) {
        GridFSFile gridFsFile = gridFsTemplate.findOne(new Query(org.springframework.data.mongodb.core.query.Criteria
                .where("_id").is(new ObjectId(id))));
        if (gridFsFile == null) {
            throw com.nagare.common.error.ApiException.notFound("Tep");
        }
        try {
            InputStream stream = gridFsTemplate.getResource(gridFsFile).getInputStream();
            String contentType = gridFsFile.getMetadata() != null
                    ? gridFsFile.getMetadata().getString("_contentType") : "application/octet-stream";
            return new StoredFile(stream, contentType != null ? contentType : "application/octet-stream",
                    gridFsFile.getFilename());
        } catch (IOException e) {
            throw new IllegalStateException("Khong doc duoc tep " + id, e);
        }
    }
}
