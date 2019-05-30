package uaic.fii.model;

public enum StaticDetectionKind {
    BASIC("basic"),
    OPTIMIZATION("optimization"),
    COUPLING("coupling"),
    CODESIZE("codesize"),
    DESIGN("design");

    private String detectedKind;

    public String getDetectedKind() {
        return detectedKind;
    }

    StaticDetectionKind(String detectedKind) {
        this.detectedKind = detectedKind;
    }

}
