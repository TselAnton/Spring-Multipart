package com.tsel.multipart.service;

import com.tsel.multipart.data.FileInfo;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    void save(MultipartFile file);

    List<FileInfo> getAllFilesInfo();

    void deleteFile(String fileName);

    void clearAllStorage();

    Resource loadFileFromStorage(String fileName);
}
