-- 更新chapters表中createTime为null的记录
UPDATE chapters 
SET create_time = NOW() 
WHERE create_time IS NULL;

-- 更新chapters表中updateTime为null的记录
UPDATE chapters 
SET update_time = NOW() 
WHERE update_time IS NULL;

-- 更新levels表中createTime为null的记录
UPDATE levels 
SET create_time = NOW() 
WHERE create_time IS NULL;

-- 更新levels表中updateTime为null的记录
UPDATE levels 
SET update_time = NOW() 
WHERE update_time IS NULL;
