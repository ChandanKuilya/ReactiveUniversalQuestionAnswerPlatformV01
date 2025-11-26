package com.myproj.ruqaplatform.controllers;

import com.myproj.ruqaplatform.dto.QuestionRequestDto;
import com.myproj.ruqaplatform.dto.QuestionResponseDto;
import com.myproj.ruqaplatform.services.IQuestionService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final IQuestionService questionService;

    public QuestionController(IQuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping()
    public Mono<QuestionResponseDto> createQuestion(@RequestBody QuestionRequestDto questionRequestDto) {
       return questionService.createQuestion(questionRequestDto)
               .doOnSuccess(response -> System.out.println("Question created successfully: " + response))
               .doOnError(error -> System.out.println("Error while creating question: " + error));
    }

    @GetMapping("/{id}")
    public Mono<QuestionResponseDto> getQuestionById(@PathVariable String id) {
        return questionService.getQuestionById(id)
                .doOnError(error -> System.out.println("Error fetching question: " + error))
                .doOnSuccess(response -> System.out.println("Question fetched successfully: " + response));
    }

    @GetMapping("/search")
    public Flux<QuestionResponseDto> searchQuestions(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return questionService.searchQuestions(query, page, size);
    }

    @GetMapping()
    public Flux<QuestionResponseDto> getAllQuestions(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size
    ) {
        return questionService.getAllQuestions(cursor, size)
                .doOnError(error -> System.out.println("Error fetching questions: " + error))
                .doOnComplete(() -> System.out.println("Questions fetched successfully"));
    }

}
