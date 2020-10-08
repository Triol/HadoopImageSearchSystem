package job.search.part;

public class SearchPartImageResult {
    private String fileName;
//    private String histogram;
    private byte[] imageBytes;
//    private String partimageBase64;
    private byte[] resimageBytes;

    public SearchPartImageResult() {
    }

    public SearchPartImageResult(String fileName, byte[] imageBytes, byte[] resimageBytes) {
        this.fileName = fileName;
        this.imageBytes = imageBytes;
        this.resimageBytes = resimageBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public byte[] getResimageBytes() {
        return resimageBytes;
    }

    public void setResimageBytes(byte[] resimageBytes) {
        this.resimageBytes = resimageBytes;
    }
}
