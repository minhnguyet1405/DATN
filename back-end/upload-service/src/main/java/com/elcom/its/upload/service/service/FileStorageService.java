package com.elcom.its.upload.service.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 *
 * @author anhdv
 */
public interface FileStorageService {

    Resource loadFileAsResource(String fileName);

    String storeFile(MultipartFile file);

    String replaceFileNameIfExists(String folderUpload,String fileName,String pathDir) throws IOException;
}
