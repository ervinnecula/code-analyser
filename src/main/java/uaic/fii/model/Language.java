package uaic.fii.model;

public enum Language {
    CSS("css"),
    JAVA("java"),
    HTML("html"),
    JSP("jsp"),
    JS("js"),
    ASPX("aspx"),
    C("c"),
    CPP("cpp"),
    CS("cs"),
    PHP("php"),
    OBJECTIVEC("m"),
    PYTHON("py");

    private String extension;

    public String getExtension() {
        return extension;
    }

    Language(String extension) {
        this.extension = extension;
    }
}
