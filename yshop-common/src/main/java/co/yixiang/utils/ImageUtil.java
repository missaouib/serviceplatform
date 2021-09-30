package co.yixiang.utils;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.util.GraphicsRenderingHints;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

public class ImageUtil {

    /**
     * 将网络图片编码为base64
     *
     * @param url
     * @return
     * @throws
     */
    public static String encodeImageToBase64(URL url) throws Exception {
//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        System.out.println("图片的路径为:" + url.toString());
//打开链接
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
//设置请求方式为"GET"
            conn.setRequestMethod("GET");
//超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
//通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
//得到图片的二进制数据，以二进制封装得到数据，具有通用性
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//创建一个Buffer字符串
            byte[] buffer = new byte[1024];
//每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
//使用一个输入流从buffer里把数据读取出来
            while ((len = inStream.read(buffer)) != -1) {
//用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
//关闭输入流
            inStream.close();
            byte[] data = outStream.toByteArray();
//对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            String base64 = encoder.encode(data);
          //  System.out.println("网络文件[{}]编码成base64字符串:[{}]"+url.toString()+base64);
            return base64;//返回Base64编码过的字节数组字符串
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("图片上传失败,请联系客服!");
        }
    }

    public String localImageToBase64(String fileName) {
        try {
            InputStream stream =  getClass().getClassLoader().getResourceAsStream(fileName);

            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//创建一个Buffer字符串
            byte[] buffer = new byte[1024];
//每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
//使用一个输入流从buffer里把数据读取出来
            while ((len = stream.read(buffer)) != -1) {
//用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
//关闭输入流
            stream.close();
            byte[] data = outStream.toByteArray();
//对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            String base64 = encoder.encode(data);
            return base64;
        }catch (Exception e) {

        }
        return "";
    }


    public String localImageToBase64_2(String fileName) {
        try {
            File file = new File(fileName);
            InputStream stream =  new FileInputStream(file);

            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//创建一个Buffer字符串
            byte[] buffer = new byte[1024];
//每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
//使用一个输入流从buffer里把数据读取出来
            while ((len = stream.read(buffer)) != -1) {
//用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
//关闭输入流
            stream.close();
            byte[] data = outStream.toByteArray();
//对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            String base64 = encoder.encode(data);
            return base64;
        }catch (Exception e) {

        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        String imgFilePath="http://papssrc.ilvzhou.com/statics/upload/attachment/202007/27/4816d313f8c3d431c52644264218af64.jpg";
      //  String base64_str = encodeImageToBase64(new URL(imgFilePath));//将网络图片编码为base64


        String path = "https://test.yiyao-mall.com/api/file/pic/20201222163307323259.png".replace("https://test.yiyao-mall.com/api"+"/file","");

     //   path.replace("\\",File.)

        path = "E:\\yshop\\file\\" + path;
        System.out.println(path);
        String base64_str = Base64Util.getImageBinary(path,"png");//将本地图片编码为base64
        System.out.println(base64_str);
        Base64Util.base64StringToFile(base64_str,"E:/out.jpg");
    }


    public static void toFileByBase64(String image_encode){


        FileOutputStream fos  = null;
        try {
            String image = URLDecoder.decode(image_encode, "UTF-8");
            BASE64Decoder decoder = new BASE64Decoder();

            // 生成图片信息
            String suffix = ".jpg";
            if (image.startsWith("data:image/jpeg;base64,")) {
                image = image.substring(23);
            } else if (image.startsWith("data:image/png;base64,")) {
                image = image.substring(22);
                suffix = ".png";
            }

            byte[] bytes = decoder.decodeBuffer(image);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }



            File file = new File("E:/1.jpg");
            if (!file.exists()) {
                file.createNewFile();
            }

            fos  = new FileOutputStream(file);
            fos.write(bytes);
        }catch (Exception e) {
e.printStackTrace();
        }finally {
            if (fos != null) {
                try{
                    fos.close();
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }


    public static int pdf2Pic(String pdfPath, String path){
        Document document = new Document();
        document.setFile(pdfPath);
        //缩放比例
        float scale = 2.5f;
        //旋转角度
        float rotation = 0f;

        for (int i = 0; i < document.getNumberOfPages(); i++) {
            BufferedImage image = (BufferedImage)
                    document.getPageImage(i, GraphicsRenderingHints.SCREEN, org.icepdf.core.pobjects.Page.BOUNDARY_CROPBOX, rotation, scale);
            RenderedImage rendImage = image;
            try {
                String imgName = i + ".png";
                System.out.println(imgName);
                File file = new File(path + imgName);
                ImageIO.write(rendImage, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            image.flush();
        }
        document.dispose();

        return document.getNumberOfPages();
    }
}
