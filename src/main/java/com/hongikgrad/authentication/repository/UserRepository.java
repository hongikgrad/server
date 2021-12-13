package com.hongikgrad.authentication.repository;

import com.hongikgrad.authentication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByStudentId(String studentId);
    public Boolean existsUserByStudentId(String studentId);
}