package com.example.demo.services;

import com.example.demo.models.Candidate;
import com.example.demo.models.Company;

import java.util.List;

public interface Icompany {
    public Company findById(long id);

    List<Company> findAllCompanies();
}
