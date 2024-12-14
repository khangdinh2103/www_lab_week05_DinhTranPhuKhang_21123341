package com.example.demo.services;



import com.example.demo.models.Job;

import java.util.List;

public interface IJob {
    public List<Job> getJobByCompanyId(Long id);

    public List<Job> getAllJobs();
    public Job getJobById(Long id);
    public Job saveJob(Job job);
    public List<Job> getJobsMatchingSkills(Long candidateId);

    List<Job> searchJobs(String location, Integer experience, List<Long> skillIds, Integer skillLevel);

    List<Object[]> getJobsByMonthStats();

    List<Object[]> getApplicantsPerJobStats();



}
