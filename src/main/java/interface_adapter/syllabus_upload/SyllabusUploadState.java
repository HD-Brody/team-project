package interface_adapter.syllabus_upload;

public class SyllabusUploadState {
    private String filePath = "";
    private String error = null;
    private boolean isProcessing = false;

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    
    public boolean isProcessing() { return isProcessing; }
    public void setProcessing(boolean processing) { isProcessing = processing; }
}