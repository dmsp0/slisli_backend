package com.quest_exfo.backend.controller;

import com.quest_exfo.backend.entity.Booth;
import com.quest_exfo.backend.entity.Like;
import com.quest_exfo.backend.entity.Member;
import com.quest_exfo.backend.repository.LikeRepository;
import com.quest_exfo.backend.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sse")
public class SseController {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LikeRepository likeRepository;

    @GetMapping("/subscribe/{memberId}")
    public SseEmitter subscribe(@PathVariable Long memberId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // Find the member
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        if (!memberOptional.isPresent()) {
            emitter.completeWithError(new Exception("Member not found"));
            return emitter;
        }

        Member member = memberOptional.get();

        // Find liked booths
        List<Like> likes = likeRepository.findByMember(member);

        new Thread(() -> {
            try {
                while (true) {
                    LocalTime now = LocalTime.now();
                    for (Like like : likes) {
                        Booth booth = like.getBooth();
                        if (booth.getDate().equals(LocalDate.now())) {
                            long minutesUntilStart = Duration.between(now, booth.getStartTime()).toMinutes();
                            if (minutesUntilStart <= 60 && minutesUntilStart > 0) {
                                // Send JSON formatted data
                                String eventData = "{\"boothTitle\": \"" + booth.getTitle() + "\", \"minutesUntilStart\": " + minutesUntilStart + "}";

                                // Send JSON formatted data
                                emitter.send(SseEmitter.event().data(eventData));
                            }
                        }
                    }
                    Thread.sleep(60000); // 1분
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }
}
