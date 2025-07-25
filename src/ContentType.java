public enum ContentType {
    ImageXIcon("image/x-icon"),
    JavaScript("text/javascript"),
    HTML("text/html"),
    StyleSheet("text/css"),
    JSON("application/json"),
    AudioMpeg("audio/mpeg"),
    TextPlain("text/plain");

    private final String typeHeader;

    ContentType(String typeHeader) {
        this.typeHeader = typeHeader;
    }

    public String getContentType() {
        return typeHeader;
    }
}
