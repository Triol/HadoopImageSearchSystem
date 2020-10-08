package job.search.forgery;

import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import utils.GlobalEnv;
import utils.ImageUtils;
import org.apache.hadoop.hbase.client.Mutation;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchForgeryReducer  extends TableReducer<ImmutableBytesWritable, SearchForgeryMapperOutValue, ImmutableBytesWritable> {

    @Override
    protected void reduce(ImmutableBytesWritable key, Iterable<SearchForgeryMapperOutValue> value, Context context)
            throws IOException, InterruptedException {

        ArrayList<SearchForgeryResult> list = new ArrayList<SearchForgeryResult>();
        String result;
        for(SearchForgeryMapperOutValue v : value) {
            if(v.isEmpty())continue;
            list.add(
                    new SearchForgeryResult(    v.getFileNmae().toString(),
                                                v.getRate().get(),
                                                v.getTargetImageBytes().getBytes(),
                                                v.getCompareImageBytes().getBytes())
            );
        }

        Collections.sort(list, new Comparator<SearchForgeryResult>() {
            @Override
            public int compare(SearchForgeryResult o1, SearchForgeryResult o2) {
                return Double.compare(o1.getRate(), o2.getRate());
            }
        });
        //left at most 3 result
        while(list.size() > 3) {
            list.remove(list.size() - 1);
        }
//        result = JSONObject.toJSONString(list);
//
//        if("[]".equals(value)) {
//            result = "Not Found";
//        }
        Configuration conf = context.getConfiguration();
        String taskName = conf.get("taskName");
        String outputPath = conf.get("outputPath");
        String outputResultInfoPath = outputPath + "/" + taskName + ".txt";
        String message = "[task " + taskName + "] : complete\n found " + list.size() + " image" + (list.size() <= 1 ? "\n" : "s\n");
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputResultInfoPath));
            bufferedWriter.write(message);
            for(SearchForgeryResult res : list) {
                bufferedWriter.write("[" + res.getFileName() + "][rate = " + res.getRate() + "]\n");
            }
            bufferedWriter.close();
            for(SearchForgeryResult res : list) {
                String originalImagePath = outputPath + "/" + taskName + "-original-" + res.getFileName();
                ImageUtils.writeToFile(ImageUtils.fromBytes(res.getTargetImageBytes()), "bmp", originalImagePath);
                String compareImagePath = outputPath + "/" + taskName +  "-compare-" + res.getFileName();
                compareImagePath = compareImagePath.replace(".bmp", ".png");
                ImageUtils.writeToFile(ImageUtils.fromBytes(res.getCompareImageBytes()), "png", compareImagePath);
            }

        }catch (Exception e) {
            System.out.println("文件创建失败");
        }

        //System.out.println("result:\n" + result);
//        Configuration conf = context.getConfiguration();
//        //String taskName = conf.get("taskName");
//        String rowKey = conf.get("rowKey");
//        //String orginalImage = conf.get("original image base64");
//        String finishTime = GlobalEnv.getCurrentTime();
//
//        Put put = new Put(rowKey.getBytes());
//        put.addColumn("Info".getBytes(), "status".getBytes(), "DONE".getBytes());
//        put.addColumn("Info".getBytes(), "result".getBytes(), result.getBytes());
//        put.addColumn("Info".getBytes(), "finish_time".getBytes(), finishTime.getBytes());
//
//        try {
//            context.write(new ImmutableBytesWritable(rowKey.getBytes()), put);
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
