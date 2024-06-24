package com.quest_exfo.backend.entity;


import com.quest_exfo.backend.common.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tbl_member")
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long member_id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private String profileImgPath;

    @Column(nullable = true)
    private String provider;

    @Column(nullable = true)
    private String providerId;
    @Builder
    public Member(String email, String password, String name, String provider, String providerId, Role role){
        this.email=email;
        this.password=password;
        this.name=name;
        this.role=role;
        this.provider = provider;
        this.providerId = providerId;
    }

    public void update(String encodedPassword, String name){
        this.password=encodedPassword;
        this.name=name;
    }

}