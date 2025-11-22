package com.myproj.ruqaplatform.adapter;

import com.myproj.ruqaplatform.dto.QuestionRequestDto;
import com.myproj.ruqaplatform.dto.QuestionResponseDto;
import com.myproj.ruqaplatform.models.Question;

import java.time.LocalDateTime;

public class QuestionAdapter {

    public static QuestionResponseDto toQuestionResponseDto(Question question){
        return QuestionResponseDto.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .createdAt(question.getCreatedAt())
                .build();
    }
}
