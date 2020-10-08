package utils;

import com.sun.tools.javac.util.Pair;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ImageUtils {
    private static BASE64Decoder decoder = new BASE64Decoder();
    private static BASE64Encoder encoder = new BASE64Encoder();
    public static BufferedImage writeRectangle(BufferedImage image, ArrayList<MyRectangle> rectangles) throws Exception {
        BufferedImage ret = toRGB(image);

        Graphics g = ret.getGraphics();
        g.setColor(Color.RED);
        for(MyRectangle rec : rectangles) {
            g.drawRect(rec.getL(), rec.getU(), rec.getR() - rec.getL() + 1, rec.getD() - rec.getU());
        }
        return ret;
    }
    public static BufferedImage writeRectangle(BufferedImage image, MyRectangle rectangles) throws Exception {
        BufferedImage ret = toRGB(image);

        Graphics g = ret.getGraphics();
        g.setColor(Color.RED);
        g.drawRect(rectangles.getL(), rectangles.getU(), rectangles.getR() - rectangles.getL() + 1, rectangles.getD() - rectangles.getU());
        return ret;
    }
    public static Pair<Double, ArrayList<MyRectangle>> compareImage(String image1Base64, String image2Base64, int thread, int precision) throws Exception {
        BufferedImage image1 = fromBase64(image1Base64);
        BufferedImage image2 = fromBase64(image2Base64);
        return compareImage(image1, image2, thread, precision);
    }
    public static Pair<Double, ArrayList<MyRectangle>> compareImage(byte[] image1Base64, byte[] image2Base64, int thread, int precision) throws Exception {
        BufferedImage image1 = fromBytes(image1Base64);
        BufferedImage image2 = fromBytes(image2Base64);
        return compareImage(image1, image2, thread, precision);
    }

    /*
     * 4 thread compare
     * @return return {rate, List<>} if the similarity is greater than threshold,
     *         else return {rate, List<> with empty}
     */
    public static Pair<Double, ArrayList<MyRectangle>> compareImage(BufferedImage image1, BufferedImage image2, int thread, int precision) throws Exception{

        if(image1 == null || image2 == null)
            return new Pair<Double, ArrayList<MyRectangle>>(1.00, new ArrayList<MyRectangle>());

        int w1 = image1.getWidth();
        int h1 = image1.getHeight();
        int w2 = image1.getWidth();
        int h2 = image2.getHeight();

        //size not match
        if(w1 != w2 || h1 != h2) {
            return new Pair<Double, ArrayList<MyRectangle>>(1.00, new ArrayList<MyRectangle>());
        }
        int h = h1, w = w1;
        int[] height = getBalancedBlocks(h, thread);
//        for(int i = 0; i < thread; i++) {
//            height[i] =  h / thread + (i < h % thread ? 1 : 0);
//        }
        BufferedImage[] imageSlices1 = new BufferedImage[thread];
        BufferedImage[] imageSlices2 = new BufferedImage[thread];
        /*
         *      O → x
         *      ↓
         *      y
         */
        for(int i = 0, curY = 0; i < thread; i++) {
            imageSlices1[i] = image1.getSubimage(0, curY, w, height[i]);
            imageSlices2[i] = image2.getSubimage(0, curY, w, height[i]);
            curY += height[i];
        }
        int [][] mark;
        int[] [][] subMark = new int[thread] [][];
        Point[][] markLUPosition, markRDPosition;

        ExecutorService pool = Executors.newFixedThreadPool(thread);
        CompareSubImageCall[] compareCalls = new CompareSubImageCall[thread];
        Future[] future = new Future[thread];

        for(int i = 0; i < thread; i++) {
            //subMark[i] = compareSubImage(imageSlices1[i], imageSlices2[i], precision, 1.00);
            compareCalls[i] = new CompareSubImageCall(imageSlices1[i], imageSlices2[i], precision, 1.00);
            future[i] = pool.submit(compareCalls[i]);
        }

        for(int i = 0; i < thread; i++) {
            subMark[i] = (int[][])future[i].get();
        }
        pool.shutdown();

        int markH = 0;
        int markW = subMark[0][0].length;
        for(int i = 0; i < thread; i++) {
            markH += subMark[i].length;
        }
        mark = new int[markH][markW];
        markLUPosition = new Point[markH][markW];
        markRDPosition = new Point[markH][markW];
        int original_X = 0, original_Y = 0;
        for(int i = 0, curY = 0; i < subMark.length; curY += subMark[i].length, i++) {
            for(int y = 0; y < subMark[i].length; y++) {
                original_X = 0;
                for(int x = 0; x < markW; x++) {
                    mark[y + curY][x] = subMark[i][y][x];
                    markLUPosition[y + curY][x] = new Point(original_X, original_Y);
                    //markLUPosition[y + curY][x].setY(original_Y);
                    markRDPosition[y + curY][x] = new Point(original_X + getPartBlockSize(w, markW, x) - 1,
                                                            original_Y + getPartBlockSize(height[i], subMark[i].length, y) - 1);
//                    markRDPosition[y + curY][x].setX(original_X + getPartBlockSize(height[i], subMark[i].length, x) - 1);
//                    markRDPosition[y + curY][x].setY(original_Y + getPartBlockSize(w, markW, y) - 1);
                    original_X += getPartBlockSize(w, markW, x);
                }
                original_Y += getPartBlockSize(height[i], subMark[i].length, y);
            }
        }
        //DSU, calculate the rectangles
        ArrayList<MyRectangle>ret = new ArrayList<MyRectangle>();
        int idCnt = 0;
        double rate = 0;
//        System.out.print("   ");
//        for (int j = 0; j < markW; j++) {
//            System.out.printf("%3d", j);
//        }
//        for(int i = 0; i < markH; i++) {
//            System.out.printf("\n%3d", i);
//            for (int j = 0; j < markW; j++) {
//                if(mark[i][j] == 0) System.out.printf("   ");
//                else System.out.printf("%3d", mark[i][j]);
//            }
//        }
//        System.out.println();
        for(int i = 0; i < markH; i++) {
            for(int j = 0; j < markW; j++) {
                if(mark[i][j] == -1) {
                    //BFS
                    rate += 1.0;
                    mark[i][j] = ++idCnt;
                    MyRectangle newOne = new MyRectangle(j, j, i, i);
                    Queue<Point> queue = new LinkedList<Point>();
                    queue.offer(new Point(j, i));
                    while(queue.size() > 0) {
                        Point front = queue.poll();
                        int fx = front.getX();
                        int fy = front.getY();
                        //8-orientation
                        for(int dy = -1; dy <= 1; dy++) {
                            for(int dx = -1; dx <= 1; dx++) {
                                if(dx == dy && dx == 0) continue;
                                int cx = fx + dx;
                                int cy = fy + dy;
                                //check bounce and difference
                                if(cy >= 0 && cy < markH && cx >= 0 && cx < markW
                                    && mark[cy][cx] == -1) {
                                    //update mark
                                    rate += 1.0;
                                    mark[cy][cx] = idCnt;
                                    //update queue
                                    queue.offer(new Point(cx, cy));
                                    //update rectangle
                                    if(cy <= newOne.getU()) newOne.setU(cy);
                                    if(cy >= newOne.getD()) newOne.setD(cy);
                                    if(cx <= newOne.getL()) newOne.setL(cx);
                                    if(cx >= newOne.getR()) newOne.setR(cx);
                                }
                            }
                        }
                    }
                    ret.add(newOne);
                }

            }
        }
//        System.out.println("=====================");
//        System.out.print("   ");
//        for (int j = 0; j < markW; j++) {
//            System.out.printf("%3d", j);
//        }
//        for(int i = 0; i < markH; i++) {
//            System.out.printf("\n%3d", i);
//            for (int j = 0; j < markW; j++) {
//                if(mark[i][j] == 0) System.out.printf("   ");
//                else System.out.printf("%3d", mark[i][j]);
//            }
//        }
//        System.out.println();
        rate /= markH * markW;
        ArrayList<MyRectangle> result = new ArrayList<MyRectangle>();
        for(MyRectangle m : ret) {
            int l, u, r, d;
            l = markLUPosition[m.getU()][m.getL()].getX();
            u = markLUPosition[m.getU()][m.getL()].getY();
            r = markRDPosition[m.getD()][m.getR()].getX();
            d = markRDPosition[m.getD()][m.getR()].getY();
            result.add(new MyRectangle(l, r, u, d));
        }
        return new Pair<Double, ArrayList<MyRectangle>>(rate, result);
    }
    /*
     * @return int[][], -1 represent the difference, 0 represent the same
     */
    private static class CompareSubImageCall implements Callable {
        private BufferedImage image1, image2;
        private int precision;
        private double threshold;

        public CompareSubImageCall(BufferedImage image1, BufferedImage image2, int precision, double threshold) {
            this.image1 = image1;
            this.image2 = image2;
            this.precision = precision;
            this.threshold = threshold;
        }

        public Object call() throws Exception {
            return compareSubImage(image1, image2, precision, threshold);
        }
    }
    private static int[][] compareSubImage(BufferedImage image1, BufferedImage image2, int precision, double threshold) throws Exception{
        //split into 16*16
        if(image1 == null || image2 == null)
            throw new NullPointerException("image can not be null");
        int h = image1.getHeight();
        int w = image1.getWidth();

        int xBlockNum = w / precision;
        int yBlockNum = h / precision;
        int[] yBlocks = getBalancedBlocks(h, yBlockNum);
        int[] xBlocks = getBalancedBlocks(w, xBlockNum);
//
//        for(int i = 0; i < xBlockNum; i++) {
//            xBlocks[i] = h / 16 + (i < h % 16 ? 1 : 0);
//        }
//        for(int i = 0; i < yBlockNum; i++) {
//            yBlocks[i] = w / 16 + (i < w % 16 ? 1 : 0);
//        }

        int[][] mark = new int[yBlockNum][xBlockNum];
        for(int i = 0, curY = 0; i < yBlockNum; curY += yBlocks[i], i++) {
            for(int j = 0, curX = 0; j < xBlockNum; curX += xBlocks[j], j++) {
                //compare block
                int total = yBlocks[i] * xBlocks[j];
                int same = 0;
                for(int dy = 0; dy < yBlocks[i]; dy++) {
                    for(int dx = 0; dx < xBlocks[j]; dx++) {
                        int x = curX + dx;
                        int y = curY + dy;
                        if(image1.getRGB(x, y) == image2.getRGB(x, y)) {
                            same++;
                        }
                    }
                }
                double rate = same;
                rate /= total;
                if(rate >= threshold) {
                    mark[i][j] = 0;
                }else {
                    mark[i][j] = -1;
                }
            }
        }
        return mark;
    }

    private static int[] getBalancedBlocks(int total, int part) throws Exception{
        if(part == 0 || total == 0) {
            throw new Exception("part and total can not be null");
        }
        int[] res = new int[part];
        for(int i = 0; i < part; i++) {
            res[i] = total / part + (i < total % part ? 1 : 0);
        }
        return res;
    }

    private static int getPartBlockSize(int total, int totalPart, int currentPart) throws Exception{
        if(currentPart > totalPart) throw new Exception("current part can not be greater than total part");
        if(totalPart == 0) throw new Exception("totalPart cannot be zero");
        if(currentPart < 0) throw new Exception("current part must be positive");
        return total / totalPart + (currentPart < total % totalPart ? 1 : 0);

    }

    private static int getPartBlockNumber(int total, int totalPart, int offset) throws Exception{
        if(offset < 0 || offset >= total)
            throw new Exception("offset must in range [0, total - 1]");
        for(int i = 0; i < totalPart; i++) {
            offset -= getPartBlockSize(total, totalPart, i);
            if(offset < 0) return i;
        }
        throw new Exception("Unknown Exception");
    }
    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
    public static BufferedImage toRGB(BufferedImage srcImg){
        return new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB), null).filter(srcImg, null);
    }
    public static String toBase64(BufferedImage image) throws Exception{
        String b64Str;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ImageIO.write(image, "bmp", baos);
        byte[] bytes = baos.toByteArray();
        return encoder.encode(bytes).trim();
    }
    public static BufferedImage fromBase64(String base64) throws IOException {
        byte[] imgBytes = decoder.decodeBuffer(base64);
        BufferedImage image = ImageIO.read(new ByteInputStream(imgBytes, imgBytes.length));
        return image;
    }
    public static BufferedImage fromBytes(byte[] bytes) throws IOException {
        return ImageIO.read(new ByteInputStream(bytes, bytes.length));
    }
    public static byte[] toBytes(BufferedImage bi) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ImageIO.write(bi, "bmp", baos);
        return baos.toByteArray();
    }
    public static BufferedImage getImage(File file) throws IOException{
        return ImageIO.read(file);
    }
    public static int[] getHistogram(BufferedImage bi) {
        int[] res = new int[256];
        int h = bi.getHeight();
        int w = bi.getWidth();
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                int rgb = bi.getRGB(x, y) & 0xFFFFFF;
                int r = (rgb & 0xFF0000) >> 16;
                int g = (rgb & 0x00FF00) >> 8;
                int b = (rgb & 0x0000FF);
                int gray = (r*19595 + g*38469 + b*7472) >> 16;
                res[gray]++;
            }
        }
        return res;
    }
    public static String getHistogramString(BufferedImage bi) {
        int[] arr = getHistogram(bi);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if(i != arr.length - 1) sb.append(',');
        }
        return sb.toString();
    }

    public static void writeToFile(BufferedImage bi, String formatName, String path) throws IOException {
        ImageIO.write(bi, formatName, new FileOutputStream(path));
    }
    public static int[][] getGrayMatrix(BufferedImage image) {

        int h = image.getHeight();
        int w = image.getWidth();
        int[][] grayMatrix = new int[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int rgb = image.getRGB(i, j) & 0xFFFFFF;
                int r = (rgb & 0xFF0000) >> 16;
                int g = (rgb & 0x00FF00) >> 8;
                int b = (rgb & 0x0000FF);
                int gray = (r * 19595 + g * 38469 + b * 7472) >> 16;
                grayMatrix[i][j] = gray;
            }
        }
        return grayMatrix;

    }
}
