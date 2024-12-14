package com.example.demo.services;


import com.example.demo.models.Candidate;
import com.example.demo.models.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICandidate {
    public Candidate findById(long id);
    List<Skill> suggestSkillsForCandidate(Long candidateId);
    public List<Candidate> findCandidatesBySkills(List<Skill> skills);
    Page<Candidate> getCandidates(Pageable pageable);
    List<Candidate> getAllCandidates();
    List<Candidate> searchCandidates(String location, List<Long> skillIds, Integer skillLevel);


    void notifyCandidateOnApplication(Long candidateId, Long jobId);
    void saveCandidate(Candidate candidate);

    void deleteCandidate(Long id);
    List<Candidate> findByIds(List<Long> candidateIds);

    List<Candidate> findCandidatesByName(String name);
}
