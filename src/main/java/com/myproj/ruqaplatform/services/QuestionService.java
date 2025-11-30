package com.myproj.ruqaplatform.services;

import com.myproj.ruqaplatform.adapter.QuestionAdapter;
import com.myproj.ruqaplatform.dto.QuestionRequestDto;
import com.myproj.ruqaplatform.dto.QuestionResponseDto;
import com.myproj.ruqaplatform.events.ViewCountEvent;
import com.myproj.ruqaplatform.models.Question;
import com.myproj.ruqaplatform.producers.KafkaEventProducer;
import com.myproj.ruqaplatform.repositories.IQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QuestionService implements IQuestionService{

    private final IQuestionRepository IQuestionRepository;     // constructor based field injection
//    public QuestionService(IQuestionRepository IQuestionRepository) {
//        this.IQuestionRepository = IQuestionRepository;       // you can also just annotate this QuestionService Class with
//                                                       // @RequiredArgsConstructor and omit this constructor declaration
//    }

    private final KafkaEventProducer kafkaEventProducer;



    @Override
    public Mono<QuestionResponseDto> createQuestion(QuestionRequestDto questionRequestDto){

        Question question = Question.builder()
                .title(questionRequestDto.getTitle())
                .content(questionRequestDto.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return IQuestionRepository.save(question)
                .map(QuestionAdapter::toQuestionResponseDto)
                .doOnSuccess(response -> System.out.println("Question created successfully: " + response))
                .doOnError(error-> System.out.println("Error while creating question: " + error));


    }

    @Override
    public Mono<QuestionResponseDto> getQuestionById(String id) {
        return IQuestionRepository.findById(id)
                .map(QuestionAdapter::toQuestionResponseDto)
                .doOnError(error -> System.out.println("Error fetching question: " + error))
                .doOnSuccess(response -> {
                    System.out.println("Question fetched successfully: " + response);
                    ViewCountEvent viewCountEvent = new ViewCountEvent(id, "question", LocalDateTime.now());
                    kafkaEventProducer.publishViewCountEvent(viewCountEvent);
                });
    }

    @Override
    public Flux<QuestionResponseDto> searchQuestions(String searchTerm, int offset, int page) {
        return IQuestionRepository.findByTitleOrContentContainingIgnoreCase(searchTerm, PageRequest.of(offset, page))
                .map(QuestionAdapter::toQuestionResponseDto)
                .doOnError(error -> System.out.println("Error searching questions: " + error))
                .doOnComplete(() -> System.out.println("Questions searched successfully"));
    }

    @Override
    public Flux<QuestionResponseDto> getAllQuestions(String cursor, int size) {
        Pageable pageable = PageRequest.of(0, size);

        if(!com.myproj.ruqaplatform.utils.CursorUtils.isValidCursor(cursor)) {
            return IQuestionRepository.findTop10ByOrderByCreatedAtAsc()
                    .take(size)
                    .map(QuestionAdapter::toQuestionResponseDto)
                    .doOnError(error -> System.out.println("Error fetching questions: " + error))
                    .doOnComplete(() -> System.out.println("Questions fetched successfully"));
        } else {
            LocalDateTime cursorTimeStamp = com.myproj.ruqaplatform.utils.CursorUtils.parseCursor(cursor);
            return IQuestionRepository.findByCreatedAtGreaterThanOrderByCreatedAtAsc(cursorTimeStamp, pageable)
                    .map(QuestionAdapter::toQuestionResponseDto)
                    .doOnError(error -> System.out.println("Error fetching questions: " + error))
                    .doOnComplete(() -> System.out.println("Questions fetched successfully"));
        }

    }

}
