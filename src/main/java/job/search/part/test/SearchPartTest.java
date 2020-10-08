package job.search.part.test;

import java.io.IOException;

public class SearchPartTest {
    public static void main(String[] args) throws IOException {
        //BasicConfigurator.configure();
        //图片转换二进制再转回来，放在ImgGray类获取像素矩阵

//        BASE64Decoder decoder = new BASE64Decoder();
//        ImageGray imageGray = new ImageGray(new File("D:\\QQData\\data\\1.bmp"));
//        String str = imageGray.getImageBytes();
//        ByteArrayInputStream partimg = new ByteArrayInputStream(decoder.decodeBuffer(str));
//        BufferedImage imgpart = ImageIO.read(partimg);
//        ImageGray partimage = new ImageGray(imgpart);
//        File file = new File("D:\\test1.txt");
//        FileWriter out = new FileWriter(file);
//        for(int i=0;i<Integer.parseInt(partimage.getHeight());i++){
//            for(int j=0;j<Integer.parseInt(partimage.getWidth());j++){
//                out.write(partimage.getGrayMatrix()[i][j]+"\t");
//            }
//            out.write("\r\n");
//        }
//        out.close();
//
//        //直接用本地路径的图片创建ImgGray类，不用二进制转换
//
//        ImageGray imageGray1 = new ImageGray(new File("D:\\QQData\\data\\1.bmp"));
//        File file1 = new File("D:\\test2.txt");
//        FileWriter out1 = new FileWriter(file1);
//        for(int i=0;i<Integer.parseInt(imageGray1.getHeight());i++){
//            for(int j=0;j<Integer.parseInt(imageGray1.getWidth());j++){
//                out1.write(imageGray1.getGrayMatrix()[i][j]+"\t");
//            }
//            out1.write("\r\n");
//        }
//        out1.close();
//        ImageGray imageGray = new ImageGray(new File("D:\\QQData\\data\\partexample\\1.bmp"));
//        String submitter = "le";
//        ImageGray imageGray = new ImageGray(new File(args[0]));
        String submitter = args[1];
//        System.out.println(imageGray.getgrayMatrixString()+"   ,"+imageGray.getGrayMatrix().length);
//        ImageGray imageGray = new ImageGray(new File("F:\\workspace\\common_files\\全图搜索样例\\样例一.bmp"));
//        String submitter = "Triol";
//        try {
//            SearchPartImageJob.doJob(submitter, ImageUtils.getImage(new File(args[0])));
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }

    }
}
