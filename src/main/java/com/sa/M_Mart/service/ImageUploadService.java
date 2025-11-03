package com.sa.M_Mart.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, String folder, String productName, boolean useWatermark) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String safeProductName = productName != null ? productName.replaceAll("[^a-zA-Z0-9-_ ]", "") : "N/A";

        Map<String, Object> options = ObjectUtils.asMap(
                "folder", "m_mart_uploads/" + folder,
                "context", "product_name=" + safeProductName
        );

        if (useWatermark) {
            options.put("transformation", new com.cloudinary.Transformation()
                    .overlay("m_mart_logo_yprkdk")  // logo must exist in Cloudinary
                    .gravity("south_east")
                    .x(10)
                    .y(10)
                    .opacity(50)
                    .width(100)
                    .crop("scale")
            );
        }

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        return uploadResult.get("secure_url").toString();
    }
}
