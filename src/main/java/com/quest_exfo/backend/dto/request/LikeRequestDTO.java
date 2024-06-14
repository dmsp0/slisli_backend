package com.quest_exfo.backend.dto.request;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequestDTO {
    private Long boothId;
    private Long member_id;
}