package org.example.sciencesocialmedia.repository;

import org.example.sciencesocialmedia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);

    @Query("select u from users u left join fetch u.subscribers left join fetch u.subscriptions where u.id = :id")
    User findByIdWithSubscriptions(@Param("id") String id);
}
