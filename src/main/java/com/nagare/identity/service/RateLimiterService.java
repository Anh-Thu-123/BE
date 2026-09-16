package com.nagare.identity.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * Rate limit don gian trong bo nho theo IP, cho hai cua mo khong dang nhap:
 * /api/auth/register (5 lan/gio) va /api/public/tour-requests (10 lan/gio).
 */
@Service
public class RateLimiterService {

    private final ConcurrentHashMap<String, Bucket> registerBuckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Bucket> tourRequestBuckets = new ConcurrentHashMap<>();

    public boolean tryRegister(String ip) {
        return registerBuckets.computeIfAbsent(ip, k -> newBucket(5)).tryConsume(1);
    }

    public boolean tryTourRequest(String ip) {
        return tourRequestBuckets.computeIfAbsent(ip, k -> newBucket(10)).tryConsume(1);
    }

    private Bucket newBucket(int perHour) {
        Bandwidth limit = Bandwidth.classic(perHour, io.github.bucket4j.Refill.intervally(perHour, Duration.ofHours(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}
