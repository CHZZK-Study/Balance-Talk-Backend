package balancetalk.global.caffeine;

import lombok.Getter;

@Getter
public enum CacheType {

    RefreshToken("refreshToken", 604800000, 10000);

    private String cacheName;
    private int expiredAfterWrite;
    private int maximumSize;

    CacheType(String cacheName, int expiredAfterWrite, int maximumSize) {
        this.cacheName = cacheName;
        this.expiredAfterWrite = expiredAfterWrite;
        this.maximumSize = maximumSize;
    }
}
