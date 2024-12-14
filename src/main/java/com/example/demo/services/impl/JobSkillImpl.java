package com.example.demo.services.impl;

import com.example.demo.models.Candidate;
import com.example.demo.models.JobSkill;
import com.example.demo.models.Skill;
import com.example.demo.repositories.CandidateRepository;
import com.example.demo.repositories.JobSkillRepository;
import com.example.demo.repositories.SkillRepository;
import com.example.demo.services.IJobSkill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSkillImpl implements IJobSkill {

    @Autowired
    private JobSkillRepository jobSkillRepository;
    @Autowired
    private CandidateRepository candidateRepository;

    @Override
    public void saveJobSkills(List<JobSkill> jobSkills) {
        // Lưu tất cả các JobSkill vào cơ sở dữ liệu
        jobSkillRepository.saveAll(jobSkills);
    }

    @Override
    public List<Candidate> findCandidatesBySkills(List<Skill> skills) {
        return candidateRepository.findCandidatesBySkills(skills);
    }

    @Override
    public List<Skill> getSkillsByJobId(Long jobId) {
        return jobSkillRepository.findSkillsByJobId(jobId);
    }


    @Override
    public List<Object[]> getJobsPerSkillStats() {
        return jobSkillRepository.getJobsPerSkillStats();
    }



}
