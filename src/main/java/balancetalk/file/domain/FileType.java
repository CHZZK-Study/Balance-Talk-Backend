package balancetalk.file.domain;

public enum FileType {
    TALK_PICK("talk-pick/"),
    GAME("balance-game/"),
    MEMBER("member/");

    private final String uploadDir;

    FileType(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getUploadDir() {
        return uploadDir;
    }
}
