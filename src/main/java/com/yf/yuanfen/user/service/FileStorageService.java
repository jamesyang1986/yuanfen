package com.yf.yuanfen.user.service;

import com.yf.yuanfen.common.exception.BizException;
import com.yf.yuanfen.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024L;
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    public String storeAvatar(Long userId, MultipartFile file) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                : "";

        String filename = UUID.randomUUID() + ext;
        Path userDir = Paths.get(uploadDir, userId.toString());

        try {
            Files.createDirectories(userDir);
            Path dest = userDir.resolve(filename);
            file.transferTo(dest.toFile());
        } catch (IOException e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR);
        }

        return baseUrl + "/uploads/avatars/" + userId + "/" + filename;
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new BizException(ErrorCode.FILE_TOO_LARGE);
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BizException(ErrorCode.FILE_TYPE_NOT_SUPPORTED);
        }
    }
}
