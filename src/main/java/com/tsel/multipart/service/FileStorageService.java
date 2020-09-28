package com.tsel.multipart.service;

import com.tsel.multipart.data.FileInfo;
import com.tsel.multipart.data.UnloadedFile;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    void save(MultipartFile file);

    void saveAll(MultipartFile[] files);

    List<FileInfo> getAllFilesInfo();

    void deleteFile(String fileName);

    void clearAllStorage();

    UnloadedFile loadFileFromStorage(String fileName);
}
