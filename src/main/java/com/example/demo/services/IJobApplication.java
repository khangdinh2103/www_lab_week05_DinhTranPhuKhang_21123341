package com.example.demo.services;

import com.example.demo.models.Job;
import com.example.demo.models.JobApplication;

import java.util.List;

public interface IJobApplication {
    List<JobApplication> getApplicationsByJobId(Long jobId);

    List<JobApplication> getApplicationsByCandidateId(Long candidateId);

    List<Object[]> getJobApplicationsStats();

    Job getMostPopularJob();


    List<Object[]> getApplicantsPerJobStats();
}
