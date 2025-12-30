package org.example.playground.global.oauth2;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EnableScheduling
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
        String randomCode = UUID.randomUUID().toString();
        codeStore.put(randomCode, CodeInfo.create(userId, roles));
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
