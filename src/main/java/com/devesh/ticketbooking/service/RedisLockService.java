package com.devesh.ticketbooking.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final StringRedisTemplate redisTemplate;

    private static final Duration HOLD_DURATION = Duration.ofMinutes(5);

    /**
     * Attempts to acquire a lock on a seat for a specific event.
     * Returns true if the lock was acquired (seat successfully held),
     * false if someone else already holds it.
     */
    public boolean acquireLock(Long eventId, Long seatId, Long userId) {
        String key = buildKey(eventId, seatId);
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(key, String.valueOf(userId), HOLD_DURATION);
        return Boolean.TRUE.equals(acquired);
    }

    /**
     * Releases the lock — called after successful booking confirmation,
     * or if the user cancels before completing payment.
     */
    public void releaseLock(Long eventId, Long seatId) {
        redisTemplate.delete(buildKey(eventId, seatId));
    }

    /**
     * Checks if a seat is currently held (by anyone) without acquiring it.
     */
    public boolean isLocked(Long eventId, Long seatId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(buildKey(eventId, seatId)));
    }

    private String buildKey(Long eventId, Long seatId) {
        return "seat:" + eventId + ":" + seatId;
    }
}