package com.myproj.ruqaplatform.repositories;

import com.myproj.ruqaplatform.models.Answer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface IAnswerRepository extends ReactiveMongoRepository<Answer,String> {

}
