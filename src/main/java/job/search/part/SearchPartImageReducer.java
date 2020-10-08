package job.search.part;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import utils.ImageUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SearchPartImageReducer extends
        TableReducer<ImmutableBytesWritable, MapperOutValue, ImmutableBytesWritable> {

    @Override
    protected void reduce(ImmutableBytesWritable key, Iterable<MapperOutValue> value, Context context)
            throws IOException, InterruptedException {
        ArrayList<SearchPartImageResult> list = new ArrayList<SearchPartImageResult>();
        for(MapperOutValue v:value) {
            if(v.isEmpty())continue;
            list.add(new SearchPartImageResult(
                    v.getFileName().toString(),
                    v.getImageBytes().getBytes(),
                    v.getResultImageBytes().getBytes()
                    ));
        }
//        String result = new String();
//        result = JSONObject.toJSONString(resList);
        Configuration conf = context.getConfiguration();
        String taskName = conf.get("taskName");
        String outputPath = conf.get("outputPath");
        String outputResultInfoPath = outputPath + "/" + taskName + ".txt";
        String message = "[task " + taskName + "] : complete\n found " + list.size() + " image" + (list.size() <= 1 ? "\n" : "s\n");
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputResultInfoPath));
            bufferedWriter.write(message);
            for(SearchPartImageResult res : list) {
                bufferedWriter.write("[" + res.getFileName() + "]\n");
            }
            bufferedWriter.close();
            for(SearchPartImageResult res : list) {
                String originalImagePath = outputPath + "/" + taskName + "-original-" + res.getFileName();
                ImageUtils.writeToFile(ImageUtils.fromBytes(res.getImageBytes()), "bmp", originalImagePath);
                String compareImagePath = outputPath + "/" + taskName +  "-compare-" + res.getFileName();
                compareImagePath = compareImagePath.replace(".bmp", ".png");
                ImageUtils.writeToFile(ImageUtils.fromBytes(res.getResimageBytes()), "png", compareImagePath);
            }

        }catch (Exception e) {
            System.out.println("文件创建失败");
        }
//        String rowKey = conf.get("rowKey");
//        String finishTime = GlobalEnv.getCurrentTime();
//        Put put = new Put(rowKey.getBytes());
//        put.addColumn("Info".getBytes(), "status".getBytes(), "DONE".getBytes());
//        put.addColumn("Info".getBytes(), "result".getBytes(), result.getBytes());
//        put.addColumn("Info".getBytes(), "finish_time".getBytes(), GlobalEnv.getCurrentTime().getBytes());
//        try {
//            context.write(new ImmutableBytesWritable(rowKey.getBytes()), put);
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
