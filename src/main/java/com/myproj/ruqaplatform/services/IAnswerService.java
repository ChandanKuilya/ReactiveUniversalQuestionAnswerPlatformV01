package com.myproj.ruqaplatform.services;

import com.myproj.ruqaplatform.dto.AnswerRequestDto;
import com.myproj.ruqaplatform.dto.AnswerResponseDto;
import com.myproj.ruqaplatform.models.Answer;
import reactor.core.publisher.Mono;

public interface IAnswerService {
    public Mono<AnswerResponseDto> createAnswer(AnswerRequestDto answerRequestDto);

    public Mono<AnswerResponseDto> getAnswerById(String id);

}
