package job.search.image.test;

import job.search.image.SearchImageMapperOutValue;
import org.apache.hadoop.io.DataOutputBuffer;

import java.io.DataOutput;

public class MapperOutValueTest {
    public static void main(String[] args) {
        SearchImageMapperOutValue t = SearchImageMapperOutValue.getInstance("filename", "histogram", "iamgeBase64");
        SearchImageMapperOutValue s = new SearchImageMapperOutValue();
        DataOutput dataOutput = new DataOutputBuffer();
        try {
            t.write(dataOutput);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
