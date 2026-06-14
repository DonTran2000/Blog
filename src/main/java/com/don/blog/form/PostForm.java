package com.don.blog.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostForm {
    private String title;
    private String content;
    private String excerpt;
    private String category;
}
