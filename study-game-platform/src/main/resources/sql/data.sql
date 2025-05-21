-- 插入示例学科数据
INSERT INTO subjects (name, description, icon_url, sort_order, status, create_time, update_time) VALUES 
('数学', '初中数学课程', 'https://example.com/math.png', 1, 1, NOW(), NOW()),
('语文', '初中语文课程', 'https://example.com/chinese.png', 2, 1, NOW(), NOW()),
('英语', '初中英语课程', 'https://example.com/english.png', 3, 1, NOW(), NOW()),
('物理', '初中物理课程', 'https://example.com/physics.png', 4, 1, NOW(), NOW()),
('化学', '初中化学课程', 'https://example.com/chemistry.png', 5, 1, NOW(), NOW());

-- 插入示例章节数据（数学学科）
INSERT INTO chapters (subject_id, name, description, icon_url, difficulty_level, sort_order, status, create_time, update_time) VALUES 
(1, '整数与有理数', '正数、负数、有理数的概念和运算', 'https://example.com/chapter1.png', 1, 1, 1, NOW(), NOW()),
(1, '代数式', '整式、分式的概念和运算', 'https://example.com/chapter2.png', 2, 2, 1, NOW(), NOW()),
(1, '一元一次方程', '一元一次方程的概念和解法', 'https://example.com/chapter3.png', 2, 3, 1, NOW(), NOW()),
(1, '几何图形', '平面几何图形的基本概念和性质', 'https://example.com/chapter4.png', 3, 4, 1, NOW(), NOW());

-- 插入示例关卡数据（整数与有理数章节）
INSERT INTO levels (chapter_id, name, description, difficulty_level, required_correct_count, require_all_correct, total_questions, base_score, sort_order, status, create_time, update_time) VALUES 
(1, '正数和负数', '了解正数和负数的概念', 1, 3, false, 5, 10, 1, 1, NOW(), NOW()),
(1, '有理数', '了解有理数的概念和性质', 2, 4, false, 5, 15, 2, 1, NOW(), NOW()),
(1, '有理数的加减法', '掌握有理数的加减法运算', 3, 5, true, 5, 20, 3, 1, NOW(), NOW());

-- 插入示例题目数据（正数和负数关卡）
INSERT INTO questions (level_id, chapter_id, subject_id, content, type, options, answer, analysis, knowledge_points, difficulty_level, score, sort_order, status, create_time, update_time) VALUES 
(1, 1, 1, '下列数中，正数有几个？0，-1，2，-3，4', 1, '[{"key":"A","value":"1个"},{"key":"B","value":"2个"},{"key":"C","value":"3个"},{"key":"D","value":"4个"}]', 'B', '正数是大于0的数，所以2和4是正数，共2个。', '正数和负数的概念', 1, 10, 1, 1, NOW(), NOW()),
(1, 1, 1, '-5的相反数是多少？', 1, '[{"key":"A","value":"-5"},{"key":"B","value":"5"},{"key":"C","value":"0"},{"key":"D","value":"不存在"}]', 'B', '一个数的相反数就是与这个数互为相反数的数，-5的相反数是5。', '相反数的概念', 1, 10, 2, 1, NOW(), NOW()),
(1, 1, 1, '下列说法正确的是：', 1, '[{"key":"A","value":"负数都小于正数"},{"key":"B","value":"0是最小的正数"},{"key":"C","value":"负数越大，其绝对值越大"},{"key":"D","value":"两个负数中，数值越小的负数反而越大"}]', 'A', '所有的负数都小于0，所有的正数都大于0，所以所有的负数都小于所有的正数。', '正数和负数的大小比较', 2, 15, 3, 1, NOW(), NOW()),
(1, 1, 1, '在数轴上，-3.5位于哪两个整数之间？', 1, '[{"key":"A","value":"-4和-3"},{"key":"B","value":"-3和-2"},{"key":"C","value":"3和4"},{"key":"D","value":"-2和-1"}]', 'A', '-3.5在数轴上位于-4和-3之间，因为-4 < -3.5 < -3。', '数轴与数的表示', 2, 15, 4, 1, NOW(), NOW()),
(1, 1, 1, '判断对错：0是既不是正数也不是负数的数。', 3, '[{"key":"A","value":"对"},{"key":"B","value":"错"}]', 'A', '0既不大于0，也不小于0，所以0既不是正数也不是负数。', '0的性质', 1, 10, 5, 1, NOW(), NOW());
