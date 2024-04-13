/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.its.upload.service.controller;

import com.elcom.its.upload.service.config.UploadConfig;
import com.elcom.its.upload.service.constant.Constant;
import com.elcom.its.upload.service.model.dto.ResponseMessageDTO;
import com.elcom.its.upload.service.model.dto.UploadDTO;
import com.elcom.its.upload.service.service.impl.FileStorageServiceImpl;
import com.elcom.its.upload.service.upload.UploadFileResponse;
import com.elcom.its.upload.service.utils.DateUtil;
import com.elcom.its.upload.service.validation.UploadValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Admin
 */
@RestController
@RequestMapping("/v1.0")
public class UploadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private FileStorageServiceImpl fileStorageService;

    @Value("${url.upload}")
    private String urlUpload;

    @Value("${folder.upload}")
    private String folderUpload;

    /**
     * Upload file
     *
     * @param files
     * @param keepFileName
     * @param localUpload
     * @param headerMap
     * @param request
     * @return image upload link
     * @throws com.fasterxml.jackson.core.JsonProcessingException
     */
    @RequestMapping(value = "/upload/**", method = RequestMethod.POST)
    public ResponseEntity<Object> uploadFile(@RequestParam(value = "file", required = false) MultipartFile[] files,
                                             @RequestParam(value = "keepFileName", required = false) Boolean keepFileName,
                                             @RequestParam(value = "localUpload", required = false) Boolean localUpload,
                                             @RequestHeader Map<String, String> headerMap, HttpServletRequest request) throws Exception {
        String requestPath = request.getRequestURI();
        if (requestPath != null && requestPath.contains(Constant.API_ROOT_PATH)) {
            requestPath = requestPath.replace(Constant.API_ROOT_PATH, "/");
        }
        int index = requestPath.indexOf("/", "/upload/".length());
        String service = null;
        if (index != -1) {
            service = requestPath.substring("/upload/".length(), index);
        } else {
            service = requestPath.replace("/upload/", "");
        }
        LOGGER.info("requestPath: {}, service: {}", requestPath, service);
        UploadDTO dto = UploadConfig.UPLOAD_DEFINE_MAP.get(requestPath);
        new UploadValidation().validate(requestPath, service, files, dto);

        //Process upload
        String ddmmyyyy = DateUtil.today("ddMMyyyy");
        String uploadDir = dto.getFolder();

        List<UploadFileResponse> list = new ArrayList<>();
        UploadFileResponse uploadFileResponse = null;
        for (MultipartFile file : files) {
            String fileName = fileStorageService.storeFile(file, uploadDir, keepFileName);
            String fileDownloadUri = urlUpload + Constant.API_ROOT_PRIVATE_PATH + uploadDir.replace("{ddmmyyyy}", ddmmyyyy) + "/" + fileName;
            String fileDownloadUriMp4 = urlUpload + (Constant.API_ROOT_PRIVATE_PATH + uploadDir.replace("{ddmmyyyy}", ddmmyyyy) + "/" + fileName).replace(".mkv", ".mp4");
            LOGGER.info("Upload file url: " + fileDownloadUri);
            uploadFileResponse = new UploadFileResponse(fileName.replace("mkv","mp4"), fileDownloadUriMp4, file.getContentType(), file.getSize());
            list.add(uploadFileResponse);
        }
        return new ResponseEntity(new ResponseMessageDTO(list), HttpStatus.OK);

    }

    /**
     * View file upload
     *
     * @param request
     * @return file
     * @throws IOException
     */
    @RequestMapping(value = "/upload/**", method = RequestMethod.GET)
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<Resource> viewFile(HttpServletRequest request) throws IOException {
        String filePath = request.getRequestURI();
        String cacheable = request.getParameter("cache");
        LOGGER.info("view file: {}", filePath);
        if (filePath != null && filePath.contains("/v1.0/")) {
            filePath = filePath.replace("/v1.0/", "");
        }
        int lastIndex = filePath != null ? filePath.lastIndexOf("/") : -1;
        String fileName = lastIndex != -1 ? filePath.substring(lastIndex + 1) : filePath;
        fileName = URLDecoder.decode(fileName, "UTF-8");

        // Fallback to the default content type if type could not be determined
        ContentDisposition contentDisposition = ContentDisposition.builder("inline").filename(fileName).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);

        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(filePath);
        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            LOGGER.info("Không nhận dạng được kiểu file.ex: " + ex);
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        if ("false".equalsIgnoreCase(cacheable)) {
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noCache())
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(resource.contentLength())
                    .headers(headers)
                    .body(resource);
        }
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES))
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(resource.contentLength())
                .headers(headers)
                .body(resource);
    }

    private static String getCommandStr(String[] cmd) {
        String commandStr = "";
        for (String s : cmd) {
            commandStr += s + " ";
        }
        return commandStr;
    }

    public void convertMkvToMp4(String inputFilePath) throws Exception {
        String outputFilePath = inputFilePath.replace("mkv", "mp4");
        String[] cmd = new String[]{"C:\\ffmpeg\\bin\\ffmpeg", "-i", inputFilePath,  "-codec copy", outputFilePath};
        if (cmd != null && cmd.length > 0) {
            try {
                Process process = Runtime.getRuntime().exec(getCommandStr(cmd));
                BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = "";
                while ((line = buf.readLine()) != null) {
                    System.out.println("line" + line);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/upload/delete", method = RequestMethod.DELETE)
    public ResponseEntity deleteFile(@RequestBody Map<String, Object> bodyParam) throws Exception {
        List<String> fileUrls = (List<String>) bodyParam.get("fileUrls");
        for (String fileUrl : fileUrls) {
            try {
                String[] link = fileUrl.split("/");
                String filePath = folderUpload + "\\upload\\file\\" + link[link.length - 2] + "\\" + link[link.length - 1];
                Path path = Paths.get(filePath);
                Files.delete(path);
                return new ResponseEntity(new ResponseMessageDTO("Xóa file thành công"), HttpStatus.OK);
            } catch (IOException e) {
                return new ResponseEntity(new ResponseMessageDTO("Xóa file thất bại"), HttpStatus.FAILED_DEPENDENCY);
            }
        }
        return null;
    }
}
