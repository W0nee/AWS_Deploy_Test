package com.ducks.goodsduck.commons.repository;

import com.ducks.goodsduck.commons.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAll();
    Optional<User> findById(Long id);
}
