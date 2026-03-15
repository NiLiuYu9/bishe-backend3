-- 更新 API 的总调用次数统计
UPDATE api_info ai
SET 
    ai.invoke_count = (
        SELECT COALESCE(SUM(total_count), 0)
        FROM api_invoke_daily aid
        WHERE aid.api_id = ai.id
    ),
    ai.success_count = (
        SELECT COALESCE(SUM(success_count), 0)
        FROM api_invoke_daily aid
        WHERE aid.api_id = ai.id
    ),
    ai.fail_count = (
        SELECT COALESCE(SUM(fail_count), 0)
        FROM api_invoke_daily aid
        WHERE aid.api_id = ai.id
    );
