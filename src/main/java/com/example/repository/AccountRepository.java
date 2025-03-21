package com.example.repository;

import com.example.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUsername(String username);

    Account findByUsername(String username);

    Account findByUsernameAndPassword(String username, String password);

    Account findByAccountId(Integer accountId);
}