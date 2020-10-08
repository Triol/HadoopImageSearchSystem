package entrance;

import job.search.forgery.SearchForgeryJob;
import job.search.image.SearchImageJob;
import job.search.part.SearchPartImageJob;
import utils.ImageUtils;

import java.io.File;

public class Main {
    /*
        args[0] SearchType  SearchPart(0), SearchImage(1), SearchForgery(2)
        args[1] inputFile<file>
        args[2] outputPath<dir>
        args[3] taskName
     */
    public static void main(String[] args) {
        if(args.length != 4) {
            System.out.println("Usages: java -jar <program> <SearchType> <inputFile(file)> <outputPath(dir)> <taskName>");
            System.out.println("<SearchType>: SearchPart SearchImage SearchForgery");
            System.out.println("<SearchType>: 0          1           2");
            System.out.println("<inputFile> : inputFile: must be a image file");
            System.out.println("<outputPath>: outputPath: must be a directory");
        }else {
            File inputFile = new File(args[1]);
            if(inputFile == null || !inputFile.isFile()) {
                System.err.println("inputFile error, please check the path");
            }
            File outputDir = new File(args[2]);
            if(outputDir == null || !outputDir.isDirectory()) {
                System.err.println("outputPath error, must be a directory, please check");
            }

            if(args[0].equals("0") || args[0].equals("SearchPart")) {
                try {
                    SearchPartImageJob.doJob(args[3], ImageUtils.getImage(inputFile), args[2]);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(args[0].equals("1") || args[0].equals("SearchImage")) {
                try {
                    SearchImageJob.doJob(args[3], ImageUtils.getImage(inputFile), args[2]);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(args[0].equals("2") || args[0].equals("SearchForgery")) {
                try {
                    SearchForgeryJob.doJob(args[3], ImageUtils.getImage(inputFile), args[2]);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
