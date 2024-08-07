package com.example.intuitcraftdemoproject.repository;

import com.example.intuitcraftdemoproject.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
}
