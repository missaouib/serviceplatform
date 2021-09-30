package co.yixiang.modules.taibao.util;
import co.yixiang.exception.ErrorRequestException;

import java.io.*;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipCompressor {
    static final int BUFFER = 8192;

    private File zipFile;

    public ZipCompressor(String pathName) {
        zipFile = new File(pathName);
        if(zipFile.exists()){
            zipFile.delete();
        }
        try {
            zipFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void compress(List<Map<String,Object>> pathName) {
        ZipOutputStream out = null;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
            CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream,
                    new CRC32());
            out = new ZipOutputStream(cos);
            String basedir = "";

            for (Map<String,Object> s : pathName) {

                compress(new File(s.get("imgUrl").toString()), out, basedir,s.get("renameUrl").toString());
            }
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void compress(String srcPathName) {
        File file = new File(srcPathName);
        if (!file.exists()){
            throw new RuntimeException(srcPathName + "不存在！");
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
            CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream,
                    new CRC32());
            ZipOutputStream out = new ZipOutputStream(cos);
            String basedir = "";
            compress(file, out, basedir,file.getName());
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void compress(File file, ZipOutputStream out, String basedir,String renameTo) {
        /* 判断是目录还是文件 */
        if (file.isDirectory()) {
            System.out.println("压缩：" + basedir + file.getName());
            this.compressDirectory(file, out, basedir);
        } else {
            System.out.println("压缩：" + basedir + file.getName());
            this.compressFile(file, out, basedir,renameTo);
        }
    }

    /** 压缩一个目录 */
    private void compressDirectory(File dir, ZipOutputStream out, String basedir) {
        if (!dir.exists()){
            throw new ErrorRequestException("文件目录："+dir.getPath()+"不存在。");
        }
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            /* 递归 */
            compress(files[i], out, basedir + dir.getName() + "/",files[i].getName());
        }
    }

    /** 压缩一个文件 */
    private void compressFile(File file, ZipOutputStream out, String basedir,String renameTo) {
        if (!file.exists()) {
            throw new ErrorRequestException("文件："+file.getPath()+"不存在。");
        }
        try {
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            ZipEntry entry = new ZipEntry(basedir + renameTo);
            out.putNextEntry(entry);
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            bis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        List<String> imgPath=Arrays.asList("d:/test.xml","d:/testRsult.txt","d:/VSHH20050678933.txt");
        List<Map<String,Object>> maps=new ArrayList<>();
        for (int i = 0; i < imgPath.size(); i++) {
            Map<String,Object> map=new HashMap<>();
            map.put("imgUrl",imgPath.get(i));
            map.put("renameUrl","SHYJK"+"12930128301982"+String.format("%03d", Integer.valueOf(i+1))+imgPath.get(i).substring(imgPath.get(i).indexOf(".")));
            maps.add(map);
        }
        ZipCompressor zc = new ZipCompressor("d:"+File.separator+"SHYJK"+"12930128301982"+".zip");
        zc.compress(maps);

        maps.clear();
        List<String> pdfPath=Arrays.asList("d:/理赔结案通知书.pdf");
        for (int i = 0; i < pdfPath.size(); i++) {
            Map<String,Object> map=new HashMap<>();
            map.put("imgUrl",pdfPath.get(i));
            map.put("renameUrl","SHYJK"+"12930128301982"+".pdf");
            maps.add(map);
        }

        String pdfZipPath="d:"+File.separator+"SHYJK"+"12930128301982"+"_pdf.zip";
        ZipCompressor zcPdf = new ZipCompressor(pdfZipPath);
        zcPdf.compress(maps);

    }
}
