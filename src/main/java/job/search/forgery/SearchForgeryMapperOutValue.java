package job.search.forgery;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SearchForgeryMapperOutValue implements Writable {
    private Text fileNmae;
    private DoubleWritable rate;
    private Text targetImageBytes;
    private Text compareImageBytes;
    public boolean isEmpty() {
        return fileNmae.getLength() == 0;
    }
    public SearchForgeryMapperOutValue(Text fileNmae, DoubleWritable rate, Text targetImageBytes, Text compareImageBytes) {
        this.fileNmae = fileNmae;
        this.rate = rate;
        this.targetImageBytes = targetImageBytes;
        this.compareImageBytes = compareImageBytes;
    }
    public SearchForgeryMapperOutValue(String fileName, double rate, String targetImageBytes, String compareImageBytes) {
        this.fileNmae = new Text(fileName);
        this.rate = new DoubleWritable(rate);
        this.targetImageBytes = new Text(targetImageBytes);
        this.compareImageBytes = new Text(compareImageBytes);
    }
    public SearchForgeryMapperOutValue(byte[] fileName, double rate, byte[] targetImageBytes, byte[] compareImageBytes) {
        this.fileNmae = new Text(fileName);
        this.rate = new DoubleWritable(rate);
        this.targetImageBytes = new Text(targetImageBytes);
        this.compareImageBytes = new Text(compareImageBytes);
    }
    public SearchForgeryMapperOutValue() {
        fileNmae = new Text();
        rate = new DoubleWritable();
        targetImageBytes = new Text();
        compareImageBytes = new Text();
    }

    public void write(DataOutput dataOutput) throws IOException {
        fileNmae.write(dataOutput);
        rate.write(dataOutput);
        targetImageBytes.write(dataOutput);
        compareImageBytes.write(dataOutput);
    }

    public void readFields(DataInput dataInput) throws IOException {
        fileNmae.readFields(dataInput);
        rate.readFields(dataInput);
        targetImageBytes.readFields(dataInput);
        compareImageBytes.readFields(dataInput);
    }

    public Text getFileNmae() {
        return fileNmae;
    }

    public void setFileNmae(Text fileNmae) {
        this.fileNmae = fileNmae;
    }

    public DoubleWritable getRate() {
        return rate;
    }

    public void setRate(DoubleWritable rate) {
        this.rate = rate;
    }

    public Text getTargetImageBytes() {
        return targetImageBytes;
    }

    public void setTargetImageBytes(Text targetImageBytes) {
        this.targetImageBytes = targetImageBytes;
    }

    public Text getCompareImageBytes() {
        return compareImageBytes;
    }

    public void setCompareImageBytes(Text compareImageBytes) {
        this.compareImageBytes = compareImageBytes;
    }
}
