package com.api.platform.service;

public interface StatisticsSyncService {

    void syncRedisToDatabase();
    
    void syncDailyStatisticsToApiInfo();

    void syncApiRating();
}
