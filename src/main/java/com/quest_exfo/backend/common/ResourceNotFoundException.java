package com.quest_exfo.backend.common;

import com.quest_exfo.backend.entity.Member;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ResourceNotFoundException(String message) {
    super(message);
  }

    public ResourceNotFoundException(Member member, String memberEmail, String email) {
    }

  public ResourceNotFoundException(String member, String memberEmail, String email) {
  }
}