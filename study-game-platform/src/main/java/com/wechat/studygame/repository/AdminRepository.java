package com.wechat.studygame.repository;

import com.wechat.studygame.model.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 管理员数据访问接口
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    /**
     * 根据用户名查找管理员
     *
     * @param username 用户名
     * @return 管理员
     */
    Optional<Admin> findByUsername(String username);
}
