package co.yixiang.modules.taibao.util;

import co.yixiang.exception.ErrorRequestException;
//import org.apache.commons.net.ftp.FTP;
//import org.apache.commons.net.ftp.FTPClient;
//import org.apache.commons.net.ftp.FTPFile;
//import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FTPUtil {
/*

    protected static Logger logger = LoggerFactory.getLogger(FTPUtil.class);
    public static FTPClient ftpClient=new FTPClient();
    public static ArrayList<String> arFiles=new ArrayList<>();


    */
/**
     * 登陆FTP服务器
     *
     * @param host     FTPServer IP地址
     * @param port     FTPServer 端口
     * @param username FTPServer 登陆用户名
     * @param password FTPServer 登陆密码
     * @return 是否登录成功
     * @throws IOException
     *//*

    public static boolean ftpConnection(String host, int port, String username, String password) {
        try {
            logger.info("ip地址："+host+",端口:"+port+",登陆用户名:"+username+",登陆密码:"+password);
            ftpClient.connect(host, port);
            if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                logger.info("ftp isPositiveCompletion true");
                if (ftpClient.login(username, password)) {
                    logger.info("ftp login true");
                    */
/**
                     需要注意这句代码，如果调用List()方法出现，文件的无线递归，与真实目录结构不一致的时候，可能就是因为转码后，读出来的文件夹与正式文件夹字符编码不一致所导致。
                     则需去掉转码，尽管递归是出现乱码，但读出的文件就是真实的文件，不会死掉。等读完之后再根据情况进行转码。
                     如果ftp部署在windows下，则：
                     for (String arFile : f.arFiles) {
                     arFile = new String(arFile.getBytes("iso-8859-1"), "GBK");
                     logger.info(arFile);
                     }
                     *//*

                    ftpClient.setControlEncoding("GBK");
                    return true;
                }
            }
            if (ftpClient.isConnected()) {
                logger.info("ftp isConnected to disconnect ");
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }


    */
/**
     * 断开FTP服务器连接
     *
     * @throws IOException
     *//*

    public static void close() throws IOException {
        if (ftpClient != null && ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }


    */
/**
     * 下载ftp服务器文件方法
     *
     * @param newFileName 新文件名
     * @param fileName    原文件（路径＋文件名）
     * @param downUrl     下载路径
     * @return
     * @throws IOException
     *//*

    public static boolean downFile(String newFileName, String fileName, String downUrl) throws IOException {
        boolean isTrue = false;
        OutputStream os = null;
        File localFile = new File(downUrl + "/" + newFileName);
        if (!localFile.getParentFile().exists()) {//文件夹目录不存在创建目录
            localFile.getParentFile().mkdirs();
            localFile.createNewFile();
        }
        os = new FileOutputStream(localFile);
        isTrue = ftpClient.retrieveFile(new String(fileName.getBytes(), "ISO-8859-1"), os);
        os.close();
        return isTrue;
    }

    */
/**
     * 上传文件
     * @param path ftp 路径
     * @param filename ftp文件名
     * @param input 文件流
     * @return
     * @throws IOException
     *//*

    public static boolean storeFile(String path, String filename, InputStream input)  throws IOException{
        // 转移工作目录至指定目录下
        boolean result = false;

        boolean change = ftpClient.changeWorkingDirectory(path);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        if (change) {
            result = ftpClient.storeFile(new String(filename.getBytes(),"iso-8859-1"), input);
            if (result) {
                System.out.println("上传成功!");
            }else{
                throw new ErrorRequestException("上传失败");
            }
        }
        input.close();
        return result;
    }

    */
/**
     * 递归遍历出目录下面所有文件
     *
     * @param pathName 需要遍历的目录，必须以"/"开始和结束
     * @throws IOException
     *//*

    public static List<String> List(List<String> arFiles,String pathName) throws IOException {
        if (pathName.startsWith("/") && pathName.endsWith("/")) {
            //更换目录到当前目录
            ftpClient.changeWorkingDirectory(pathName);
            FTPFile[] files = ftpClient.listFiles();
            for (FTPFile file : files) {
                if (file.isFile()) {
                    arFiles.add(pathName + file.getName());
                } else if (file.isDirectory()) {
                    // 需要加此判断。否则，ftp默认将‘项目文件所在目录之下的目录（./）’与‘项目文件所在目录向上一级目录下的目录（../）’都纳入递归，这样下去就陷入一个死循环了。需将其过滤掉。
                    if (!".".equals(file.getName()) && !"..".equals(file.getName())) {
                        List(arFiles,pathName + file.getName() + "/");
                    }
                }
            }
        }
        return  arFiles;
    }

    */
/**
     * 递归遍历目录下面指定的文件名
     *
     * @param pathName 需要遍历的目录，必须以"/"开始和结束
     * @param ext      文件的扩展名
     * @throws IOException
     *//*

    public static List<String> List(List<String> arFiles,String pathName, String ext) throws IOException {
        if (pathName.startsWith("/") && pathName.endsWith("/")) {
            //更换目录到当前目录
            ftpClient.changeWorkingDirectory(pathName);
            FTPFile[] files = ftpClient.listFiles();
            for (FTPFile file : files) {
                if (file.isFile()) {
                    if (file.getName().endsWith(ext)) {
                        arFiles.add(pathName + file.getName());
                    }
                } else if (file.isDirectory()) {
                    if (!".".equals(file.getName()) && !"..".equals(file.getName())) {
                        List(arFiles,pathName + file.getName() + "/", ext);
                    }
                }
            }
        }
        return  arFiles;
    }

    public static Boolean renameFile(String oldName,String newName) {
        try {
            ftpClient.rename(oldName,newName);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return  false;
        }
    }


    public static void main(String[] args) throws Exception {
        */
/*
        boolean b = ftpConnection("103.10.1.221", 21, "taibaoftp", "Yiyao@0425");
        if(b){
            List<String> list=new ArrayList<>();
            list= List(list,"/waitReadFolder/");
            for (String s : list) {
                //下载至本地临时文件
                System.out.println("D:\\"+s.substring(s.lastIndexOf("/")+1));
                boolean flag = downFile(s.substring(s.lastIndexOf("/")+1), s, "D:\\");
                System.out.println(flag);
                if(flag){
                    renameFile(s,"/readFolder/"+s.substring(s.lastIndexOf("/")+1));// 绝对路径
                }
                 //读取且传入库中
                //////
                List<String> strings=  FileUtils.getFileContext("D:\\"+s.substring(s.lastIndexOf("/")+1));
                for (String str : strings) {
                    String[] split=str.split("\\|",-1);
                    PolicyInfo policyInfo=new PolicyInfo(split);
                    System.out.println(policyInfo);
                }
                //存入库中后删除本地临时文件
                File file = new File("D:\\"+s.substring(s.lastIndexOf("/")+1));
                if (file.exists()) {
                    file.delete();
                }
            }

        }
        close();
        *//*

        boolean b = ftpConnection("rsxlp-ft.cpic.com.cn", 18022, "dcos", "QAZplm1234!");
        System.out.println(b);
//        上传
       */
/* boolean b = ftpConnection("103.10.1.221", 21, "taibaoftp", "Yiyao@0425");
        if(b){
            try {
                FileInputStream in = new FileInputStream(new File("D:\\result.zip"));
                boolean flag = storeFile( "/advancePaymentResultFolder", "result.zip", in);
                System.out.println(flag);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }*//*




    }
*/

}
