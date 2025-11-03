package com.sa.M_Mart.controller;
import com.sa.M_Mart.service.ImageUploadService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageUploadService imageUploadService;

    @RolesAllowed({"ROLE_SELLER", "ROLE_ADMIN"})
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "products") String folder,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "useWatermark", defaultValue = "false") boolean useWatermark
    ) throws IOException {

        String url = imageUploadService.uploadImage(file, folder, productName, useWatermark);
        return ResponseEntity.ok(Map.of("url", url));
    }

}