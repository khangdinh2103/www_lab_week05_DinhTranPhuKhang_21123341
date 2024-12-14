package com.example.demo.controllers;

import com.example.demo.enums.AccountRole;
import com.example.demo.models.Account;
import com.example.demo.services.IAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAccount accountService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("account", new Account());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Account account, @RequestParam("role") String role) {
        account.setRole(AccountRole.valueOf(role.toUpperCase()));
        accountService.register(account);
        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
