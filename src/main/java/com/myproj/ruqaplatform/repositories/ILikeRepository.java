package com.myproj.ruqaplatform.repositories;

import com.myproj.ruqaplatform.models.Like;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ILikeRepository extends ReactiveMongoRepository<Like,String> {

}
