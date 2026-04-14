package com.example.blog.service;

import com.example.blog.common.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final long MAX_BYTES = 2 * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = new HashSet<>(Arrays.asList("image/jpeg", "image/png", "image/jpg"));

    @Value("${blog.avatar.storage-dir:user_logo_images}")
    private String storageDir;

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Paths.get(storageDir));
    }

    public String storeAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BusinessException(400, "请选择图片文件");
        if (file.getSize() > MAX_BYTES) throw new BusinessException(413, "图片大小不能超过 2MB");
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new BusinessException(400, "仅支持 png / jpg / jpeg 格式");
        }
        String ext = contentType.toLowerCase(Locale.ROOT).contains("png") ? "png" : "jpg";
        String filename = "user_logo_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_"
                + UUID.randomUUID().toString().substring(0, 8) + "." + ext;
        Path target = Paths.get(storageDir).toAbsolutePath().normalize().resolve(filename);
        try {
            file.transferTo(target.toFile());
        } catch (IOException e) {
            throw new BusinessException(500, "保存文件失败");
        }
        return filename;
    }
}
