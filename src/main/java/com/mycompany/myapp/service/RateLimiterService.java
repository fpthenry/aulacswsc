package com.mycompany.myapp.service;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Giới hạn số lần gửi form công khai theo IP (lưu trong bộ nhớ, đủ cho 1 máy chủ).
 * Chạy nhiều máy chủ thì cần chuyển sang Redis / bucket4j.
 */
@Service
public class RateLimiterService {

    private final Map<String, Deque<Long>> hits = new ConcurrentHashMap<>();

    /**
     * Ghi nhận một lần gọi; vượt {@code max} lần trong {@code window} thì ném 429 Too Many Requests.
     */
    public void check(String action, String clientKey, int max, Duration window) {
        long now = System.currentTimeMillis();
        long from = now - window.toMillis();
        Deque<Long> deque = hits.computeIfAbsent(action + ":" + clientKey, k -> new ArrayDeque<>());
        synchronized (deque) {
            while (!deque.isEmpty() && deque.peekFirst() < from) {
                deque.pollFirst();
            }
            if (deque.size() >= max) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests, please try again later");
            }
            deque.addLast(now);
        }
    }

    /** Dọn các khóa không còn lượt gọi trong 1 giờ để tránh phình bộ nhớ. */
    @Scheduled(fixedDelay = 600_000)
    public void cleanUp() {
        long from = System.currentTimeMillis() - Duration.ofHours(1).toMillis();
        hits
            .entrySet()
            .removeIf(e -> {
                synchronized (e.getValue()) {
                    return e.getValue().isEmpty() || e.getValue().peekLast() < from;
                }
            });
    }
}
