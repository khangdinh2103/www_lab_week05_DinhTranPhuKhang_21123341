package com.example.demo.services.impl;

import com.example.demo.models.Candidate;
import com.example.demo.models.Company;
import com.example.demo.repositories.CandidateRepository;
import com.example.demo.repositories.CompanyRepository;
import com.example.demo.services.Icompany;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class CompanyImpl implements Icompany {
    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public Company findById(long id) {
        Optional<Company> company = companyRepository.findById(id);
        return company.orElse(null);
    }

    @Override
    public List<Company> findAllCompanies() {
        return companyRepository.findAll(); // Trả về tất cả các công ty
    }
}
