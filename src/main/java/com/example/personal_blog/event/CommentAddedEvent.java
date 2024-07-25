package com.example.personal_blog.event;

import com.example.personal_blog.entity.Comment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CommentAddedEvent {

    private final Comment comment;
}
