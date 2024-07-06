package com.example.personal_blog.repository;

import com.example.personal_blog.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.loginId = :loginId")
    Optional<User> findByLoginId(@Param("loginId") String loginId);
}
