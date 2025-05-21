-- 创建数据库
CREATE DATABASE IF NOT EXISTS study_game DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE study_game;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    open_id VARCHAR(64) NOT NULL COMMENT '微信小程序的openid',
    nickname VARCHAR(50) COMMENT '微信用户昵称',
    avatar_url VARCHAR(255) COMMENT '微信头像URL',
    total_score INT DEFAULT 0 COMMENT '用户积分总分',
    create_time DATETIME COMMENT '账号创建时间',
    last_login_time DATETIME COMMENT '最后登录时间',
    status INT DEFAULT 0 COMMENT '账号状态 (0: 正常, 1: 封禁)',
    UNIQUE KEY (open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 学科表
CREATE TABLE IF NOT EXISTS subjects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '学科名称',
    description VARCHAR(255) COMMENT '学科描述',
    icon_url VARCHAR(255) COMMENT '学科图标URL',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    status INT DEFAULT 1 COMMENT '是否启用 (0: 禁用, 1: 启用)',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学科表';

-- 章节表
CREATE TABLE IF NOT EXISTS chapters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL COMMENT '所属学科ID',
    name VARCHAR(100) NOT NULL COMMENT '章节名称',
    description VARCHAR(255) COMMENT '章节描述',
    icon_url VARCHAR(255) COMMENT '章节图标URL',
    difficulty_level INT DEFAULT 1 COMMENT '章节难度级别（1-5）',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    status INT DEFAULT 1 COMMENT '是否启用 (0: 禁用, 1: 启用)',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    INDEX (subject_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节表';

-- 关卡表
CREATE TABLE IF NOT EXISTS levels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chapter_id BIGINT NOT NULL COMMENT '所属章节ID',
    name VARCHAR(100) NOT NULL COMMENT '关卡名称',
    description VARCHAR(255) COMMENT '关卡描述',
    difficulty_level INT DEFAULT 1 COMMENT '关卡难度级别（1-5）',
    required_correct_count INT COMMENT '通过所需题目数量',
    require_all_correct BOOLEAN DEFAULT FALSE COMMENT '是否需要全部答对才能过关',
    total_questions INT COMMENT '题目总数',
    base_score INT DEFAULT 10 COMMENT '每道题的分值基数',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    status INT DEFAULT 1 COMMENT '是否启用 (0: 禁用, 1: 启用)',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    INDEX (chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关卡表';

-- 题目表
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    level_id BIGINT NOT NULL COMMENT '所属关卡ID',
    chapter_id BIGINT COMMENT '所属章节ID（冗余字段，方便查询）',
    subject_id BIGINT COMMENT '所属学科ID（冗余字段，方便查询）',
    content TEXT NOT NULL COMMENT '题目内容',
    type INT COMMENT '题目类型 (1: 单选题, 2: 多选题, 3: 判断题, 4: 填空题, 5: 简答题)',
    options TEXT COMMENT '题目选项，JSON格式',
    answer TEXT NOT NULL COMMENT '正确答案',
    analysis TEXT COMMENT '题目解析',
    knowledge_points VARCHAR(255) COMMENT '涉及知识点',
    difficulty_level INT DEFAULT 1 COMMENT '题目难度级别（1-5）',
    score INT DEFAULT 10 COMMENT '分值',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    status INT DEFAULT 1 COMMENT '是否启用 (0: 禁用, 1: 启用)',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    INDEX (level_id),
    INDEX (chapter_id),
    INDEX (subject_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 用户答题记录表
CREATE TABLE IF NOT EXISTS user_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    level_id BIGINT COMMENT '关卡ID',
    chapter_id BIGINT COMMENT '章节ID',
    subject_id BIGINT COMMENT '学科ID',
    user_answer TEXT COMMENT '用户的答案',
    is_correct BOOLEAN COMMENT '是否回答正确',
    score INT DEFAULT 0 COMMENT '获得的分数',
    answer_time BIGINT COMMENT '答题时间（毫秒）',
    is_first_attempt BOOLEAN DEFAULT TRUE COMMENT '是否是首次答题',
    create_time DATETIME COMMENT '创建时间',
    INDEX (user_id),
    INDEX (question_id),
    INDEX (level_id),
    INDEX (chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户答题记录表';

-- 用户关卡进度表
CREATE TABLE IF NOT EXISTS user_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    level_id BIGINT NOT NULL COMMENT '关卡ID',
    chapter_id BIGINT COMMENT '章节ID',
    subject_id BIGINT COMMENT '学科ID',
    status INT DEFAULT 0 COMMENT '进度状态 (0: 未开始, 1: 进行中, 2: 已完成)',
    correct_count INT DEFAULT 0 COMMENT '正确题目数量',
    total_count INT DEFAULT 0 COMMENT '题目总数',
    total_score INT DEFAULT 0 COMMENT '获得的总分数',
    time_used BIGINT DEFAULT 0 COMMENT '用时（秒）',
    last_attempt_time DATETIME COMMENT '最后一次闯关时间',
    first_completion_time DATETIME COMMENT '首次完成时间',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    INDEX (user_id),
    INDEX (level_id),
    INDEX (chapter_id),
    UNIQUE KEY (user_id, level_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关卡进度表';
