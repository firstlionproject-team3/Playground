package org.example.playground.global.oauth2.store;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EnableScheduling
@Slf4j
public class TempCodeStore {

    private final Map<String, CodeInfo> codeStore = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 60000)
    public void cleanExpiredCodes() {
        long now = System.currentTimeMillis();
        codeStore.entrySet().removeIf(entry ->
                now - entry.getValue().getCreatedAt() > 5 * 60 * 1000
        );
    }

    public String createCode(Long userId,  List<String> roles) {
        String randomCode;
        CodeInfo codeInfo = CodeInfo.create(userId, roles);
        int attempts = 0;
        do {
            randomCode = UUID.randomUUID().toString();
            if (++attempts > 10) {
                log.error("Failed to generate unique code for userId: {}", userId);
                throw new IllegalStateException("Failed to generate unique code");
            }
        } while (codeStore.putIfAbsent(randomCode, codeInfo) != null);

        if (attempts > 1) {
            log.warn("Code generation took {} attempts", attempts);
        }
        return randomCode;
    }

    public CodeInfo getCodeInfoAndRemove(String code) {
        CodeInfo info = codeStore.remove(code);
        if (info == null ||
                System.currentTimeMillis() - info.getCreatedAt() > 5 * 60 * 1000) {
            return null;
        }
        return info;
    }

    public int getSize() {
        return codeStore.size();
    }



}
