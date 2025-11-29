package com.myproj.ruqaplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeResponseDto {

    private String id;

    private String targetId;

    private String targetType;

    private Boolean isLike;

    private LocalDateTime createdAt;



}
