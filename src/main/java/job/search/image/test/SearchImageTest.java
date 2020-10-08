package job.search.image.test;

import org.apache.log4j.BasicConfigurator;

public class SearchImageTest {
    public static void main(String[] args) {
        BasicConfigurator.configure();
//        if(args.length != 2) {
//            System.out.println("Usage: java -jar Hadoop_Exp.jar <path> <submitter>");
//            return;
//        }
//        ImageGray imageGray = new ImageGray(new File(args[0]));
//        String submitter = args[1];
//        ImageGray imageGray = new ImageGray(new File("F:\\workspace\\common_files\\全图搜索样例\\样例一.bmp"));
//        String submitter = "Triol";
        String submitter = "Triol";
        String path = "F:\\workspace\\common_files\\BOSSBASE\\1.bmp";
        if(args.length == 2) {
            path = args[0];
            submitter = args[1];
        }
//
//        try {
//            SearchImageJob.doJob(submitter, ImageUtils.getImage(new File(path)));
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }

    }
}
