package com.example.demo.repositories;

import com.example.demo.models.Job;
import com.example.demo.models.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    @Query("SELECT j.jobName, COUNT(a) FROM Job j JOIN JobApplication a ON j.id = a.job.id GROUP BY j.id")
    List<Object[]> countCandidatesByJob();

    List<JobApplication> findByJobId(Long jobId);

    List<JobApplication> findByCandidateId(Long candidateId);


    @Query("SELECT j.jobName, COUNT(a.id) FROM JobApplication a JOIN a.job j GROUP BY j.jobName")
    List<Object[]> countApplicationsByJob();

    @Query("SELECT j FROM JobApplication a JOIN a.job j GROUP BY j.id ORDER BY COUNT(a.id) DESC LIMIT 1")
    Job findMostPopularJob();

    @Query("SELECT j.jobName, COUNT(a.id) " +
            "FROM Job j LEFT JOIN JobApplication a ON j.id = a.job.id " +
            "GROUP BY j.jobName")
    List<Object[]> getJobApplicationsStats();

//    @Query("SELECT j.jobName, COUNT(ja) " +
//            "FROM JobApplication ja JOIN ja.job j " +
//            "GROUP BY j.jobName")
//    List<Object[]> findApplicantsPerJob();

    @Query("SELECT j.jobName, COUNT(ja.id) " +
            "FROM Job j LEFT JOIN j.applications ja " +
            "GROUP BY j.jobName")
    List<Object[]> getApplicantsPerJobStats();




}
