package com.judge.myojudge.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CloudinaryService {
     Map uploadTestcase(MultipartFile file) throws IOException;
     List<String> readCloudinaryFile(String fileUrl);
     String readCloudinaryFileForExecuting(String fileUrl);
     Map getFile(String publicUrl) throws Exception;
     Map uploadImage(MultipartFile file) throws Exception;
     void deleteCloudinaryFile(String fileKey,String fileType);
}
