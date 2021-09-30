package co.yixiang.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Base64Util {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
//      String base64 = getImageBinary("D:/test.jpg","jpg");
//      base64StringToFile(base64,"E:/out.jpg");
        //System.out.println(base64);//

//       File file = new File("D:/test.pdf");
//       String base64 = getFileBinary(file );
//       System.out.println(base64);
//       base64StringToFile(base64,"E:/out.pdf");
    }
    /**
     * 图片转base64
     * @param filePath 图片路径: "D:/test.jpg"
     * @param fileTyp  图片类型: "jpg"
     * @return base64字符串
     */
    public  static String getImageBinary(String filePath,String fileTyp){
        File f= new File(filePath);
        BufferedImage bi;
        try {
            bi = ImageIO.read(f);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, fileTyp, baos);
            byte[] bytes = baos.toByteArray();

            return new sun.misc.BASE64Encoder().encodeBuffer(bytes).trim();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件转成base64
     * @param file File对象:File file = new File("D:/test.pdf");
     * @return base64字符串
     */
   public static String getFileBinary(File file){
        FileInputStream fin = null;
        BufferedInputStream bin = null;
        ByteArrayOutputStream baos = null;
        BufferedOutputStream bout = null;

        try {
            //建立读取文件的额文件输出流
            fin = new FileInputStream(file);
            //在文件输出流上安装节点流(更大效率读取)
            bin = new BufferedInputStream(fin);
            //创建一个新的byte数组输出流,它具有指定大小的缓冲区容量
            baos = new ByteArrayOutputStream();
            //创建一个新的缓冲输出流,以将数据写入指定的底层输出流
            bout = new BufferedOutputStream(baos);
            byte[] buffer = new byte[1024];
            int len = bin.read(buffer);
            while (len != -1) {
                bout.write(buffer,0,len);
                len = bin.read(buffer);
            }
            //刷新此输出流并强制写出所有缓冲的输出字节,必须这行代码,否则有可能有问题
            bout.flush();
            byte[] bytes = baos.toByteArray();
            //sun公司的API
            return new sun.misc.BASE64Encoder().encodeBuffer(bytes).trim();
            //apache公司的API
            //return Base64.encodeBase64String(bytes);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
            try {
                fin.close();
                bin.close();
                bout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * base64转文件
     * @param base64sString
     * @param filePath "E:/out.jpg" "/home/test.pdf"
     */
    public static void base64StringToFile(String base64sString,String filePath){
        BufferedInputStream bin = null;
        FileOutputStream fout = null;
        BufferedOutputStream bout = null;
        try {
            //将base64编码的字符串解码成字节数组
            byte[] bytes = new sun.misc.BASE64Decoder().decodeBuffer(base64sString);
            //apache公司的API
            //byte[] bytes = Base64.decodeBase64(base64sString);
            //创建一个将bytes作为其缓冲区的ByteArrayInputStream对象
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            //创建从底层输入流中读取数据的缓冲输入流对象
            bin = new BufferedInputStream(bais);
            //指定输出文件
            //File file = new File("/home/test.pdf");
            File file = new File(filePath);
            //创建到指定文件的输出流
            fout = new FileOutputStream(file);
            //为文件输出流对接缓冲输出流对象
            bout = new BufferedOutputStream(fout);
            byte[] buffers = new byte[1024];
            int len = bin.read(buffers);
            while (len != -1) {
                bout.write(buffers,0,len);
                len = bin.read(buffers);
            }
            //刷新此输出流并强制写出所有缓冲的输出字节,必须这行代码,否则有可能有问题
            bout.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                bin.close();
                fout.close();
                bout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}