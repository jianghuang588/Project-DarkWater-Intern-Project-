package com.example.demo.user;


import com.example.demo.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NewsScheduler {

    @Autowired
    private NewsService newsService;

    // Run every minute to check for scheduled posts
    @Scheduled(fixedRate = 60000) // 60 seconds
    public void publishScheduledPosts() {
        newsService.publishScheduledPosts();
    }
}