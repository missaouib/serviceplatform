package co.yixiang.modules.taibao.util;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SFTPUtil {
    protected static Logger loggerMonitor = LoggerFactory.getLogger(SFTPUtil.class);

    /**
     * FTPClient对象
     **/
    private static ChannelSftp ftpClient  =new ChannelSftp();
    /**
     *
     */
    private static Session sshSession = null;

    /**
     * 连接服务器
     * @param host
     * @param port
     * @param userName
     * @param password
     * @return
     * @throws Exception
     */
    public static boolean ftpConnection(String host, int port, String userName, String password){
        try {
            JSch jsch = new JSch();
            // 获取sshSession
            sshSession = jsch.getSession(userName, host, port);
            // 添加s密码
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            // 开启sshSession链接
            sshSession.connect();
            // 获取sftp通道
            ftpClient = (ChannelSftp) sshSession.openChannel("sftp");
            // 开启
            ftpClient.connect();
            loggerMonitor.debug("success ..........");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 关闭
     *
     * @throws Exception
     */
    public static void close() throws Exception {
        loggerMonitor.debug("close............");
        try {
            ftpClient.disconnect();
            sshSession.disconnect();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new Exception("close stream error.");
        }
    }

    /**
     * 下载文件
     * @param ftpPath	服务器文件路径
     * @param savePath	下载保存路径
     * @param oldFileName	服务器上文件名
     * @param newFileName	保存后新文件名
     * @throws Exception
     */
    public static boolean downFile(String newFileName, String ftpPath, String savePath, String oldFileName)
            throws Exception {
        FileOutputStream fos = null;
        try {
            ftpClient.cd(ftpPath);
            File file = new File(savePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            String saveFile = savePath + newFileName;
            File file1 = new File(saveFile);
            fos = new FileOutputStream(file1);
            ftpClient.get(oldFileName, fos);
        } catch (Exception e) {
            loggerMonitor.error("下载文件异常............", e.getMessage());
            return  false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return  false;
                }
            }
        }
        return  true;
    }

    /**
     * 上传
     * @param input 上传文件流
     * @param ftpPath	服务器保存路径
     * @param newFileName	新文件名
     * @throws Exception
     */
    public static boolean storeFile(String ftpPath, String newFileName, FileInputStream input) throws Exception {
        try {
            ftpClient.cd(ftpPath);
            ftpClient.put(input, newFileName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 递归遍历出目录下面所有文件
     *
     * @param pathName 需要遍历的目录，必须以"/"开始和结束
     * @throws IOException
     */
    public static List<String> List(List<String> arFiles, String pathName) throws Exception {
        if (pathName.startsWith("/") && pathName.endsWith("/")) {
            //更换目录到当前目录
            ftpClient.cd(pathName);
            Vector<ChannelSftp.LsEntry> list = ftpClient.ls("*.txt");
            for (ChannelSftp.LsEntry entry :  list) {
                arFiles.add(pathName + entry.getFilename());
            }
        }
        return  arFiles;
    }

    /**
     *  移动文件
     * @param oldName  文件路径
     * @param newName  新路径
     * @return
     */
    public static Boolean renameFile(String oldName,String newName) {
        try {
            ftpClient.rename(oldName,newName);
        } catch (SftpException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除文件
     *
     * @param directory
     *            要删除文件所在目录
     * @param deleteFile
     *            要删除的文件
     */
    public static Boolean delete(String directory, String deleteFile) {
        try {
            ftpClient.cd(directory);
            ftpClient.rm(deleteFile);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static void main(String[] args) {
        try {
            ftpConnection("rsxlp-ft.cpic.com.cn", 18022, "dcos", "QAZplm1234!");
            close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}