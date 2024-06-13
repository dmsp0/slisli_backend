package com.quest_exfo.backend.service.booth;

import com.quest_exfo.backend.dto.request.BoothDTO;
import com.quest_exfo.backend.entity.Booth;
import com.quest_exfo.backend.repository.BoothRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
public class BoothServiceImpl implements BoothService {

  @Autowired
  private BoothRepository boothRepository;

  @Autowired
  private S3Client s3Client;

  @Value("${cloud.aws.s3.bucket-name}")
  private String bucketName;

  @Override
  public Booth createBooth(BoothDTO boothDTO, MultipartFile file) throws IOException {
    Booth booth = new Booth();
    booth.setTitle(boothDTO.getTitle());
    booth.setInfo(boothDTO.getInfo());
    booth.setCategory(boothDTO.getCategory());
    booth.setDate(boothDTO.getDate());
    booth.setStartTime(boothDTO.getStartTime());
    booth.setEndTime(boothDTO.getEndTime());
    booth.setMaxPeople(boothDTO.getMaxPeople());
    booth.setOpenerName(boothDTO.getOpenerName());
    booth.setType(boothDTO.getType());

    // UUID + 파일명으로 파일명 중복 처리
    String originalFileName = file.getOriginalFilename();
    String uniqueFileName = "booth_img/" + UUID.randomUUID().toString() + "_" + originalFileName;

    // 파일을 S3에 업로드
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(uniqueFileName)
        .build();

    PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

    if (putObjectResponse.sdkHttpResponse().isSuccessful()) {
      String fileUrl = s3Client.utilities().getUrl(b -> b.bucket(bucketName).key(uniqueFileName)).toExternalForm();
      booth.setImgPath(fileUrl);
    } else {
      throw new IOException("Could not upload file to S3");
    }

    // 비디오룸 ID생성
    int videoRoomId = generateUniqueVideoRoomId(boothDTO.getDate());
    booth.setVideoRoomId(videoRoomId);

    return boothRepository.save(booth);
  }

  private int generateUniqueVideoRoomId(LocalDate date) {
    // 주어진 날짜에 이미 사용된 비디오룸 ID 목록을 조회.
    List<Integer> usedIds = boothRepository.findVideoRoomIdsByDate(date);

    // 랜덤 숫자를 생성하기 위한 Random 인스턴스를 생성.
    Random random = new Random();

    int videoRoomId;

    // 조건: 30000~ 39999까지 랜덤으로 가진다. 같은날에는 같은 비디오룸 아이디를 가질수없다.
    do {
      videoRoomId = 30000 + random.nextInt(10000); // 30000 ~ 39999 범위의 숫자를 생성.
    } while (usedIds.contains(videoRoomId)); // 생성된 ID가 이미 사용된 경우 반복.

    return videoRoomId; // 고유한 비디오룸 ID를 반환.
  }
}