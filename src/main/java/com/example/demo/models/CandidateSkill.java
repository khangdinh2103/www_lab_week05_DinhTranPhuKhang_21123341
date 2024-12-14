package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "candidate_skill")
public class CandidateSkill {

    @EmbeddedId
    private CandidateSkillId id;

    // Ánh xạ quan hệ với Candidate
    @MapsId("canId") // Liên kết với khóa chính trong CandidateSkillId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "can_id", nullable = false)
    private Candidate can;

    // Ánh xạ quan hệ với Skill, thêm insertable = false và updatable = false để tránh việc chèn hay cập nhật cột skill_id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false, insertable = false, updatable = false)
    private Skill skill;

    @Column(name = "more_infos", length = 1000)
    private String moreInfos;

    @Column(name = "skill_level", nullable = false)
    private Byte skillLevel;
}
