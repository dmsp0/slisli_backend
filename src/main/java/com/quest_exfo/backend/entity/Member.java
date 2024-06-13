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

    @Column(nullable = true)
    private String provider;

    @Column(nullable = true)
    private String providerId;

    @Column
    private String profileImage;
    @Builder
    public Member(String email, String password, String name, Role role, String provider, String providerId, String profileImage) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.profileImage = profileImage;
    }

    public void update(String password, String name, String profileImage) {
        this.password = password;
        this.name = name;
        this.profileImage = profileImage;
    }
}
