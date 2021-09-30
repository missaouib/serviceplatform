package co.yixiang.utils;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;

import java.io.File;
import java.net.ConnectException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OpenOfficeUtil {
    public static boolean officeToPDF(String sourceFile, String destFile) {
        try {

            File inputFile = new File(sourceFile);
            if (!inputFile.exists()) {
                // 找不到源文件, 则返回false
                return false;
            }
            // 如果目标路径不存在, 则新建该路径
            File outputFile = new File(destFile);
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }
            //如果目标文件存在，则删除
            if (outputFile.exists()) {
                outputFile.delete();
            }
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            OpenOfficeConnection connection = new SocketOpenOfficeConnection("10.10.10.31", 8100);
            connection.connect();
            //用于测试openOffice连接时间
            System.out.println("连接时间:" + df.format(new Date()));
            DocumentConverter converter = new StreamOpenOfficeDocumentConverter(
                    connection);
            converter.convert(inputFile, outputFile);
            //测试word转PDF的转换时间
            System.out.println("转换时间:" + df.format(new Date()));
            connection.disconnect();
            return true;
        } catch (ConnectException e) {
            e.printStackTrace();
            System.err.println("openOffice连接失败！请检查IP,端口");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        officeToPDF("E:\\c1.xlsx", "E:\\bba.pdf");
    }

}
