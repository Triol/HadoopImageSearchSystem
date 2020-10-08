package job.search.part;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import sun.misc.BASE64Decoder;
import utils.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.ExecutionException;
public class SearchPartImageMapper extends TableMapper<ImmutableBytesWritable, MapperOutValue> {
    @Override
    public void map(ImmutableBytesWritable key, Result value, Context context)
            throws IOException, InterruptedException {
        byte[] family = Bytes.toBytes("Img_Info");
//        byte[] histogramQualifier = Bytes.toBytes("I_histogram");
        byte[] imageQualifier = Bytes.toBytes("I_img");//二进制图片
        byte[] fileNameQualifier = Bytes.toBytes("I_name");

//        String targetHistogram = Bytes.toString(value.getValue(family, histogramQualifier));
        byte[] targetImageBytes = value.getValue(family, imageQualifier);
        byte[] targetFileName = value.getValue(family, fileNameQualifier);
        Configuration conf = context.getConfiguration();
//        String originalHistogram = new String();
        byte[] rowKey = new byte[5];
        try {
//            originalHistogram = conf.get("original image histogram");
            rowKey = conf.get("rowKey").getBytes();
//            System.out.println("Get rowKey = " + rowKey.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //1、获取hbase原图
        BufferedImage imgtotal = ImageUtils.fromBytes(targetImageBytes);
        //2、获取特征样例
        String partImageBase64 = conf.get("original image base64");
        BufferedImage imgpart = ImageUtils.fromBase64(partImageBase64);
        //初始化多线程
        PartImgUtils part = new PartImgUtils(imgtotal, imgpart);
        //坐标字符串
        String findpartres = "";
        try {
            long st = System.currentTimeMillis();
            findpartres = part.threadFind();
            long ed = System.currentTimeMillis();
            System.out.println("in " + new String(targetFileName) + "\tfind feature cost " + (ed - st) + "ms");
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (!findpartres.equals("")) {
            //对找到的原图进行修改
//            String targetresImageBase64 = targetImageBytes;
//            ByteArrayInputStream resimg = new ByteArrayInputStream(decoder.decodeBuffer(targetresImageBase64));
//            BufferedImage imgres = ImageIO.read(resimg);
            String[] coordinates = findpartres.split(",");
            System.out.println("find in" + new String(targetFileName));
            MyRectangle result = new MyRectangle(
                    Integer.parseInt(coordinates[0]),
                    Integer.parseInt(coordinates[2]),
                    Integer.parseInt(coordinates[1]),
                    Integer.parseInt(coordinates[3])
            );
            BufferedImage image3 = null;
            try {
                image3 = ImageUtils.writeRectangle(ImageUtils.deepCopy(imgtotal), result);
//                ImageGray resimage = new ImageGray(image3);
                byte[] targetresImageBytes = ImageUtils.toBytes(image3);
                context.write(new ImmutableBytesWritable(rowKey), new MapperOutValue( targetFileName, targetImageBytes, targetresImageBytes));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            context.write(new ImmutableBytesWritable(rowKey), new MapperOutValue());
        }

    }
}
