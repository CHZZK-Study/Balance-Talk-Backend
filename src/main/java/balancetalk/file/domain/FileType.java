package balancetalk.file.domain;

public enum FileType {
    TALK_PICK("talks/", 10),
    TEMP_TALK_PICK("temp-talks/", 10),
    GAME("games/", 1),
    TEMP_GAME("temp-games/", 10),
    MEMBER("members/", 1);

    private final String uploadDir;
    private final int maxCount;

    FileType(String uploadDir, int maxCount) {
        this.uploadDir = uploadDir;
        this.maxCount = maxCount;
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public int getMaxCount() {
        return maxCount;
    }
}
