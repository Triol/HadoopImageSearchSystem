package job.search.image;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.*;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import utils.GlobalEnv;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.mapreduce.Job;
import utils.ImageUtils;

import java.awt.image.BufferedImage;


public class SearchImageJob {
    public static void doJob(String taskName, BufferedImage image, String outputPath) throws Exception {
        Configuration conf = GlobalEnv.getConf();
        Scan scan = GlobalEnv.getScan();

        String imageBase64Str = ImageUtils.toBase64(image);
        conf.set("original image base64", imageBase64Str);
        conf.set("original image histogram", ImageUtils.getHistogramString(image));
        conf.set("taskName", taskName);
        conf.set("outputPath", outputPath);
        String submit_time = GlobalEnv.getCurrentTime();
        conf.set("submit_time", submit_time);
        String rowkey = GlobalEnv.reverse(submit_time) + taskName;
        conf.set("rowKey", rowkey);
        System.out.println("Set rowKey = " + rowkey);
        Job job = Job.getInstance(conf, "search image");
        job.setOutputKeyClass(ImmutableBytesWritable.class);
        job.setOutputValueClass(Put.class);

        TableMapReduceUtil.initTableMapperJob(
                "Images",
                scan,
                SearchImageMapper.class,
                ImmutableBytesWritable.class,
                SearchImageMapperOutValue.class,
                job
        );
        job.setReducerClass(SearchImageReducer.class);
        FileOutputFormat.setOutputPath(job, new Path("/searchImageJob/" + taskName + "-" + GlobalEnv.getUnderLineTime() + "reduce.txt"));
//
//        TableMapReduceUtil.initTableReducerJob(
//                "SearchJob",
//                SearchImageReducer.class,
//                job
//        );
//        HbaseClient.init();
//        String[] fields = {"Info:taskName", "Info:submit_time", "Info:status", "Info:original_image", "Info:job_type"};
//        String[] values= {taskName, submit_time, "WAITING", imageBase64Str, "Search Image"};
//        HbaseClient.addRecord("SearchJob", rowkey, fields, values);
        long st = System.currentTimeMillis();
        boolean status = job.waitForCompletion(true);
        long ed = System.currentTimeMillis();
        job.close();
        System.out.println((status ? "[ok]" : "[fail]") + "[search image job] cost " + (ed -st) / 1000 + "s");
    }
}
