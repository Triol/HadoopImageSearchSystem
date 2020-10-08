package job.search.forgery;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.apache.hadoop.mapreduce.Job;
import utils.GlobalEnv;
import utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class SearchForgeryJob {
    public static void doJob(String taskName, BufferedImage bufferedImage, String outputPath) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = GlobalEnv.getConf();
        Scan scan = GlobalEnv.getScan();

        byte[] imageBytes = ImageUtils.toBytes(bufferedImage);
        conf.set("forgeryImage base64", GlobalEnv.toBase64(imageBytes));

        conf.set("forgeryImage histogram", ImageUtils.getHistogramString(bufferedImage));
        conf.set("taskName", taskName);
        conf.set("outputPath", outputPath);
        String submit_time = GlobalEnv.getCurrentTime();
        conf.set("submit_time", submit_time);
        String rowkey = GlobalEnv.reverse(submit_time) + taskName;
        conf.set("rowKey", rowkey);
        System.out.println("Set rowKey = " + rowkey);
        Job job = Job.getInstance(conf, "search forgeryImage");
        job.setOutputKeyClass(ImmutableBytesWritable.class);
        job.setOutputValueClass(Put.class);

        TableMapReduceUtil.initTableMapperJob(
                "Images",
                scan,
                SearchForgeryMapper.class,
                ImmutableBytesWritable.class,
                SearchForgeryMapperOutValue.class,
                job
        );
        job.setReducerClass(SearchForgeryReducer.class);
        FileOutputFormat.setOutputPath(job, new Path("/searchForgeryJob/" + taskName + "-" + GlobalEnv.getUnderLineTime() + "reduce.txt"));
//        TableMapReduceUtil.initTableReducerJob(
//                "SearchJob",
//                SearchForgeryReducer.class,
//                job
//        );

//        HbaseClient.init();
//        String[] fields = {"Info:taskName", "Info:submit_time", "Info:status", "Info:original_image", "Info:job_type"};
//        byte[][] values = {taskName.getBytes(), submit_time.getBytes(), "WAITING".getBytes(), imageBytes, "Search Forgery".getBytes()};
//        HbaseClient.addRecord("SearchJob", rowkey, fields, values);
        long st = System.currentTimeMillis();
        boolean status = job.waitForCompletion(true);
        long ed = System.currentTimeMillis();
        System.out.println((status ? "[ok]" : "[fail]") + "[search forgery job] cost " + (ed - st) / 1000 + "s");
    }
}
