package com.myproj.ruqaplatform.services;

import com.myproj.ruqaplatform.adapter.QuestionAdapter;
import com.myproj.ruqaplatform.dto.QuestionRequestDto;
import com.myproj.ruqaplatform.dto.QuestionResponseDto;
import com.myproj.ruqaplatform.models.Question;
import com.myproj.ruqaplatform.repositories.QuestionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class QuestionService implements IQuestionService{

    private final QuestionRepository questionRepository;     // constructor based field injection
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository=questionRepository;       // you can also just annotate this QuestionService Class with
                                                       // @RequiredArgsConstructor and omit this constructor declaration
    }
    @Override
    public Mono<QuestionResponseDto> createQuestion(QuestionRequestDto questionRequestDto){

        Question question = Question.builder()
                .title(questionRequestDto.getTitle())
                .content(questionRequestDto.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return questionRepository.save(question)
                .map(QuestionAdapter::toQuestionResponseDto)
                .doOnSuccess(response -> System.out.println("Question created successfully: " + response))
                .doOnError(error-> System.out.println("Error while creating question: " + error));


    }

    @Override
    public Mono<QuestionResponseDto> getQuestionById(String id) {
        return questionRepository.findById(id)
                .map(QuestionAdapter::toQuestionResponseDto)
                .doOnError(error -> System.out.println("Error fetching question: " + error))
                .doOnSuccess(response -> {
                    System.out.println("Question fetched successfully: " + response);
                });
    }

    @Override
    public Flux<QuestionResponseDto> searchQuestions(String searchTerm, int offset, int page) {
        return questionRepository.findByTitleOrContentContainingIgnoreCase(searchTerm, PageRequest.of(offset, page))
                .map(QuestionAdapter::toQuestionResponseDto)
                .doOnError(error -> System.out.println("Error searching questions: " + error))
                .doOnComplete(() -> System.out.println("Questions searched successfully"));
    }

}
