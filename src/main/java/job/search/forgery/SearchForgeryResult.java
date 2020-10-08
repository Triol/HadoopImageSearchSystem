package job.search.forgery;

public class SearchForgeryResult {
    private String fileName;
    private Double rate;
    private byte[] targetImageBytes;
    private byte[] compareImageBytes;

    public SearchForgeryResult(String fileName, Double rate, byte[] targetImageBytes, byte[] compareImageBytes) {
        this.fileName = fileName;
        this.rate = rate;
        this.targetImageBytes = targetImageBytes;
        this.compareImageBytes = compareImageBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public byte[] getTargetImageBytes() {
        return targetImageBytes;
    }

    public void setTargetImageBytes(byte[] targetImageBytes) {
        this.targetImageBytes = targetImageBytes;
    }

    public byte[] getCompareImageBytes() {
        return compareImageBytes;
    }

    public void setCompareImageBytes(byte[] compareImageBytes) {
        this.compareImageBytes = compareImageBytes;
    }
}
