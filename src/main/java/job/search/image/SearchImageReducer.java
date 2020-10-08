package job.search.image;

import com.alibaba.fastjson.JSONObject;
import job.search.forgery.SearchForgeryResult;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import utils.GlobalEnv;
import utils.ImageUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SearchImageReducer extends
        TableReducer<ImmutableBytesWritable, SearchImageMapperOutValue, ImmutableBytesWritable> {

    @Override
    protected void reduce(ImmutableBytesWritable key, Iterable<SearchImageMapperOutValue> value, Context context)
            throws IOException, InterruptedException {
//        for(SearchForgeryMapperOutValue mv : value) {
//            System.out.println(mv.getTargetImageBytes());
//        }
        ArrayList<SearchImageResult> list = new ArrayList<SearchImageResult>();
        for(SearchImageMapperOutValue v : value) {
            if(v.isEmpty())continue;
            list.add(new SearchImageResult(v.getFileName().toString(), v.getHistogram().toString(), v.getImageBase64().toString()));
        }
//        System.out.println("reduce start in" + key);
//        String result = "[]";
//        try {
//            result = JSONObject.toJSONString(resultList);
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if("[]".equals(value)) {
//            result = "Not Found";
//        }
//        for(SearchForgeryMapperOutValue mv : value) {
//            System.out.println(mv.getTargetImageBytes());
//        }
//        System.out.println("result:\n" + result);

        Configuration conf = context.getConfiguration();
        String taskName = conf.get("taskName");
        String outputPath = conf.get("outputPath");
        String outputResultInfoPath = outputPath + "/" + taskName + ".txt";
        String message = "[task " + taskName + "] : complete\n found " + list.size() + " image" + (list.size() <= 1 ? "\n" : "s\n");
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputResultInfoPath));
            bufferedWriter.write(message);
            for(SearchImageResult res : list) {
                bufferedWriter.write("[" + res.getFileName() + "]\n");
            }
            bufferedWriter.close();
        }catch (Exception e) {
            System.out.println("文件创建失败");
        }
        //String submitter = conf.get("submitter");
//        String rowKey = conf.get("rowKey");
//        //String orginalImage = conf.get("original image base64");
//        String finishTime = GlobalEnv.getCurrentTime();

//        Put put = new Put(rowKey.getBytes());
//        put.addColumn("Info".getBytes(), "status".getBytes(), "DONE".getBytes());
//        put.addColumn("Info".getBytes(), "result".getBytes(), result.getBytes());
//        put.addColumn("Info".getBytes(), "finish_time".getBytes(), finishTime.getBytes());
//
//        try {
//            context.write(new ImmutableBytesWritable(rowKey.getBytes()), put);
//        }catch (Exception e) {
//            //e.printStackTrace();
//            System.out.println(e.toString());
//        }
    }
}
