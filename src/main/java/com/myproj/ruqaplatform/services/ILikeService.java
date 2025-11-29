package com.myproj.ruqaplatform.services;

import com.myproj.ruqaplatform.dto.LikeRequestDto;
import com.myproj.ruqaplatform.dto.LikeResponseDto;
import reactor.core.publisher.Mono;

public interface ILikeService {

    Mono<LikeResponseDto> createLike(LikeRequestDto likeRequestDto);
    Mono<LikeResponseDto> countLikesByTargetIdAndTargetType(String targetId, String targetType);

    Mono<LikeResponseDto> countDisLikesByTargetIdAndTargetType(String targetId, String targetType);

    Mono<LikeResponseDto> toggleLike(String targetId, String targetType);



}
