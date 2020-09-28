package com.tsel.multipart.controller;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import com.tsel.multipart.data.FileInfo;
import com.tsel.multipart.data.ResponseMessage;
import com.tsel.multipart.data.UnloadedFile;
import com.tsel.multipart.service.FileStorageService;
import com.tsel.multipart.utils.StreamUtils;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileController {

    private static final String SUCCESS_FILE_SAVE_MSG = "The file was saved successfully!";

    private final FileStorageService storageService;

    public FileController(@Autowired FileStorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping(produces = "text/plain; charset=UTF-8")
    public ResponseEntity<String> getAllEndPoints() {
        return ResponseEntity.ok("All available endpoints:\n" +
                "  /upload (MultipartFile: file) - Upload a file to the storage with check extension\n" +
                "  /upload/all (List of MultipartFile: files) - Upload multiple files to storage\n" +
                "  /unload (String : fileName) - Get file from repository by file name\n" +
                "  /list - Get list of all file in the storage\n" +
                "  /delete (String: fileName) - Delete file from storage by file name\n" +
                "  /delete/all - Delete all files from storage"
        );
    }

    @PostMapping(value = "/upload", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam MultipartFile file) {
        storageService.save(file);
        return buildSuccessMessage(SUCCESS_FILE_SAVE_MSG);
    }

    @PostMapping(value = "/upload/all", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> uploadAllFile(@RequestParam MultipartFile[] files) {
        storageService.saveAll(files);
        return buildSuccessMessage("All files were saved successfully!");
    }

    @GetMapping("/unload")
    public void getFileFromStorage(@RequestParam String fileName, HttpServletResponse response) throws IOException {
        UnloadedFile unloadedFile = storageService.loadFileFromStorage(fileName);

        response.setContentLength((int) unloadedFile.getContentLength());
        response.setContentType(unloadedFile.getContentType());
        StreamUtils.stream(unloadedFile.getStream(), response.getOutputStream());
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileInfo>> getListOfStorageFiles() {
        return ResponseEntity.ok(storageService.getAllFilesInfo());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseMessage> deleteFile(@RequestParam String fileName) {
        storageService.deleteFile(fileName);
        return buildSuccessMessage("File was deleted successfully!");
    }

    @DeleteMapping("/delete/all")
    public ResponseEntity<ResponseMessage> clearStorage() {
        storageService.clearAllStorage();
        return buildSuccessMessage("The storage has been successfully cleaned up!");
    }

    private ResponseEntity<ResponseMessage> buildSuccessMessage(String message) {
        return ResponseEntity.ok(new ResponseMessage(message));
    }
}
