package job.search.part;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import utils.GlobalEnv;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.mapreduce.Job;
import utils.ImageUtils;

import java.awt.image.BufferedImage;


public class SearchPartImageJob {
    public static void doJob(String taskName, BufferedImage image, String outputPath) throws Exception {
        Configuration conf = GlobalEnv.getConf();
        Scan scan = GlobalEnv.getScan();

        String imageBase64Str = ImageUtils.toBase64(image);
        conf.set("original image base64", imageBase64Str);
        conf.set("taskName", taskName);
        conf.set("outputPath", outputPath);
        String submit_time = GlobalEnv.getCurrentTime();
        conf.set("submit_time", submit_time);
        String rowkey = GlobalEnv.reverse(submit_time) + taskName;
        conf.set("rowKey", rowkey);
        Job job = Job.getInstance(conf, "search image");
        job.setOutputKeyClass(ImmutableBytesWritable.class);
        job.setOutputValueClass(Put.class);

        TableMapReduceUtil.initTableMapperJob(
                "Images",
                scan,
                SearchPartImageMapper.class,
                ImmutableBytesWritable.class,
                MapperOutValue.class,
                job
        );
        job.setReducerClass(SearchPartImageReducer.class);
        FileOutputFormat.setOutputPath(job, new Path("/searchPartJob/" + taskName + "-" + GlobalEnv.getUnderLineTime() + "reduce.txt"));
//
//        TableMapReduceUtil.initTableReducerJob(
//                "SearchJob",
//                SearchPartImageReducer.class,
//                job
//        );
//        job.setNumReduceTasks(1);
//        HbaseClient.init();
//        String[] fields = {"Info:taskName", "Info:submit_time", "Info:status", "Info:original_image", "Info:job_type"};
//        String[] values = {taskName, submit_time, "WAITING", imageBase64Str, "Search PartImage"};
//        HbaseClient.addRecord("SearchJob", rowkey, fields, values);
        long st = System.currentTimeMillis();
        boolean status = job.waitForCompletion(true);
        long ed = System.currentTimeMillis();
//        HbaseClient.close();
        System.out.println((status ? "[ok]" : "[fail]") + "[search part job] cost " + (ed - st) / 1000 + "s");
    }
}
