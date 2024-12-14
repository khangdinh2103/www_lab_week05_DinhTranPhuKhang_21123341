package com.example.demo.controllers;

import com.example.demo.enums.AccountRole;
import com.example.demo.models.Account;
import com.example.demo.models.Address;
import com.example.demo.models.Candidate;
import com.example.demo.services.IAccount;
import com.example.demo.services.IAddress;
import com.example.demo.services.ICandidate;
import com.example.demo.services.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/candidates")
public class CandidateController {

    @Autowired
    private ICandidate candidateService;

    @Autowired
    private IAddress addressService;

    @Autowired
    private IAccount accountService;

    @Autowired
    private IEmailService emailService;


    @GetMapping
    public String showCandidateList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<Candidate> candidatePage = candidateService.getCandidates(PageRequest.of(page, size));
        model.addAttribute("candidates", candidatePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", candidatePage.getTotalPages());

        return "candidate-list"; // Points to the Thymeleaf template
    }

    @GetMapping("/all")
    public String showAllCandidates(Model model) {
        List<Candidate> candidates = candidateService.getAllCandidates();
        model.addAttribute("candidates", candidates);
        return "candidate-list-full"; // Non-paginated view
    }

    @GetMapping("/candidate-search")
    public String showSearchForm() {

        return "candidate-search";
    }

    @GetMapping("/search")
    public String searchCandidates(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) List<Long> skillIds,
            @RequestParam(required = false) Integer skillLevel,
            Model model
    ) {
        List<Candidate> candidates = candidateService.searchCandidates(location, skillIds, skillLevel);
        model.addAttribute("candidates", candidates);
        return "candidate-search-results";
    }

    // Gửi email đến các ứng viên đã chọn
    @PostMapping("/send-email")
    public String sendEmailToCandidates(@RequestParam List<Long> candidateIds, Model model) {
        try {
            // Lấy danh sách ứng viên theo ID
            List<Candidate> candidates = candidateService.findByIds(candidateIds);

            // Gửi email cho mỗi ứng viên
            for (Candidate candidate : candidates) {
                String subject = "Thông báo về cơ hội nghề nghiệp";

                // Tạo nội dung email chi tiết
                String body = "Chào " + candidate.getFullName() + ",\n\n"
                        + "Chúng tôi rất vui khi được thông báo bạn về các cơ hội nghề nghiệp tại công ty chúng tôi.\n\n"
                        + "Dưới đây là một số thông tin về các công việc mà bạn có thể quan tâm:\n"
                        + "- Vị trí: [Tên vị trí]\n"
                        + "- Mô tả công việc: [Mô tả công việc chi tiết]\n"
                        + "- Yêu cầu kỹ năng: [Danh sách kỹ năng yêu cầu]\n\n"
                        + "Nếu bạn quan tâm đến vị trí này, vui lòng gửi hồ sơ xin việc và thư xin việc của bạn qua email: [email công ty] hoặc liên hệ với chúng tôi qua số điện thoại: [số điện thoại].\n\n"
                        + "Chúng tôi mong muốn được gặp bạn trong cuộc phỏng vấn sắp tới.\n\n"
                        + "Trân trọng,\n"
                        + "[Tên công ty]\n"
                        + "[Địa chỉ công ty]\n"
                        + "[Số điện thoại công ty]";

                // Gửi email đến ứng viên
                emailService.sendEmail(candidate.getEmail(), subject, body);
            }

            // Hiển thị thông báo thành công
            model.addAttribute("successMessage", "Đã gửi email thành công đến các ứng viên.");
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi gửi email: " + e.getMessage());
            e.printStackTrace();  // In chi tiết lỗi ra console
        }
        return "candidate-search-results";  // Quay lại trang kết quả tìm kiếm
    }



    @GetMapping("/add")
    public String showAddCandidateForm(Model model) {
        model.addAttribute("candidate", new Candidate()); // Model cho form
        model.addAttribute("address", new Address()); // Thêm model cho Address
        return "candidate-add"; // Đi tới giao diện thêm ứng viên
    }

    @PostMapping("/add")
    public String addCandidate(
            @ModelAttribute Candidate candidate,
            @ModelAttribute Address address,
            Model model
    ) {
        try {
            // Lưu Address trước
            Address savedAddress = addressService.saveAddress(address);

            // Gán Address vào Candidate
            candidate.setAddress(savedAddress);

            // Kiểm tra nếu Candidate không có Account thì tạo một tài khoản mặc định
            if (candidate.getAccount() == null || candidate.getAccount().getId() == null) {
                Account defaultAccount = new Account();
                defaultAccount.setUsername(candidate.getEmail()); // Sử dụng email làm username
                defaultAccount.setPassword("defaultPassword"); // Mật khẩu mặc định
                defaultAccount.setRole(AccountRole.CANDIDATE); // Sử dụng giá trị Enum thay vì String
                accountService.saveAccount(defaultAccount); // Sử dụng phương thức đã sửa
                candidate.setAccount(defaultAccount);
            }

            // Lưu Candidate
            candidateService.saveCandidate(candidate);

            // Chuyển hướng về danh sách ứng viên sau khi thêm thành công
            return "redirect:/candidates";

        } catch (Exception e) {
            // Xử lý lỗi và hiển thị thông báo lỗi trên giao diện
            model.addAttribute("errorMessage", "Đã xảy ra lỗi khi thêm ứng viên: " + e.getMessage());
            model.addAttribute("candidate", candidate);
            model.addAttribute("address", address);
            return "candidate-add"; // Quay lại form thêm ứng viên
        }
    }


    @GetMapping("/edit/{id}")
    public String showEditCandidateForm(@PathVariable Long id, Model model) {
        try {
            Candidate candidate = candidateService.findById(id); // Lấy ứng viên từ ID
            if (candidate != null) {
                model.addAttribute("candidate", candidate);
                return "candidate-edit"; // Hiển thị giao diện sửa ứng viên
            }
            model.addAttribute("errorMessage", "Ứng viên không tồn tại.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
        }
        return "redirect:/candidates";
    }



    @PostMapping("/edit")
    public String updateCandidate(@ModelAttribute Candidate candidate, Model model) {
        try {
            // Lấy Candidate hiện tại từ database
            Candidate existingCandidate = candidateService.findById(candidate.getId());
            if (existingCandidate == null) {
                throw new IllegalArgumentException("Ứng viên không tồn tại.");
            }

            // Cập nhật thông tin Address
            Address address = existingCandidate.getAddress();
            if (address != null) {
                address.setStreet(candidate.getAddress().getStreet());
                address.setCity(candidate.getAddress().getCity());
                address.setZipcode(candidate.getAddress().getZipcode());
            } else {
                existingCandidate.setAddress(candidate.getAddress());
            }

            // Cập nhật các thông tin khác của Candidate
            existingCandidate.setFullName(candidate.getFullName());
            existingCandidate.setEmail(candidate.getEmail());
            existingCandidate.setPhone(candidate.getPhone());
            existingCandidate.setDob(candidate.getDob());

            // Lưu Candidate
            candidateService.saveCandidate(existingCandidate);

            return "redirect:/candidates";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật ứng viên: " + e.getMessage());
            model.addAttribute("candidate", candidate);
            return "candidate-edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteCandidate(@PathVariable Long id, Model model) {
        try {
            Candidate candidate = candidateService.findById(id);
            if (candidate == null) {
                model.addAttribute("errorMessage", "Ứng viên không tồn tại.");
            } else {
                candidateService.deleteCandidate(id);
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi xóa ứng viên: " + e.getMessage());
        }
        return "redirect:/candidates"; // Quay lại danh sách ứng viên
    }


}
