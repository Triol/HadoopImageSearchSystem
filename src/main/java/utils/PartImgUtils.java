package utils;


import java.util.concurrent.*;

import java.awt.image.BufferedImage;

public class PartImgUtils {
    private BufferedImage image1 = null;
    private BufferedImage image2 = null;
    private String coordinate = "";
    public PartImgUtils(BufferedImage image1, BufferedImage image2) {
        this.image1 = image1;
        this.image2 = image2;
    }

    private static class CompareImgCall implements Callable
    {
        private BufferedImage image1 = null;
        private BufferedImage image2 = null;
        private int start;
        private int end;

        public CompareImgCall(BufferedImage image1, BufferedImage image2, int start, int end) {
            this.image1 = image1;
            this.image2 = image2;
            this.start = start;
            this.end = end;
        }

        public Object call() throws Exception
        {
            return CompaseImg(this.image1, this.image2, this.start,this.end);
        }
    }


    public String threadFind() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CompareImgCall[] comparecall = new CompareImgCall[2];
        Future[] future = new Future[2];
        future[0] = pool.submit(new CompareImgCall(image1, image2, 0, 256 + 20));
        future[1] = pool.submit(new CompareImgCall(image1, image2, 256 - 20, 512));
//        for(int i=0;i<5;i++)
//        {
//            if(i==4)
//            {
//                comparecall[i] = new CompareImgCall(image1,image2,i*103,512);
//            }
//            else
//            {
//                comparecall[i] = new CompareImgCall(image1,image2,i*103,(i+1)*103+5);
//            }
//
//            future[i] = pool.submit(comparecall[i]);
//        }
        for(int i=0;i<2;i++)
        {
            coordinate+=(String)future[i].get();
            //System.out.println(coordinate);
        }

        pool.shutdown();
        return coordinate;
    }
    private static String CompaseImg(BufferedImage image1, BufferedImage image2, int start, int end)
    {
        String coordinate = "";
        int height = image2.getHeight();
        int width = image2.getWidth();
        int m=0,n=0;
        boolean flag1 = true;
        boolean flag = true;
        int[][] imggrayMtrixexam = ImageUtils.getGrayMatrix(image1);
        int[][] Mtrix = ImageUtils.getGrayMatrix(image2);
        for (int i = start; i < end - height && flag1; i++) {
            for (int j = 0; j < 512 - width && flag1; j++) {
                flag = true;
                m = 0;
                n = 0;
                for (m = 0; m < height && flag; m++) {
                    for (n = 0; n < width && flag; n++) {
                        if (imggrayMtrixexam[i + m][j + n] != Mtrix[m][n]) {
                            flag = false;
                        }
                    }
                }
                if (m == height && n == width && flag) {
                    coordinate += i + "," + j + "," + (i + height) + "," + (j + width);
                    flag1 = false;
                }
            }

        }
        return coordinate;
    }

}
