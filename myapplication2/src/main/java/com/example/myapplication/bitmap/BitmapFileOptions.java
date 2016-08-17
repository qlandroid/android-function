package com.example.myapplication.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/17.
 */
public class BitmapFileOptions {
    //200M
    public static final long fileMaxSize = 200 * 1024;
    /**
     * 获得一个图片路径，如果图片大小超过指定大小，则会进行压缩；
     * 等比例压缩
     * @param fileUri 所要压缩的图片路径
     * @return
     */
    public static File scal(Uri fileUri){
        //先获得路径
        String path = fileUri.getPath();
        //创建以个file对象
        File outputFile = new File(path);
        //获得该文件的大小
        long fileSize = outputFile.length();
        //设置一个最大值，如果大于此值就进行压缩

        if (fileSize >= fileMaxSize) {
            //获得一个压缩对象
            BitmapFactory.Options options = new BitmapFactory.Options();
            //将该属性设置为true，bitmap将不会加载到内存中；
            options.inJustDecodeBounds = true;
            //将path路径下的图片的宽高，赋值给options
            BitmapFactory.decodeFile(path, options);
            //获取图片的宽高
            int height = options.outHeight;
            int width = options.outWidth;
            //进行 返回正确舍入的 double 值的正平方根。也就是缩放比例
            double scale = Math.sqrt((float) fileSize / fileMaxSize);
            options.outHeight = (int) (height / scale);
            options.outWidth = (int) (width / scale);
            options.inSampleSize = (int) (scale + 0.5);
            options.inJustDecodeBounds = false;
            //创建一个bitmap对象已经加载到内存中
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            //一个临时的文件路径
            String newPath = createImageFile().getPath();
            // 创建一个file对象用于IO流操作
            outputFile = new File(newPath);
            FileOutputStream fos = null;
            try {
                //创建一个输出流
                fos = new FileOutputStream(outputFile);
                //将图片保存到本地，图片的质量只有原来的50%；
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //判断当前是否有回收
            if (!bitmap.isRecycled()) {
                //没有回收，则手动调用
                bitmap.recycle();
            }else{
                //图片被回收；
                File tempFile = outputFile;
                outputFile = new File(createImageFile().getPath());
                copyFileUsingFileChannels(tempFile, outputFile);
            }

        }
        return outputFile;

    }

    /**
     * 创建一个图片存储到本地的文件
     * @return
     */
    public static Uri createImageFile(){
        // Create an image file name
        //文件保存时间
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //设置文件名
        String imageFileName = "JPEG_ "+ timeStamp + "_";
        //文件的根目录
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            /**
             * 参数：
             * prefix - 用于生成文件名的前缀字符串；必须至少是三字符长
             * suffix - 用于生成文件名的后缀字符串；可以为 null，在这种情况下，将使用后缀 ".tmp"
             * directory - 将创建的文件所在的目录；如果使用默认临时文件目录，则该参数为 null
             */
            image = File.createTempFile(imageFileName, /* prefix */
                    ".jpg", /* suffix */storageDir  /* directory */);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        //通过创建的文件夹 返回一个Uri；进行文件传输用
        return Uri.fromFile(image);
    }

    /**
     * 复制一个新的文件；
     * @param source
     * @param dest
     */
    public static void copyFileUsingFileChannels(File source, File dest){
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            try {
                inputChannel = new FileInputStream(source).getChannel();
                outputChannel = new FileOutputStream(dest).getChannel();
                /**
                 * inputChanne1输入通道
                 * 0，复制文件的起始位置
                 * size ,大小；
                 */
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } finally {
            if (inputChannel!=null){
                try {
                    inputChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                inputChannel= null;
            }
            if (outputChannel!=null) {
                try {
                    outputChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                outputChannel = null;
            }
        }
    }
}
