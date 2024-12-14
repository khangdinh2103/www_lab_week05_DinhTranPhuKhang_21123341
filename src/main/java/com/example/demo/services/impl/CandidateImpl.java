package com.example.demo.services.impl;

import com.example.demo.models.Candidate;
import com.example.demo.models.Job;
import com.example.demo.models.Skill;
import com.example.demo.repositories.CandidateRepository;
import com.example.demo.repositories.JobRepository;
import com.example.demo.repositories.SkillRepository;
import com.example.demo.services.ICandidate;
import com.example.demo.services.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CandidateImpl implements ICandidate {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private IEmailService emailService;


    @Autowired
    private SkillRepository skillRepository;


    @Override
    public Candidate findById(long id) {
        Optional<Candidate> candidate = candidateRepository.findById(id);
        return candidate.orElse(null);
    }



    // Đề xuất các skill mà ứng viên chưa có
    @Override
    public List<Skill> suggestSkillsForCandidate(Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        List<Skill> allSkills = skillRepository.findAll();
        List<Skill> candidateSkills = candidate.getSkills();

        return allSkills.stream()
                .filter(skill -> !candidateSkills.contains(skill))
                .collect(Collectors.toList());
    }

    @Override
    public List<Candidate> findCandidatesBySkills(List<Skill> skills) {
        return candidateRepository.findCandidatesBySkills(skills);
    }


    @Override
    public Page<Candidate> getCandidates(Pageable pageable) {
        return candidateRepository.findAll(pageable);
    }

    @Override
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll(); // Retrieve all candidates without pagination
    }

    @Override
    public List<Candidate> searchCandidates(String location, List<Long> skillIds, Integer skillLevel) {
        return candidateRepository.searchCandidates(location, skillIds, skillLevel);
    }

    @Override
    public void notifyCandidateOnApplication(Long candidateId, Long jobId) {
        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
        Job job = jobRepository.findById(jobId).orElse(null);

        if (candidate != null && job != null) {
            String subject = "Xác nhận nộp đơn ứng tuyển";
            String body = "Chào " + candidate.getFullName() +
                    ",\n\nBạn đã nộp đơn thành công cho công việc: " + job.getJobName() +
                    " tại " + job.getCompany().getCompName() + ".\n\nChúc bạn may mắn!";
            emailService.sendEmail(candidate.getEmail(), subject, body);
        }
    }

    @Override
    public void saveCandidate(Candidate candidate) {
        if (candidate.getAccount() == null || candidate.getAccount().getId() == null) {
            throw new IllegalArgumentException("Ứng viên phải có tài khoản hợp lệ.");
        }
        candidateRepository.save(candidate);
    }

    @Override
    public void deleteCandidate(Long id) {
        candidateRepository.deleteById(id);
    }

    @Override
    public List<Candidate> findByIds(List<Long> candidateIds) {
        return candidateRepository.findAllById(candidateIds);
    }

    @Override
    public List<Candidate> findCandidatesByName(String name) {
        return candidateRepository.findByFullNameContainingIgnoreCase(name);
    }


}
