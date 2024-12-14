package com.example.demo.controllers;

import com.example.demo.models.Account;
import com.example.demo.services.IAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private IAccount accountService;

    @GetMapping("/")
    public String redirectToRegisterOrHome(Authentication authentication) {
        // Nếu người dùng chưa đăng nhập, chuyển hướng đến trang đăng ký
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/register";
        }
        // Nếu đã đăng nhập, chuyển đến trang home
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String showHomePage(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login"; // Nếu không đăng nhập, chuyển hướng
        }

        String username = authentication.getName();
        Account account = accountService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("username", account.getUsername());
        model.addAttribute("role", account.getRole().name());

        return "home"; // Trả về giao diện home.html
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact"; // Trang liên hệ
    }

}
