package job.upload.test;

import java.io.IOException;

import static job.upload.UploadImages.uploadImage;

public class uploadImagesTest {
    public static void main(String[] args) {
        try {
            uploadImage("F:\\workspace\\common_files\\BOSSBASE");

        }catch (IOException e) {
            e.printStackTrace();
        }

    }
}
