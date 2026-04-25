package com.yf.yuanfen.user.service;

import com.yf.yuanfen.common.exception.BizException;
import com.yf.yuanfen.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());
        ReflectionTestUtils.setField(fileStorageService, "baseUrl", "http://localhost:8080");
    }

    @Test
    void storeAvatar_validJpeg_returnsUrl() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", new byte[1024]);

        String url = fileStorageService.storeAvatar(1L, file);

        assertThat(url).startsWith("http://localhost:8080/uploads/avatars/1/");
        assertThat(url).endsWith(".jpg");
    }

    @Test
    void storeAvatar_fileTooLarge_throwsBizException() {
        byte[] bigFile = new byte[6 * 1024 * 1024]; // 6 MB
        MockMultipartFile file = new MockMultipartFile(
                "file", "big.jpg", "image/jpeg", bigFile);

        assertThatThrownBy(() -> fileStorageService.storeAvatar(1L, file))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getErrorCode())
                .isEqualTo(ErrorCode.FILE_TOO_LARGE);
    }

    @Test
    void storeAvatar_unsupportedType_throwsBizException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", new byte[1024]);

        assertThatThrownBy(() -> fileStorageService.storeAvatar(1L, file))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getErrorCode())
                .isEqualTo(ErrorCode.FILE_TYPE_NOT_SUPPORTED);
    }
}
