package com.sorted.commons.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.Url;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret
    ) {
        Map config = new HashMap();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        config.put("secure", true);
        this.cloudinary = new Cloudinary(config);
    }

    /**
     * Uploads an image to Cloudinary and returns both the URL and the full response map.
     *
     * @param file MultipartFile to upload
     * @return Map with keys: "url" (String) and "response" (Map<String, Object>)
     * @throws IOException if upload fails
     */
//    public Map<String, Object> uploadImage(MultipartFile file) throws IOException {
//        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
//                "access_mode", "authenticated"
//        ));
//        Map<String, Object> result = new HashMap<>();
//        result.put("url", uploadResult.get("secure_url"));
//        result.put("response", uploadResult);
//        return result;
//    }
    public Map uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "access_mode", "authenticated"
        ));
        return uploadResult;
    }

    /**
     * Uploads an image to Cloudinary and returns only the URL.
     *
     * @param file MultipartFile to upload
     * @return The secure URL of the uploaded image
     * @throws IOException if upload fails
     */
    public String uploadImageAndGetUrl(MultipartFile file) throws IOException {

        Map<String, String> stringObjectMap = uploadImage(file);
        String publicId = stringObjectMap.get("public_id");

        Url url = cloudinary.url()
                .resourceType("image")
                .type("authenticated")
                .secure(true)
                .signed(true);

        String signedUrl = url.generate(publicId);
        System.out.println("Signed URL: " + signedUrl);

        return signedUrl;
    }
}