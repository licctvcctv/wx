package com.wechat.studygame.repository;

import com.wechat.studygame.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据仓库
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户可选值
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据用户名或昵称模糊查询用户
     * 
     * @param username 用户名关键词
     * @param nickname 昵称关键词
     * @param pageable 分页参数
     * @return 用户分页数据
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.nickname LIKE %:keyword%")
    Page<User> findByUsernameOrNicknameContaining(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 统计用户总数
     * 
     * @return 用户总数
     */
    long count();
}
