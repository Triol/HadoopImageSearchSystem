package utils.test;

import com.sun.tools.javac.util.Pair;
import utils.GlobalEnv;
import utils.ImageUtils;
import utils.MyRectangle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ImageUtilsTest {
    public static void main(String[] args) throws Exception {
        File imageFile1 = new File("F:\\workspace\\common_files\\SAME\\1.bmp");
        File imageFile2 = new File("F:\\workspace\\common_files\\SAME\\220.bmp");
        BufferedImage image1, image2;
        image1 = ImageIO.read(imageFile1);
        image2 = ImageIO.read(imageFile2);

//        long timeStart = System.currentTimeMillis();
//        for(int i = 0; i < 1000; i++) {
//            Pair<Double, ArrayList<MyRectangle>> result = ImageUtils.compareImage(image1, image2, 4, 32);
//        }
//        long timeEnd = System.currentTimeMillis();
//        System.out.println("##cost-time: " + (timeEnd - timeStart) + "ms");
        Pair<Double, ArrayList<MyRectangle>> result = ImageUtils.compareImage(image1, image2, 8, 16);

        System.out.println(result.fst);
//        for(int y = 0; y < image1.getHeight(); y += 16) {
//            g.drawLine(0, y, image1.getWidth(), y);
//            g.drawLine(y, 0, y, image1.getHeight());
//        }
//        g.setColor(Color.BLACK);
//        for(MyRectangle rec : result.getValue()) {
//
//            g.drawRect(rec.getL(), rec.getU(), rec.getR() - rec.getL() + 1, rec.getD() - rec.getU());
//        }
//        BufferedImage image3 = image1.getSubimage(100, 0, 128, 512);
        BufferedImage image3 = ImageUtils.writeRectangle(image2, result.snd);
        FileOutputStream file = new FileOutputStream("F:\\workspace\\common_files\\SAME\\compare.png");
        ImageIO.write(image3, "png", file);
    }
}
