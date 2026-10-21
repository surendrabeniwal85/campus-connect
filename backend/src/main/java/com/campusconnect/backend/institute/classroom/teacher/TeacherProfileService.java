package com.campusconnect.backend.institute.classroom.teacher;


import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service public class TeacherProfileService {
 private final TeacherProfileRepository repo;
 public TeacherProfileService(TeacherProfileRepository repo){this.repo=repo;}
 public List<TeacherProfile> list(String department){return department==null?repo.findAll().stream().filter(x->!x.isDeleted()).toList():repo.findAllByDepartmentAndDeletedFalse(department);}
 public TeacherProfile get(Long id){return repo.findById(id).filter(x->!x.isDeleted()).orElseThrow(()->new NoSuchElementException("Teacher profile not found"));}
 @Transactional public TeacherProfile create(Long accountId,String name,String department,String designation,boolean hod){if(repo.existsByAccountIdAndDeletedFalse(accountId))throw new IllegalStateException("A profile already exists for this account"); return repo.save(new TeacherProfile(accountId,name,department,designation,hod));}
 @Transactional public TeacherProfile update(Long id,String name,String department,String designation,boolean hod){TeacherProfile p=get(id);p.update(name,department,designation,hod);return p;}
 @Transactional public void softDelete(Long id){get(id).setDeleted(true);}
}
