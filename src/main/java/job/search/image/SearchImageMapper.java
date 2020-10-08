package job.search.image;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;


import java.io.IOException;

public class SearchImageMapper extends TableMapper<ImmutableBytesWritable, SearchImageMapperOutValue> {
    @Override
    public void map(ImmutableBytesWritable key, Result value, Context context)
            throws IOException, InterruptedException {

        byte[] family = Bytes.toBytes("Img_Info");
        byte[] histogramQualifier = Bytes.toBytes("I_histogram");
        byte[] imageQualifier = Bytes.toBytes("I_img");
        byte[] fileNameQualifier = Bytes.toBytes("I_name");

        String targetHistogram = new String(value.getValue(family, histogramQualifier));
//        byte[] targetImageBytes = value.getValue(family, imageQualifier);
        String targetFileName = Bytes.toString(value.getValue(family, fileNameQualifier));
        Configuration conf = context.getConfiguration();
        String originalHistogram = null;
        String originalBase64 = null;
        byte[] rowKey = new byte[5];
        try {
            originalHistogram = conf.get("original image histogram");
            originalBase64 = conf.get("original image base64");
            rowKey = conf.get("rowKey").getBytes();
//            System.out.println("Get rowKey = " + new String(rowKey));
        }catch (Exception e) {
            e.printStackTrace();
        }

        if(originalHistogram != null
                && originalHistogram.hashCode() == targetHistogram.hashCode()
                && originalHistogram.equals(targetHistogram)) {
//            if(originalBase64 != null
//                &&  originalBase64.hashCode() == targetImageBase64.hashCode()
//                && originalBase64.equals(targetImageBase64)) {
            System.out.println("Now in [found]:" + targetFileName);
//            System.out.println(targetImageBytes);
            try {
                context.write(new ImmutableBytesWritable(rowKey),
                                SearchImageMapperOutValue.getInstance(targetFileName, targetHistogram, originalBase64));
//                System.out.println("map write in " + targetFileName);
            }catch (Exception e) {
                System.out.println(e.toString());
            }
        }else {
            context.write(new ImmutableBytesWritable(rowKey), new SearchImageMapperOutValue());
//            System.out.println("Now in [not-found]:" + targetFileName);
        }
    }


}
