package com.quest_exfo.backend.service.booth;

import com.quest_exfo.backend.common.BoothCategory;
import com.quest_exfo.backend.dto.request.BoothDTO;
import com.quest_exfo.backend.entity.Booth;
import com.quest_exfo.backend.repository.BoothRepository;
import com.quest_exfo.backend.repository.LikeRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class BoothServiceImpl implements BoothService {

  @Autowired
  private BoothRepository boothRepository;

  @Autowired
  private LikeRepository likeRepository;

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
    booth.setMemberId(boothDTO.getMemberId());

    try {
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
    } catch (MultipartException e) {
      throw new IOException("File size exceeds the allowable limit", e);
    }

    // 비디오룸 ID생성
    int videoRoomId = generateUniqueVideoRoomId(boothDTO.getDate());
    booth.setVideoRoomId(videoRoomId);

    return boothRepository.save(booth);
  }

  @Override
  public Booth updateBooth(Long boothId, BoothDTO boothDTO, MultipartFile file) throws IOException {
    Booth booth = findBoothById(boothId);
    if (booth == null) {
      throw new IllegalArgumentException("부스아이디 : " + boothId + " 수정 실패");
    }

    booth.setTitle(boothDTO.getTitle());
    booth.setInfo(boothDTO.getInfo());
    booth.setCategory(boothDTO.getCategory());
    booth.setDate(boothDTO.getDate());
    booth.setStartTime(boothDTO.getStartTime());
    booth.setEndTime(boothDTO.getEndTime());
    booth.setMaxPeople(boothDTO.getMaxPeople());
    booth.setOpenerName(boothDTO.getOpenerName());
    booth.setType(boothDTO.getType());

    if (file != null && !file.isEmpty()) {
      // 기존 파일 삭제
      if (booth.getImgPath() != null) {
        String existingFileKey = booth.getImgPath().substring(booth.getImgPath().indexOf("booth_img/"));
        System.out.println("Existing file key: " + existingFileKey); // 디버그 출력
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(existingFileKey)
            .build();
        s3Client.deleteObject(deleteObjectRequest);
      }

      // 새로운 파일 업로드
      try {
        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = "booth_img/" + UUID.randomUUID().toString() + "_" + originalFileName;

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
      } catch (MultipartException e) {
        throw new IOException("File size exceeds the allowable limit", e);
      }
    }

    return boothRepository.save(booth);
  }


  @Override
  public Page<Booth> getBoothsByMemberAndCategory(Long memberId, String category, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    if (category.isEmpty()) {
      return boothRepository.findByMemberIdOrderByDateDesc(memberId, pageable);
    } else {
      return boothRepository.findByMemberIdAndCategoryOrderByDateDesc(memberId, BoothCategory.valueOf(category), pageable);
    }
  }

  private int generateUniqueVideoRoomId(LocalDate date) {
    List<Integer> usedIds = boothRepository.findVideoRoomIdsByDate(date);
    Random random = new Random();
    int videoRoomId;
    do {
      videoRoomId = 30000 + random.nextInt(10000);
    } while (usedIds.contains(videoRoomId));
    return videoRoomId;
  }

  @Override
  public Booth findBoothById(Long boothId) {
    return boothRepository.findById(boothId).orElse(null);
  }

  @Override
  public void deleteByBoothId(Long boothId) {
    Booth booth = findBoothById(boothId);
    if (booth != null) {
      boothRepository.delete(booth);
    } else {
      throw new IllegalArgumentException("부스아이디 : " + boothId + " 삭제 실패");
    }
  }

  @Override
  public Map<String, Booth> getTopLikedBoothsByCategoryAndDate(LocalDate date) {
    List<Object[]> results = likeRepository.findTopLikedBoothsByCategoryAndDate(date);

    // 디버깅 로그 추가
    for (Object[] result : results) {
      Booth booth = (Booth) result[0];
      Long likeCount = (Long) result[1];
      System.out.println("Category: " + booth.getCategory() + ", Booth: " + booth.getTitle() + ", Likes: " + likeCount);
    }

    Map<String, Booth> topLikedBoothsByCategory = results.stream()
        .collect(Collectors.groupingBy(
            result -> ((Booth) result[0]).getCategory().toString(),
            Collectors.collectingAndThen(Collectors.toList(), list -> (Booth) list.get(0)[0])
        ));

    System.out.println("Top liked booths by category: " + topLikedBoothsByCategory);
    return topLikedBoothsByCategory;
  }

  @Override
  public Map<String, Booth> getLatestBoothsByCategory() {
    Map<String, Booth> latestBooths = new HashMap<>();
    for (BoothCategory category : BoothCategory.values()) {
      boothRepository.findTopByCategoryOrderByBoothIdDesc(category).ifPresent(booth -> latestBooths.put(category.name(), booth));
    }
    return latestBooths;
  }

  @Override
  public long getTotalBoothsCount() {
    return boothRepository.count();
  }

  @Override
  public long getUniqueBoothMembersCount() {
    return boothRepository.findAll().stream().map(Booth::getMemberId).distinct().count();
  }
  }