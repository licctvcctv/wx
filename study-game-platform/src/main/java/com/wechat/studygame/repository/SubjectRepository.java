package com.wechat.studygame.repository;

import com.wechat.studygame.model.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 学科数据仓库
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    /**
     * 查找所有启用的学科，按排序权重排序
     *
     * @return 学科列表
     */
    List<Subject> findByStatusOrderBySortOrderAsc(Integer status);
}
