package com.example.demo.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class StoryDto {
    // Getters and Setters
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

}