package com.example.demo.controllers;

import com.example.demo.models.*;
import com.example.demo.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/job")
public class JobController {
    @Autowired
    private IJob jobService;

    @Autowired
    private ISkill skillService;

    @Autowired
    private IJobSkill jobSkillService;

    @Autowired
    private ICandidate candidateService;

    @Autowired
    private Icompany companyService;

    @Autowired
    private IEmailService emailService;

    @Autowired
    private IJobApplication jobApplicationService;

    @GetMapping("/")
    public String home() {
        return "home"; // Trả về trang home.html
    }

    @GetMapping("/company-id-form")
    public String showCompanyIdForm(Model model) {
        List<Company> companies = companyService.findAllCompanies(); // Lấy tất cả công ty
        if (companies.isEmpty()) {
            model.addAttribute("errorMessage", "Không có công ty nào được tìm thấy.");
        }
        model.addAttribute("companies", companies); // Truyền danh sách công ty vào model
        return "company-id-form";  // Trả về view
    }


    @GetMapping("/jobs")
    public String getAllJobs(@RequestParam Long companyId, Model model) {
        Company company = companyService.findById(companyId); // Lấy thông tin công ty
        List<Job> jobs = jobService.getJobByCompanyId(companyId); // Lấy danh sách công việc của công ty

        model.addAttribute("companyName", company != null ? company.getCompName() : "Không rõ");
        model.addAttribute("companyId", companyId);
        model.addAttribute("jobs", jobs);
        return "job-list"; // Trả về view 'job-list.html'
    }


    @GetMapping("/job/create")
    public String showJobForm(@RequestParam Long companyId, Model model) {
        model.addAttribute("skills", skillService.getAllSkills()); // Thêm kỹ năng vào form
        model.addAttribute("companyId", companyId); // Truyền companyId vào form để tạo công việc
        return "job-form"; // Hiển thị form tạo công việc
    }


    @PostMapping("/job/create")
    public String createJob(Job job,
                            @RequestParam("skills[]") List<Long> skillIds,
                            @RequestParam("skillLevels[]") List<Byte> skillLevels,
                            @RequestParam Long companyId) {

        // Kiểm tra nếu số lượng kỹ năng không khớp với số lượng cấp độ
        if (skillIds.size() != skillLevels.size()) {
            throw new IllegalArgumentException("Mỗi kỹ năng phải có cấp độ tương ứng.");
        }

        // Lấy danh sách kỹ năng từ ID
        List<Skill> skills = skillService.getSkillsByIds(skillIds);

        // Kiểm tra nếu có kỹ năng không hợp lệ
        if (skills.size() != skillIds.size()) {
            throw new IllegalArgumentException("Một số kỹ năng không hợp lệ.");
        }

        // Tạo danh sách JobSkill từ kỹ năng và cấp độ
        List<JobSkill> jobSkills = new ArrayList<>();
        for (int i = 0; i < skills.size(); i++) {
            Skill skill = skills.get(i);
            Byte skillLevel = skillLevels.get(i);

            JobSkill jobSkill = new JobSkill();
            JobSkillId jobSkillId = new JobSkillId();
            jobSkillId.setJobId(job.getId());
            jobSkillId.setSkillId(skill.getId());

            jobSkill.setId(jobSkillId);
            jobSkill.setJob(job);
            jobSkill.setSkill(skill);
            jobSkill.setSkillLevel(skillLevel);

            jobSkills.add(jobSkill);
        }

        // Tìm công ty và gán cho công việc
        Company company = companyService.findById(companyId);
        if (company == null) {
            throw new IllegalArgumentException("Công ty không tồn tại.");
        }
        job.setCompany(company);

        if (job.getCreatedDate() == null) {
            job.setCreatedDate(LocalDateTime.now());
        }

        // Lưu công việc và kỹ năng vào database
        jobService.saveJob(job);
        jobSkillService.saveJobSkills(jobSkills);

        return "redirect:/job/jobs?companyId=" + companyId;
    }

    @GetMapping("/job/{id}")
    public String getJobDetail(@PathVariable Long id, Model model) {
        Job job = jobService.getJobById(id);
        model.addAttribute("job", job);

        // Lấy danh sách kỹ năng yêu cầu cho công việc
        List<Skill> jobSkills = jobSkillService.getSkillsByJobId(id);

        // Tìm các ứng viên có kỹ năng phù hợp
        List<Candidate> candidates = candidateService.findCandidatesBySkills(jobSkills);

        // Thêm danh sách ứng viên vào model
        model.addAttribute("candidates", candidates);

        return "job-detail";
    }

