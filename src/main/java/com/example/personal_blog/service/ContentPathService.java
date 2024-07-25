package com.example.personal_blog.service;

import com.example.personal_blog.entity.Article;
import com.example.personal_blog.entity.ContentPath;
import com.example.personal_blog.dto.ContentPathDto;
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

    public String saveImages(MultipartFile[] files, Article article) throws IOException {
        String fileName = LocalDateTime.now().toString().replaceAll(":", "-") + "_image";

        Path uploadPath = Path.of(packagePath);
        Path filePath = uploadPath.resolve(fileName);
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            if (files != null) {
                for (MultipartFile file : files) {
                    if (file.isEmpty()) {
                        continue;
                    }
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    contentPathRepository.save(ContentPath.builder()
                        .contentPath(fileName)
                        .article(article)
                        .build()
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
            throw e;
        }

        return fileName;
    }

    /**
     * 업데이트 방식은 기존 이미지를 삭제하고 새로운 이미지를 추가하는 방식으로 진행
     * @param files
     * @param article
     * @throws IOException
     */
    public void updateImages(MultipartFile[] files, Article article) throws IOException {
        deleteAllContentPaths(article.getArticleId());
        saveImages(files, article);
    }

    /**
     * 이미지 삭제
     * @param articleId
     */
    private void deleteAllContentPaths(Long articleId) {
        var contentPaths = contentPathRepository.findByArticleId(articleId);

        //디렉토리에 저장된 이미지 삭제
        if (contentPaths.isPresent() || !contentPaths.isEmpty()) {
            for (ContentPath contentPath : contentPaths.get()) {
                try {
                    if (Files.exists(Path.of(packagePath, contentPath.getContentPath()))) {
                        Files.delete(Path.of(packagePath, contentPath.getContentPath()));
                    }
                } catch (IOException e) {
                    System.err.println("Error deleting file: " + e.getMessage());
                }
            }
        } else {
            System.err.println("No images to delete");
        }

        // DB에 저장된 이미지 삭제
        contentPathRepository.deleteAllByArticleId(articleId);
    }

    public Set<ContentPathDto> getImagePaths(Long articleId) {
        var contentPaths = contentPathRepository.findByArticleId(articleId);

        if (contentPaths.isEmpty() || contentPaths.isPresent()) {
            return new HashSet<>();
        }

        return contentPaths.get().stream()
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

