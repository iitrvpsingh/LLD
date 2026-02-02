package votingsystem_distributed.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simulates distributed locking (like Redis Redlock)
 * In production: Use Redis with Redisson library
 * 
 * Example with Redisson:
 * RLock lock = redisson.getLock("vote_lock:" + voterId);
 * lock.lock(10, TimeUnit.SECONDS);
 * try {
 *     // Critical section
 * } finally {
 *     lock.unlock();
 * }
 */
public class DistributedLock {
    // Simulates distributed lock storage (like Redis)
    private static final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    /**
     * Try to acquire lock with timeout
     * @param lockKey Unique key for the lock
     * @param timeout Timeout in seconds
     * @return true if lock acquired, false otherwise
     */
    public boolean tryLock(String lockKey, long timeout, TimeUnit unit) {
        ReentrantLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantLock());
        try {
            return lock.tryLock(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Acquire lock (blocking)
     */
    public void lock(String lockKey) {
        ReentrantLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantLock());
        lock.lock();
    }

    /**
     * Release lock
     */
    public void unlock(String lockKey) {
        ReentrantLock lock = locks.get(lockKey);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * Check if lock is currently held
     */
    public boolean isLocked(String lockKey) {
        ReentrantLock lock = locks.get(lockKey);
        return lock != null && lock.isLocked();
    }
}
