package com.example.personal_blog.service;

import com.example.personal_blog.entity.ContentPath;
import com.example.personal_blog.model.ArticleDto;
import com.example.personal_blog.repository.ContentPathRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ContentPathService {

    private final ContentPathRepository contentPathRepository;

    // Save image in a local directory
    public String saveImageToStorage(MultipartFile imageFile, ArticleDto articleDto) throws IOException {
        String uniqueFileName = LocalDateTime.now().toString().replaceAll(":", "-") + "_" + imageFile.getOriginalFilename();
        String packagePath = "src/main/resources/static/images";
        var article = articleDto.to(articleDto);

        Path uploadPath = Path.of(packagePath);
        Path filePath = uploadPath.resolve(uniqueFileName);
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            contentPathRepository.save(ContentPath.builder()
                .contentPath(uniqueFileName)
                .createdAt(LocalDateTime.now())
                .article(article)
                .build());
            System.out.println("File saved successfully: " + filePath.toString());
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
            throw e;
        }

        return uniqueFileName;
    }

    // To view an image
    public byte[] getImage(String imageDirectory, String imageName) throws IOException {
        Path imagePath = Path.of(imageDirectory, imageName);

        if (Files.exists(imagePath)) {
            byte[] imageBytes = Files.readAllBytes(imagePath);
            return imageBytes;
        } else {
            return null; // Handle missing images
        }
    }

    // Delete an image
    public String deleteImage(String imageDirectory, String imageName) throws IOException {
        Path imagePath = Path.of(imageDirectory, imageName);

        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
            return "Success";
        } else {
            return "Failed"; // Handle missing images
        }
    }
}

