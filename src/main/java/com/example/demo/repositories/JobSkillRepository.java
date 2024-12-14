package com.example.demo.repositories;

import com.example.demo.models.JobSkill;
import com.example.demo.models.JobSkillId;
import com.example.demo.models.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSkillRepository extends JpaRepository<JobSkill, JobSkillId> {
    @Query("SELECT js.skill FROM JobSkill js WHERE js.job.id = :jobId")
    List<Skill> findSkillsByJobId(@Param("jobId") Long jobId);

    @Query("SELECT s.skillName, COUNT(js.job.id) " +
            "FROM JobSkill js " +
            "JOIN js.skill s " +
            "GROUP BY s.skillName")
    List<Object[]> getJobsPerSkillStats();

}