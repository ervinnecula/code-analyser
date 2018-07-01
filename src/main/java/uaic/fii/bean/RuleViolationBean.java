package uaic.fii.bean;

public class RuleViolationBean {
    private String message;
    private String description;
    private String externalInfoUrl;
    private String priority;
    private String fileName;
    private String methodName;
    private String className;
    private int beginLine;
    private int endLine;

    public RuleViolationBean(String message, String description, String externalInfoUrl, String priority,
                             String fileName, String methodName, String className, int beginLine, int endLine) {
        this.message = message;
        this.description = description;
        this.externalInfoUrl = externalInfoUrl;
        this.priority = priority;
        this.fileName = fileName;
        this.methodName = methodName;
        this.className = className;
        this.beginLine = beginLine;
        this.endLine = endLine;
    }

    public RuleViolationBean() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExternalInfoUrl() {
        return externalInfoUrl;
    }

    public void setExternalInfoUrl(String externalInfoUrl) {
        this.externalInfoUrl = externalInfoUrl;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getBeginLine() {
        return beginLine;
    }

    public void setBeginLine(int beginLine) {
        this.beginLine = beginLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }
}
