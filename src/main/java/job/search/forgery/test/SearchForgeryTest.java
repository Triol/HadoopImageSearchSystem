package job.search.forgery.test;

public class SearchForgeryTest {
    public static void main(String[] args) throws Exception{
        //BasicConfigurator.configure();
//        String path = "F:\\workspace\\common_files\\SAME\\220 - 副本.bmp";
//        String submitter = "Triol";
        //        BasicConfigurator.configure();
        String path = "F:\\workspace\\common_files\\图像篡改检查样例\\样例一.bmp";
        String submitter = "Triol";
        if(args.length != 2) {
            System.out.println("Usage: java -jar Hadoop_Exp.jar <path> <submitter>");

            //return;
        }else {
            path = args[0];
            submitter = args[1];
        }
//            System.out.println("start test of 1000 image compare forgery");
//            String b64 = new ImageGray(new File(path)).getImageBytes();
//            long st = System.currentTimeMillis();
//
//            BufferedImage bi = ImageUtils.fromBase64(b64);
//            long ed = System.currentTimeMillis();
//            System.out.println("##base64-translate time:" + (ed - st) + "ms");
//            st = System.currentTimeMillis();
//            for(int i = 0; i < 1; i++) {
//                ImageUtils.compareImage(bi, bi, 4, 32);
//            }
//            ed = System.currentTimeMillis();
//            System.out.println("##cost-time:" + (ed - st) + "ms");
//            System.exit(0);
//        }
//        ImageGray imageGray = new ImageGray(new File(path));
        //String submitter = args[1];
////        ImageGray imageGray = new ImageGray(new File(path));
//        try {
//            SearchForgeryJob.doJob(submitter, ImageUtils.getImage(new File(path)));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }
}
