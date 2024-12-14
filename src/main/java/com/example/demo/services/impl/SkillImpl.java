package com.example.demo.services.impl;

import com.example.demo.models.Skill;
import com.example.demo.repositories.SkillRepository;
import com.example.demo.services.ISkill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillImpl implements ISkill {
    @Autowired
    private SkillRepository skillRepository;
    @Override
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }
    @Override
    public void add(Skill skill) {
        skillRepository.save(skill);
    }

    @Override
    public void delete(Long id) {
        skillRepository.deleteById(id);
    }

    @Override
    public void update(Skill skill) {
        skillRepository.save(skill);
    }

    @Override
    public List<Skill> getSkillsByIds(List<Long> skillIds) {
        // Gọi phương thức trong repository để lấy các kỹ năng theo danh sách ID
        return skillRepository.findByIdIn(skillIds);
    }
}
