package org.webserver.dto.request;

public class FileContentRequest {
    private int part;
    private String fileContent;

    public FileContentRequest(int part, String fileContent) {
        this.part = part;
        this.fileContent = fileContent;
    }

    public FileContentRequest() {
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
}
