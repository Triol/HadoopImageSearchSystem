package utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Scan;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GlobalEnv {

    public static Configuration getConf() {
        Configuration conf = new Configuration();
        conf.addResource("hbase-site.xml");
        conf.set("fs.defaultFS","hdfs://master:9000/");
        conf.set("hbase.zookeeper.quorum", "master");
        conf.set("mapreduce.app-submission.cross-platform", "true");
        conf.set("yarn.resourcemanager.hostname","master");
        conf.set("dfs.client.use.datanode.hostname", "true");
        return conf;
    }
    public static Scan getScan() {
        Scan scan = new Scan();
        scan.setCacheBlocks(false);
        scan.setCaching(500);
        return scan;
    }
    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatter.format(new Date());
    }
    public static String getUnderLineTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        return formatter.format(new Date());
    }
    public static String getCurrentTime(String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(new Date());
    }
    public static String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    public static String toBase64(byte[] bytes) {
        return new BASE64Encoder().encode(bytes);
    }

    public static String toBase64(String s) {
        return toBase64(s.getBytes());
    }
    public static byte[] fromBase64(String base64) throws IOException {
        return new BASE64Decoder().decodeBuffer(base64);
    }
}
