package com.example.demo.services.impl;

import com.example.demo.models.Candidate;
import com.example.demo.models.Job;
import com.example.demo.models.Skill;
import com.example.demo.repositories.CandidateRepository;
import com.example.demo.repositories.JobRepository;
import com.example.demo.services.IJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobImpl implements IJob {
    @Autowired
    private JobRepository jobRepository;

    @Override
    public List<Job> getJobByCompanyId(Long id) {
        return jobRepository.getJobsByCompanyId(id);
    }

    @Override
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @Override
    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    @Override
    public Job saveJob(Job job) {
        return jobRepository.save(job);
    }

    @Autowired
    private CandidateRepository candidateRepository;

    @Override
    public List<Job> getJobsMatchingSkills(Long candidateId) {
        Candidate candidate = candidateRepository.findByIdWithSkills(candidateId);
        if (candidate == null) {
            return List.of();  // Không tìm thấy ứng viên
        }

        // Lấy danh sách các skillId của ứng viên
        List<Long> skillIds = candidate.getCandidateSkills().stream()
                .map(cs -> cs.getSkill().getId())
                .collect(Collectors.toList());

        return jobRepository.findJobsBySkillIds(skillIds);
    }

    @Override
    public List<Job> searchJobs(String location, Integer experience, List<Long> skillIds, Integer skillLevel) {
        return jobRepository.searchJobs(location, experience, skillIds, skillLevel);
    }

    @Override
    public List<Object[]> getApplicantsPerJobStats() {
        return jobRepository.getApplicantsPerJobStats();
    }

    @Override
    public List<Object[]> getJobsByMonthStats() {
        return jobRepository.getJobsByMonthStats();
    }

}
