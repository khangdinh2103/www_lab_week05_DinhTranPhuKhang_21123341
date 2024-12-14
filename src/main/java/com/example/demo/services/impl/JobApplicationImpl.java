package com.example.demo.services.impl;

import com.example.demo.models.Job;
import com.example.demo.models.JobApplication;
import com.example.demo.repositories.JobApplicationRepository;
import com.example.demo.services.IJobApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobApplicationImpl implements IJobApplication {
    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Override
    public List<JobApplication> getApplicationsByJobId(Long jobId) {
        return jobApplicationRepository.findByJobId(jobId);
    }

    @Override
    public List<JobApplication> getApplicationsByCandidateId(Long candidateId) {
        return jobApplicationRepository.findByCandidateId(candidateId);
    }



    @Override
    public Job getMostPopularJob() {
        return jobApplicationRepository.findMostPopularJob();
    }


    @Override
    public List<Object[]> getJobApplicationsStats() {
        return jobApplicationRepository.getJobApplicationsStats();
    }


    @Override
    public List<Object[]> getApplicantsPerJobStats() {
        return jobApplicationRepository.getApplicantsPerJobStats();
    }

}
