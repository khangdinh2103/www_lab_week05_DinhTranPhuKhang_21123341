package com.example.demo.services;

import com.example.demo.models.Candidate;
import com.example.demo.models.JobSkill;
import com.example.demo.models.Skill;

import java.util.List;

public interface IJobSkill {
    void saveJobSkills(List<JobSkill> jobSkills);
    public List<Candidate> findCandidatesBySkills(List<Skill> skills);
    public List<Skill> getSkillsByJobId(Long jobId);

    List<Object[]> getJobsPerSkillStats();
}
