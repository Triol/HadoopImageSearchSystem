package job.search.forgery;

import com.sun.tools.javac.util.Pair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import utils.GlobalEnv;
import utils.ImageUtils;
import utils.MyRectangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class SearchForgeryMapper extends TableMapper<ImmutableBytesWritable, SearchForgeryMapperOutValue> {

    @Override
    public void map(ImmutableBytesWritable key, Result value, Context context)
            throws IOException, InterruptedException {

        byte[] family = Bytes.toBytes("Img_Info");
        byte[] histogramQualifier = Bytes.toBytes("I_histogram");
        byte[] imageQualifier = Bytes.toBytes("I_img");
        byte[] fileNameQualifier = Bytes.toBytes("I_name");

        //String targetHistogram = Bytes.toString(value.getValue(family, histogramQualifier));
        byte[] targetImageBytes = value.getValue(family, imageQualifier);
        byte[] targetFileName = value.getValue(family, fileNameQualifier);

        Configuration conf = context.getConfiguration();
        //System.out.println("map loaded values");
        byte[] rowKey;
        try {
            rowKey = conf.get("rowKey").getBytes();
            long st = System.currentTimeMillis();
            byte[] originalImageBytes = GlobalEnv.fromBase64(conf.get("forgeryImage base64"));
            long ed = System.currentTimeMillis();
            long st_1 = System.currentTimeMillis();
            Pair<Double, ArrayList<MyRectangle>> compareResult = ImageUtils.compareImage(originalImageBytes,
                                                                                        targetImageBytes,
                                                                                2, 32);
            long ed_1 = System.currentTimeMillis();
            if(compareResult.fst <= 0.7) {
                System.out.println(new String(targetFileName)+ ":rate=" + compareResult.snd.toString() +
                                    ";rectangle size =" + compareResult.snd.size());
                BufferedImage compareImage = ImageUtils.writeRectangle(
                                                    ImageUtils.fromBytes(originalImageBytes),
                                                    compareResult.snd);
                SearchForgeryMapperOutValue outValue =
                        new SearchForgeryMapperOutValue(
                                targetFileName,
                                compareResult.fst,
                                targetImageBytes,
                                ImageUtils.toBytes(compareImage));
                context.write(new ImmutableBytesWritable(rowKey), outValue);
            }else {
                context.write(new ImmutableBytesWritable(rowKey), new SearchForgeryMapperOutValue());
            }
            System.out.println( "Get rowKey = " + new String(key.get()) +
                                "\tbase64 decode cost " + (ed - st) + "ms" +
                                "\tcompare cost " + (ed_1 - st_1) + "ms");
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}
