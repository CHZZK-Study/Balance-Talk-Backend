package balancetalk.file.domain;

public enum FileFormat {
    JPEG("image/jpeg", "jpeg"),
    PNG("image/png", "png");

    private final String mimeType;
    private final String fileExtension;

    FileFormat(String mimeType, String fileExtension) {
        this.mimeType = mimeType;
        this.fileExtension = fileExtension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
