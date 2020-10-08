package job.search.image;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static org.apache.curator.shaded.com.google.common.primitives.Ints.min;

public class SearchImageMapperOutValue implements Writable {
    private Text fileName;
    private Text histogram;
    private Text imageBase64;

    private static int bufferSize = 6000;
    public SearchImageMapperOutValue() {
        fileName = new Text();
        histogram = new Text();
        imageBase64 = new Text();
    }
    public boolean isEmpty() {
        return fileName.getLength() == 0;
    }
    public SearchImageMapperOutValue(Text fileName, Text histogram, Text imageBase64) {
        this.fileName = fileName;
        this.histogram = histogram;
        this.imageBase64 = imageBase64;
    }

    //    public SearchImageMapperOutValue(String fileName, String histogram, String imageBase64) {
//        this.fileName = fileName;
//        this.histogram = histogram;
//        this.imageBase64 = imageBase64;
//
//    }
    public SearchImageMapperOutValue(String fileName, String histogram, String imageBase64) {
        this.fileName = new Text(fileName);
        this.histogram = new Text(histogram);
        this.imageBase64 = new Text(imageBase64);
    }
    public static SearchImageMapperOutValue getInstance(String fileName, String histogram, String imageBase64) {
        return new SearchImageMapperOutValue(fileName, histogram, imageBase64);
    }
    public void write(DataOutput dataOutput) throws IOException {
        fileName.write(dataOutput);
        histogram.write(dataOutput);
        imageBase64.write(dataOutput);
    }

    public void readFields(DataInput dataInput) throws IOException {
        fileName.readFields(dataInput);
        histogram.readFields(dataInput);
        imageBase64.readFields(dataInput);

    }

    public Text getFileName() {
        return fileName;
    }

    public void setFileName(Text fileName) {
        this.fileName = fileName;
    }

    public Text getHistogram() {
        return histogram;
    }

    public void setHistogram(Text histogram) {
        this.histogram = histogram;
    }

    public Text getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(Text imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public static int getBufferSize() {
        return bufferSize;
    }

    public static void setBufferSize(int bufferSize) {
        SearchImageMapperOutValue.bufferSize = bufferSize;
    }
}
