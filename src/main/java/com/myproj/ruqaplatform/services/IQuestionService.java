package com.myproj.ruqaplatform.services;

import com.myproj.ruqaplatform.dto.QuestionRequestDto;
import com.myproj.ruqaplatform.dto.QuestionResponseDto;
import com.myproj.ruqaplatform.models.Question;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IQuestionService {

    public Mono<QuestionResponseDto> createQuestion(QuestionRequestDto questionRequestDto);
    public Mono<QuestionResponseDto> getQuestionById(String id);
    public Flux<QuestionResponseDto> searchQuestions(String searchTerm, int offset, int page);
    public Flux<QuestionResponseDto> getAllQuestions(String cursor, int size);

}
