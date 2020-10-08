package job.search.part;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MapperOutValue implements Writable {
    private Text fileName;
    private Text imageBytes;
    private Text resultImageBytes;

    private static int bufferSize = 6000;

    public boolean isEmpty() {
        return fileName.getLength() == 0;
    }
    public MapperOutValue() {
        fileName = new Text();
        imageBytes = new Text();
        resultImageBytes = new Text();
    }

    public MapperOutValue(Text fileName, Text imageBytes, Text resultImageBytes) {
        this.fileName = fileName;
        this.imageBytes = imageBytes;
        this.resultImageBytes = resultImageBytes;
    }


    public MapperOutValue(String fileName, String imageBytes, String resultImageBytes) {
        this.fileName = new Text(fileName);
        this.imageBytes = new Text(imageBytes);

        this.resultImageBytes = new Text(resultImageBytes);
    }
    public MapperOutValue(byte[] fileName, byte[] imageBytes, byte[] resultImageBytes) {
        this.fileName = new Text(fileName);
        this.imageBytes = new Text(imageBytes);
        this.resultImageBytes = new Text(resultImageBytes);

    }
    public static MapperOutValue getInstance(String fileName, String imageBase64, String resimageBase64) {
        return new MapperOutValue(fileName, imageBase64, resimageBase64);
    }
    public void write(DataOutput dataOutput) throws IOException {
        fileName.write(dataOutput);
        imageBytes.write(dataOutput);
        resultImageBytes.write(dataOutput);
    }

    public void readFields(DataInput dataInput) throws IOException {
        fileName.readFields(dataInput);
        imageBytes.readFields(dataInput);
        resultImageBytes.readFields(dataInput);
    }
    public Text getFileName() {
        return fileName;
    }

    public void setFileName(Text fileName) {
        this.fileName = fileName;
    }

    public Text getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(Text imageBytes) {
        this.imageBytes = imageBytes;
    }

    public Text getResultImageBytes() {
        return resultImageBytes;
    }
    public void setResultImageBytes(Text resultImageBytes) {
        this.resultImageBytes = resultImageBytes;
    }

    public static int getBufferSize() {
        return bufferSize;
    }

    public static void setBufferSize(int bufferSize) {
        MapperOutValue.bufferSize = bufferSize;
    }
}
