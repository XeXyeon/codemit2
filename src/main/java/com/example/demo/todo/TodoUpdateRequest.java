package com.example.demo.todo;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class TodoUpdateRequest {

    @Pattern(regexp = ".*\\S.*", message = "제목은 비어 있을 수 없습니다.")
    @Size(max = 30, message = "제목은 30자 이하여야 합니다.")
    private String title;
    private Boolean completed;

}
