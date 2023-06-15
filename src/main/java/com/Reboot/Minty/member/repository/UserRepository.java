package com.Reboot.Minty.member.repository;

import com.Reboot.Minty.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);


    User findByNickName(String nickName);

    int countByEmail(String email);

    int countByMobile(String mobile);

    boolean existsByEmail(String email);
    boolean existsByNickName(String nickName);

    boolean existsByMobile(String mobile);
}
