package com.quest_exfo.backend.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberDeleteDTO {
    private Long member_id;

    @Builder
    public MemberDeleteDTO(Long member_id){
        this.member_id=member_id;
    }
}