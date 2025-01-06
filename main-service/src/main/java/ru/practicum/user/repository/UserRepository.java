package ru.practicum.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u " +
            "FROM User u " +
            "WHERE (:usersIds IS NULL OR u.id IN :usersIds)")
    List<User> findAllUsersByCondition(@Param("usersIds") List<Long> usersIds, Pageable pageable);

    boolean existsByEmail(String email);

}