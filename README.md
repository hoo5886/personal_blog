
# 개인 블로그 프로젝트

실제 사용할 개인 블로그를 만들어 보는 프로젝트입니다.

## 1. 개발환경

### 서버 및 데이터베이스
- OpenJDK 17
- MySQL 8.0.26

### 주요 프레임워크 및 라이브러리
- Spring Boot 3.3.0
- Spring Security
- Spring Data JPA
- Spring REST docs
- JUnit 5
- JsonWebToken: jjwt
- Lombok

### IDE 및 유틸리티
- IntelliJ Ultra IDEA
- Claude 3.5 sonnet
- ChatGPT 4o mini
- GitHub copilot

## 2. 데이터베이스 모델
<img src=src\main\resources\personal_blog_db_model.svg>

## 3. 주요 기능

### 회원가입
- 로그인 아이디, 비밀번호, 닉네임을 입력하여 회원가입을 할 수 있습니다.
- 회원가입 시, 비밀번호는 암호화하여 저장합니다.
- 회원가입 시, 회원의 권한은 USER로 설정합니다.
- 회원가입 시, 회원의 상태는 활성화 상태로 설정합니다.
- 회원가입 시, 회원의 생성일과 수정일을 저장합니다.

### JWT 토큰을 이용한 로그인 & 로그아웃
- 아이디와 비밀번호를 입력하여 로그인을 할 수 있습니다.
- 로그인 성공 시, JWT 토큰을 발급합니다.
- JWT 토큰은 60분 동안 유효하며 로그인한 회원의 권한 정보를 포함합니다.
- 로그인한 후, 매 요청마다 JWT 토큰을 헤더에 포함하여 요청합니다. 요청의 토큰에 signature 파트가 Project에 위치한 설정파일의 키와 비교한 뒤 일치하면 요청을 처리합니다. 일치하지 않을 경우 403 Forbidden을 반환합니다.
- 로그인한 후, 로그아웃을 할 수 있습니다. 로그아웃을 할 경우 사용한 JWT 토큰을 메모리 내에 블랙리스트로 저장하고 저장된 토큰은 유효하지 않은 토큰으로 처리합니다.

### 게시글 및 이미지 처리
- 로그인한 회원은 게시글을 작성할 수 있습니다.
- 게시글을 작성할 때, 제목과 내용을 입력해야 합니다.
- 게시글을 작성할 때, 이미지를 여러 개 포함할 수 있습니다. 이미지를 포함하여 게시글을 작성할 시, 저장 엔드포인트를 호출할 때 이미지파일을 project 내부에 저장하고, 이 사진의 경로를 DB에 저장합니다.
- 게시글을 조회할 때 게시글의 제목, 내용은 `article` 테이블을 통해 가져오며, 이미지는 `content_path` 테이블을 통해 경로를 가져와 해당 경로에 있는 이미지를 byte 형식으로 전송합니다.
- 게시글을 삭제하면 삭제 상태로 변경합니다. 데이터를 지우지 않습니다.
- 게시글은 `좋아요`기능이 있습니다. `좋아요`를 누르면 좋아요 수가 증가하며, 다시 누르면 좋아요 수가 감소합니다.

### 댓글 기능
- 게시글에 댓글을 작성할 수 있습니다.
- 댓글은 `comment` 테이블을 통해 관리합니다.
- 댓글을 작성할 때, 댓글 내용을 입력해야 합니다.
- 댓글을 작성할 때는 이미지를 사용할 수 없습니다.

### 알림 기능
- 게시글에 좋아요 또는 댓글이 작성되는 경우, 해당 게시글 작성자에게 알림 메시지를 보냅니다.
- 알림은 `notification` 테이블을 통해 관리합니다. 알림이 생성되는 경우, 알림을 받을 회원의 ID와 알림 내용을 저장합니다.
- 알림 기능은 스프링 프레임워크의 웹소켓 STOMP로 이벤트 드리븐 아키텍처를 통해 구현했습니다.
- 사용자가 로그인에 성공한 경우 자동으로 웹소켓 연결을 맺습니다.

## 4. API 명세
[문서참조](src/docs/asciidoc/index.adoc)

## 5. 트러블슈팅 및 해결방법
[문서참조](src/docs/asciidoc/troubleShooting.adoc)

## 6. 프로젝트 구조
```
src
├── main
│   ├── java
│   │   └── com
│   │       └── blog
│   │           ├── BlogApplication.java
│   │           ├── config
│   │           │   ├── JacksonConfig.java
│   │           │   ├── SecurityConfig.java
│   │           │   └── WebSocketConfig.java
│   │           ├── controller
│   │           │   ├── ArticleController.java
│   │           │   ├── CommentController.java
│   │           │   ├── LoginController.java
│   │           │   └── NotificationController.java
│   │           ├── dto
│   │           │   ├── request
│   │           │   │   ├── SignInRequest.java
│   │           │   │   └── SignUpRequest.java
│   │           │   ├── response
│   │           │   │   └── JwtAuthenticationResponse.java
│   │           │   ├── ArticleDto.java
│   │           │   ├── CommentDto.java
│   │           │   ├── NotificationDto.java
│   │           │   └── UserDto.java
│   │           ├── entity
│   │           │   ├── Article.java
│   │           │   ├── Authority.java
│   │           │   ├── BaseEntity.java
│   │           │   ├── Comment.java
│   │           │   ├── ContentPath.java
│   │           │   ├── Notification.java
│   │           │   ├── Role.java
│   │           │   └── User.java
│   │           ├── event
│   │           │   ├── Listener
│   │           │   │   ├── ArticleEventListener.java
│   │           │   │   └── CommentEventListener.java
│   │           │   ├── websocket
│   │           │   │   └── WebSocketHandler.java
│   │           │   ├── ArticleLikedEvent.java
│   │           │   └── CommentAddedEvent.java
│   │           ├── exception
│   │           │   └── GlobalExceptionHandler.java
│   │           ├── repository
│   │           │   ├── ArticleRepository.java
│   │           │   ├── AuthorityRepository.java
│   │           │   ├── CommentRepository.java
│   │           │   ├── ContentPathRepository.java
│   │           │   ├── NotificationRepository.java
│   │           │   └── UserRepository.java
│   │           ├── security
│   │           │   ├── CustomDaoAuthenticationProvider.java
│   │           │   └── JwtAuthenticationFilter.java
│   │           └── service
│   │               ├── ArticleService.java
│   │               ├── AuthenticationService.java
│   │               ├── CommentService.java
│   │               ├── ContentPathService.java
│   │               ├── JpaUserDetailsService.java
│   │               ├── JwtBlacklistService.java
│   │               ├── NotificationService.java
│   │               └── JwtService.java
│   └── resources
│       ├── application.yml
│       ├── personal_blog_db_model.svg
│       ├── static
│       │   └── images
│       └── templates
└── test
    └── java
        └── com
            └── blog
                ├── controller
                │   ├── ArticleControllerTest.java
                │   ├── CommentControllerTest.java
                │   └── LoginControllerTest.java
                ├── service
                │   ├── ArticleServiceTest.java
                │   ├── ContentPathServiceTest.java
                │   ├── JwtServiceTest.java
                │   └── NotificationServiceTest.java
                ├── testconfig
                │   ├── filter
                │   │   └── MockJwtAuthenticationFIlter.java
                │   └── TestSecurityConfig.java
                ├── websocket
                │   └── WebSocketIntegrationTest.java
                └── BlogApplicationTests.java
```