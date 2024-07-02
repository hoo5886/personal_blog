CREATE TABLE IF NOT EXISTS article (
                         article_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         content MEDIUMTEXT NOT NULL,
                        hits INT DEFAULT 0,
                        isDeleted BOOLEAN DEFAULT FALSE,
                        likes INT DEFAULT 0,
                        user_id BIGINT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user(user_id)

);

CREATE TABLE IF NOT EXISTS content_path (
                              content_path_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              content_path VARCHAR(255),
                              article_id BIGINT NOT NULL,
                              CONSTRAINT fk_article FOREIGN KEY (article_id) REFERENCES article(article_id)
);

CREATE TABLE IF NOT EXISTS user (
                         user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         login_id VARCHAR(255) NOT NULL,
                         username VARCHAR(255) NOT NULL,
                         password VARCHAR(255) NOT NULL,
                         enabled BOOLEAN DEFAULT TRUE,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS authority (
                        authority_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        role VARCHAR(255) NOT NULL,
                        user_id BIGINT NOT NULL,
                        CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user(user_id)
);