    @GetMapping("/suggest-form")
    public String showSuggestForm() {
        return "candidate-id-form";  // Chuyển hướng đến trang nhập ID ứng viên
    }

    @PostMapping("/suggest")
    public String suggestJobs(@RequestParam("candidateId") Long candidateId, Model model) {
        Candidate candidate = candidateService.findById(candidateId);

        if (candidate != null) {
            List<Job> suggestedJobs = jobService.getJobsMatchingSkills(candidateId);
            model.addAttribute("suggestedJobs", suggestedJobs); // Thêm danh sách công việc vào model
            model.addAttribute("candidateId", candidateId); // Thêm candidateId vào model
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy ứng viên với ID đã nhập.");
        }

        return "suggested-jobs"; // Trả về giao diện danh sách công việc gợi ý
    }

    @PostMapping("/send-email")
    public String sendSuggestedJobsEmail(@RequestParam("candidateId") Long candidateId, Model model) {
        Candidate candidate = candidateService.findById(candidateId);

        if (candidate != null) {
            List<Job> suggestedJobs = jobService.getJobsMatchingSkills(candidateId);

            if (!suggestedJobs.isEmpty()) {
                // Xây dựng nội dung email
                StringBuilder emailContent = new StringBuilder("Danh sách công việc phù hợp:\n");
                for (Job job : suggestedJobs) {
                    emailContent.append("- ")
                            .append(job.getJobName())
                            .append(" tại ")
                            .append(job.getCompany() != null ? job.getCompany().getCompName() : "Không rõ công ty")
                            .append("\n");
                }

                // Gửi email
                emailService.sendEmail(
                        candidate.getEmail(),
                        "Gợi ý công việc phù hợp",
                        emailContent.toString()
                );

                model.addAttribute("message", "Email gợi ý công việc đã được gửi!");
            } else {
                model.addAttribute("message", "Không có công việc phù hợp để gửi email.");
            }
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy ứng viên với ID đã nhập.");
        }

        return "email-result"; // Trả về trang kết quả gửi email
    }


    // Trang hiển thị form nhập ID
    @GetMapping("/candidate/skills")
    public String showForm() {
        return "candidate-id-form";
    }

    // Xử lý form khi ứng viên nhập ID
    @PostMapping("/candidate/skills")
    public String suggestSkills(@RequestParam Long candidateId, Model model) {
        List<Skill> suggestedSkills = candidateService.suggestSkillsForCandidate(candidateId);
        model.addAttribute("suggestedSkills", suggestedSkills);
        return "suggested-skills";
    }

    @GetMapping("/job-search")
    public String showSearchForm() {
        return "job-search";
    }


    @GetMapping("/search")
    public String searchJobs(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer experience,
            @RequestParam(required = false) List<Long> skillIds,
            @RequestParam(required = false) Integer skillLevel,
            Model model
    ) {
        List<Job> jobs = jobService.searchJobs(location, experience, skillIds, skillLevel);
        model.addAttribute("jobs", jobs);
        return "job-search-results";
    }


    @GetMapping("/stats/applicants-per-job")
    public String getApplicantsPerJobStats(Model model) {
        List<Object[]> stats = jobService.getApplicantsPerJobStats();
        List<String> jobNames = new ArrayList<>();
        List<Long> applicantCounts = new ArrayList<>();

        for (Object[] stat : stats) {
            jobNames.add((String) stat[0]);
            applicantCounts.add(((Number) stat[1]).longValue());
        }

        // Thêm log để kiểm tra dữ liệu
        System.out.println("Job Names: " + jobNames);
        System.out.println("Applicant Counts: " + applicantCounts);

        model.addAttribute("jobNames", jobNames);
        model.addAttribute("applicantCounts", applicantCounts);
        return "job-stats";
    }


    @GetMapping("/stats/jobs-by-month")
    public String getJobsByMonthStats(Model model) {
        List<Object[]> stats = jobService.getJobsByMonthStats();
        List<String> months = new ArrayList<>();
        List<Long> jobCounts = new ArrayList<>();

        for (Object[] stat : stats) {
            months.add(stat[0].toString());
            jobCounts.add(((Number) stat[1]).longValue());
        }
        System.out.println("Job Names: " + months);
        System.out.println("Applicant Counts: " + jobCounts);
        model.addAttribute("months", months);
        model.addAttribute("jobCounts", jobCounts);

        return "jobs-by-month-stats";
    }
}