package com.example.demo.todo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class TodoCreateRequest {

    @NotBlank(message = "제목을 작성해주세요.")
    @Size(max = 30, message = "제목은 30자 이하여야 합니다.")
    private String title;

}
