package com.nagare.identity.scheduler;

import com.nagare.identity.repo.RefreshTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Du da co TTL index tren expiresAt, job nay don them cac token da revoked qua lau
 * (Mongo TTL chi xoa dua vao expiresAt, khong biet ve revokedAt).
 */
@Component
public class RefreshTokenCleanupJob {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenCleanupJob(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Scheduled(cron = "0 30 3 * * *")
    public void cleanup() {
        var all = refreshTokenRepository.findAll();
        var toDelete = all.stream()
                .filter(rt -> rt.getRevokedAt() != null
                        && rt.getRevokedAt().isBefore(java.time.Instant.now().minusSeconds(86400)))
                .toList();
        refreshTokenRepository.deleteAll(toDelete);
    }
}
