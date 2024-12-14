package com.example.demo.services;


import com.example.demo.models.Skill;

import java.util.List;

public interface ISkill {
    public List<Skill> getAllSkills();
    public void add(Skill skill);
    public void delete(Long id);
    public void update(Skill skill);
    List<Skill> getSkillsByIds(List<Long> skillIds);

}
