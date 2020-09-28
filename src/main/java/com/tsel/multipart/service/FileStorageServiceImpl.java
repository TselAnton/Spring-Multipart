package com.tsel.multipart.service;

import static com.tsel.multipart.exception.MyWebException.throwEx;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

import com.tsel.multipart.data.FileInfo;
import com.tsel.multipart.data.UnloadedFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageServiceImpl.class);
    private static final String STORAGE_CLEAN_ERROR = "Error while clearing storage!";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");

    @Value("${file.storage.home.dir}")
    private String filePath;

    @Value("${file.storage.home.clear}")
    private Boolean clearDir;

    @Value("#{'${file.storage.allowed.extensions}'.split(',')}")
    private List<String> allowedExtensions;

    private Path storagePath;

    @PostConstruct
    private void updateStorage() throws IOException {
        LOGGER.info("Storage path is \"{}\"", filePath);
        storagePath = Paths.get(filePath).toAbsolutePath().normalize();

        if (Files.isDirectory(storagePath) && TRUE.equals(clearDir)) {
            if (clearDir(storagePath.toFile())) {
                LOGGER.info("Storage cleaned");
            } else {
                LOGGER.error(STORAGE_CLEAN_ERROR);
                throw new IOException(STORAGE_CLEAN_ERROR);
            }
        }

        if (!Files.isDirectory(storagePath)) {
            Files.createDirectory(storagePath);
            LOGGER.info("Storage created");
        }
    }

    @Override
    public void save(MultipartFile file) {
        if (file == null || file.getSize() == 0) {
            throw throwEx(BAD_REQUEST, "The file(s) can't be saved because it is null value!");
        }

        if (isNotAllowedExtension(file)) {
            throw throwEx(UNSUPPORTED_MEDIA_TYPE,
                format("The file \"%s\" has an illegal extension!", file.getOriginalFilename()));
        }

        try {
            Files.copy(file.getInputStream(), resolveFilePath(file.getOriginalFilename()));
            LOGGER.info("The file \"{}\" was saved to storage", file.getOriginalFilename());

        } catch (FileAlreadyExistsException e) {
            throw throwEx(BAD_REQUEST,
                format("The file with name \"%s\" already exist in storage!", file.getOriginalFilename()), e);

        } catch (Exception e) {
            throw throwEx(INTERNAL_SERVER_ERROR,
                    format("The file \"%s\" cannot be saved to the repository!", file.getOriginalFilename()), e);
        }
    }

    @Override
    public void saveAll(MultipartFile[] files) {
        if (files == null) {
            throw throwEx(BAD_REQUEST, "Files is null value!");
        }

        Stream.of(files).forEach(this::save);
    }

    @Override
    public List<FileInfo> getAllFilesInfo() {
        try (Stream<Path> paths = Files.walk(storagePath)) {
            return paths.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .map(this::convertToFileInfo)
                    .filter(Objects::nonNull)
                    .collect(toList());

        } catch (Exception e) {
            throw throwEx(INTERNAL_SERVER_ERROR,
                    "Something went wrong while getting a list of all files in the repository!", e);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        Path deletedFile = storagePath.resolve(fileName).normalize();
        try {
            Files.delete(deletedFile);
        } catch (NoSuchFileException e) {
            throw throwEx(NOT_FOUND, format("The file with the name \"%s\" was not found in the " +
                            "storage and could not be deleted!", fileName), e);
        } catch (Exception e) {
            throw throwEx(INTERNAL_SERVER_ERROR,
                    format("Something went wrong while deleting a file \"%s\"!", fileName), e);
        }
    }

    @Override
    public void clearAllStorage() {
        if (!clearDir(storagePath.toFile())) {
            throw throwEx(INTERNAL_SERVER_ERROR, STORAGE_CLEAN_ERROR);
        }
        if (!Files.isDirectory(storagePath)) {
            try {
                Files.createDirectory(storagePath);
                LOGGER.info("Storage created");
            } catch (Exception e) {
                throw throwEx(INTERNAL_SERVER_ERROR, STORAGE_CLEAN_ERROR, e);
            }
        }
    }

    @Override
    public UnloadedFile loadFileFromStorage(String fileName) {
        try {
            Path pathToFile = storagePath.resolve(fileName);
            File file = new File(pathToFile.toUri());

            String contentType = Files.probeContentType(pathToFile);
            InputStream inputStream = new FileInputStream(file);

            LOGGER.info("Unloaded file \"{}\" with content length = {} and content type = \"{}\"",
                fileName, file.length(), contentType);
            return new UnloadedFile(inputStream, contentType, file.length());

        } catch (FileNotFoundException e) {
            throw throwEx(NOT_FOUND, format("The file with the name \"%s\" was not found in the " +
                        "repository and could not be unloaded!", fileName));

        } catch (Exception e) {
            throw throwEx(INTERNAL_SERVER_ERROR,
                format("Something went wrong while trying load file \"%s\" from storage!", fileName), e);
        }
    }

    private boolean isNotAllowedExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw throwEx(UNSUPPORTED_MEDIA_TYPE, "The file can't be saved because is hasn't file name!");
        }

        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        Optional<String> allowedExtension = allowedExtensions.stream()
                .filter(e -> e.equalsIgnoreCase(fileExtension))
                .findAny();

        return !allowedExtension.isPresent();
    }

    private FileInfo convertToFileInfo(File file) {
        BasicFileAttributes attributes;
        try {
            attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            return new FileInfo(file.getName(), convertSize(attributes.size()),
                    convertTime(attributes.creationTime().toString()));
        } catch (IOException e) {
            LOGGER.warn("Failed to get file information with path \"{}\"", file.getAbsolutePath());
            return null;
        }
    }

    private String convertSize(Long size) {
        return format("%.2f KB", (size / 1024.0));
    }

    private String convertTime(String time) {
        LocalDateTime localDateTime = LocalDateTime.parse(time.substring(0, time.length() - 1));
        return FORMATTER.format(localDateTime);
    }

    private Path resolveFilePath(String fileName) {
        return Paths.get(storagePath.toString(), StringUtils.cleanPath(fileName));
    }

    private boolean clearDir(File dir) {
        return FileSystemUtils.deleteRecursively(dir);
    }
}
