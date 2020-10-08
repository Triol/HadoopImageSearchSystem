package job.upload;

import utils.HbaseClient;
import utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class UploadImages {
    public static void uploadImage(String path) throws IOException {
        //String path = "F:\\workspace\\common_files\\BOSSBASE";

        //HbaseClient.init();
        //System.out.println("Hbase initialized");
        File dir = new File(path);
        if(!dir.isDirectory()) {
            System.err.println("Not a directory");
        }else {
            File[] files = dir.listFiles();
            int cnt = 0;
            for(File f : files) {
                //ImageGray img = new ImageGray(f);
                //String res = img.getImageBytes();
                BufferedImage bi = ImageUtils.getImage(f);
                cnt++;
                String tableName = "Images";
                byte[] I_filename = (f.getName()).getBytes();
                byte[] I_img = ImageUtils.toBytes(bi);
                byte[] I_histogram = ImageUtils.getHistogramString(bi).getBytes();
                String[] fields = {"Img_Info:I_img", "Img_Info:I_name", "Img_Info:I_histogram"};
                byte[][] values = {I_img, I_filename, I_histogram};

                String rowKey = (new StringBuilder(String.valueOf(bi.hashCode())).reverse().toString() )+ "-" + new String(I_filename);
                HbaseClient.addRecord(tableName, rowKey, fields, values);
                System.out.println(new String(I_filename) + " OK " + cnt + "/" + files.length + ";rowkey = " + rowKey);



                //System.err.println("Error: " + f.getName());
            }
        }
    }
}
