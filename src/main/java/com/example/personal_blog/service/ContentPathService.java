package com.example.personal_blog.service;

import com.example.personal_blog.entity.ContentPath;
import com.example.personal_blog.model.ArticleDto;
import com.example.personal_blog.model.ContentPathDto;
import com.example.personal_blog.repository.ContentPathRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ContentPathService {

    private final ContentPathRepository contentPathRepository;

    private final String packagePath = "src/main/resources/static/images";

    public String saveImages(MultipartFile imageFile, ArticleDto articleDto) throws IOException {
        String fileName = LocalDateTime.now().toString().replaceAll(":", "-") + "_" + imageFile.getOriginalFilename();

        var article = articleDto.to(articleDto);

        Path uploadPath = Path.of(packagePath);
        Path filePath = uploadPath.resolve(fileName);
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            contentPathRepository.save(ContentPath.builder()
                .contentPath(fileName)
                .createdAt(LocalDateTime.now())
                .article(article)
                .build());
            System.out.println("File saved successfully: " + filePath.toString());
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
            throw e;
        }

        return fileName;
    }

    public Set<ContentPathDto> getImagePaths(Long articleId) {
        Set<ContentPath> contentPaths = contentPathRepository.findByArticleId(articleId);

        if (contentPaths.isEmpty() || contentPaths == null) {
            return new HashSet<>();
        }

        return contentPaths.stream()
            .map(ContentPathDto::from)
            .collect(Collectors.toSet());
    }

//    // To view an image
//    public static Set<byte[]> getImages(Set<ContentPath> pathSet) throws IOException {
//        if (pathSet.isEmpty()) {
//            return null; // Handle missing images
//        }
//
//       Set<byte[]> imageByteSet = new HashSet<>();
//        for (ContentPath contentPath : pathSet) {
//            Path imagePath = Path.of("src/main/resources/static/images", contentPath.getContentPath());
//            imageByteSet.add(Files.readAllBytes(imagePath));
//        }
//
//        return imageByteSet;
//    }

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

