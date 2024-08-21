package balancetalk.file.domain;

public enum FileType {
    TALK_PICK("talk-pick/", 10),
    TEMP_TALK_PICK("temp-talk-pick/", 10),
    GAME("balance-game/", 1),
    MEMBER("member/", 1);

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
