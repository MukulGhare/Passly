package com.passly.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    /** Uploads a file and returns the public URL. */
    String upload(MultipartFile file, String folder);

    /** Deletes a file by its public ID (Cloudinary) or key (S3). */
    void delete(String publicId);
}
