package com.quest_exfo.backend.dto.response;

import com.quest_exfo.backend.common.BoothCategory;
import lombok.*;

import java.time.LocalTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LikeResponseDTO {
    private Long likeId;
    private Long boothId;
    private Long member_id;
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private BoothCategory category;
    private String imgPath;
}