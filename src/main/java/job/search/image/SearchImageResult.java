package job.search.image;

public class SearchImageResult {
    private String fileName;
    private String histogram;
    private String imageBase64;

    public SearchImageResult() {
    }

    public SearchImageResult(String fileName, String histogram, String imageBase64) {
        this.fileName = fileName;
        this.histogram = histogram;
        this.imageBase64 = imageBase64;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getHistogram() {
        return histogram;
    }

    public void setHistogram(String histogram) {
        this.histogram = histogram;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
