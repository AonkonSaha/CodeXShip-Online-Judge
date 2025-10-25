package com.judge.myojudge.service.imp;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.judge.myojudge.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile("prod")
public class CloudinaryServiceImp implements CloudinaryService {
    private final Cloudinary cloudinary;
    @Override
    public Map uploadTestcase(MultipartFile file) throws IOException {
        String original = file.getOriginalFilename();
        String safeName;
        if(original != null && original.endsWith(".in")){
            safeName = original
                    .replace(".in", ".txt");
        }
        else{
            safeName = original != null ? original.replace(".out", ".txt") : null;
        }

        String uniqueFileName = UUID.randomUUID() + "_" + safeName;
        return cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "public_id", "testcases/" + uniqueFileName,
                        "resource_type", "raw"
                )
        );
    }

    @Override
    public List<String> readCloudinaryFile(String fileUrl) {
        List<String> lines = new ArrayList<>();
        if (fileUrl != null && !fileUrl.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(fileUrl).openStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to read file from Cloudinary", e);
            }
        }
        return lines;
    }

    @Override
    public String readCloudinaryFileForExecuting(String fileUrl) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new URL(fileUrl).openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading Cloudinary file: " + fileUrl, e);
        }
        return content.toString();
    }

    @Override
    public Map getFile(String publicUrl) throws Exception {
        return cloudinary.api().resource(publicUrl, ObjectUtils.emptyMap());

    }

    @Override
    public Map uploadImage(MultipartFile file) throws Exception {
        if(file==null || file.isEmpty()){
            throw new RuntimeException("Upload Image File is Empty or Null");
        }
        if(file.getSize()>1024*1024*10L){
            throw new RuntimeException("Upload Image File Size is too Large. Max Size is 10MB");
        }
        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        return cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "public_id", "images/" + uniqueFileName,
                        "resource_type", "image"
                )
        );
    }

    @Override
    public void deleteCloudinaryFile(String fileKey, String fileType) {
            try {
                cloudinary.uploader().destroy(fileKey, ObjectUtils.asMap("resource_type", fileType));
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file from Cloudinary with key: " + fileKey, e);
            }

    }
}
