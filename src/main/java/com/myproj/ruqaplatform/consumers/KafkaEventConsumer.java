package com.myproj.ruqaplatform.consumers;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.myproj.ruqaplatform.config.KafkaConfig;
import com.myproj.ruqaplatform.events.ViewCountEvent;
import com.myproj.ruqaplatform.repositories.IQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventConsumer {

    private final IQuestionRepository questionRepository;

    @KafkaListener(
            topics = KafkaConfig.TOPIC_NAME,
            groupId = "view-count-consumer",
            containerFactory = "kafkaListenerContainerFactory"

    )
    public void handleViewCount(ViewCountEvent viewCountEvent) {

          questionRepository.findById(viewCountEvent.getTargetId())
                  .flatMap(question->{
                      System.out.println("Incrementing viewcount for question: " + question.getId());
                      Integer views= question.getViews();
                      question.setViews(views==null?0:views + 1);
                      return questionRepository.save(question);
                  })
                  .subscribe(updatedQuestion->{
                      System.out.println("Viewcount incremented for question" + updatedQuestion.getId());
                  }, error -> {
                      System.out.println("Error in incrementing viewcount for question" + error.getMessage());
                  });
    }

}
