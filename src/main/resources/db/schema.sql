CREATE TABLE IF NOT EXISTS article (
                         article_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         content TEXT NOT NULL,
                        hits INT DEFAULT 0,
                        isDeleted BOOLEAN DEFAULT FALSE,
                        likes INT DEFAULT 0,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

);

CREATE TABLE IF NOT EXISTS content_path (
                              content_path_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              content_path VARCHAR(255),
                              article_id BIGINT NOT NULL,
                              CONSTRAINT fk_article FOREIGN KEY (article_id) REFERENCES article(article_id)
);