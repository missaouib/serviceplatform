package co.yixiang.mp.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PdfServiceImpl {
    @Value("${file.path}")
    private String path;

    public void generatePdf() {
        String newFileName = path +  "rochesmapdf" + File.separator + "agreement-"+  DateUtil.today() + ".pdf";

        if(FileUtil.exist(newFileName)) {
            return;
        }

        try{
            Map<String, Object> data = new HashMap<>();//要插入的数据
            data.put("fille_1", DateUtil.today());
            //  data.put("fille_2", "隔壁老李");
            //初始化itext
            //设置编码
            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

            String fileName = path + "static/textpdf/agreement.pdf";

            PdfReader pdfReader = new PdfReader(fileName);

            log.info("rochesmapdf filePath ={}",newFileName);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(newFileName));
            AcroFields form = pdfStamper.getAcroFields();
            form.addSubstitutionFont(baseFont);

            //写入数据
            for (String key : data.keySet()) {
                String value = data.get(key).toString();
                //key对应模板数据域的名称
                form.setField(key, value);
            }

            //还要将图片添加到指定的key文本域中
       /* int pageNo = form.getFieldPositions("fill_11_2").get(0).page;
        Rectangle signRect = form.getFieldPositions("fill_11_2").get(0).position;
        float x = signRect.getLeft();
        float y = signRect.getBottom();
        //要添加的图片地址 C:\Users\Administrator\Desktop\6.jpeg
        Image image = Image.getInstance("C:\\Users\\Administrator\\Desktop\\6.jpeg");
        PdfContentByte under = pdfStamper.getOverContent(pageNo);
        //设置图片大小
        image.scaleAbsolute(signRect.getWidth(), signRect.getHeight());
        //设置图片位置
        image.setAbsolutePosition(x, y);
        under.addImage(image);*/

            //设置不可编辑
            pdfStamper.setFormFlattening(true);
            pdfStamper.close();
        }catch (IOException e) {
            e.printStackTrace();
        }catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args)throws IOException, DocumentException {
        Map<String, Object> data = new HashMap<>();//要插入的数据
        data.put("fille_1", "隔壁老王");
        data.put("fille_2", "隔壁老李");
        //初始化itext
        //设置编码
        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

        PdfReader pdfReader = new PdfReader("C:\\generate\\agreement.pdf");

        PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream("C:\\generate\\agreement-update.pdf"));
        AcroFields form = pdfStamper.getAcroFields();
        form.addSubstitutionFont(baseFont);

        //写入数据
        for (String key : data.keySet()) {
            String value = data.get(key).toString();
            //key对应模板数据域的名称
            form.setField(key, value);
        }

        //还要将图片添加到指定的key文本域中
       /* int pageNo = form.getFieldPositions("fill_11_2").get(0).page;
        Rectangle signRect = form.getFieldPositions("fill_11_2").get(0).position;
        float x = signRect.getLeft();
        float y = signRect.getBottom();
        //要添加的图片地址 C:\Users\Administrator\Desktop\6.jpeg
        Image image = Image.getInstance("C:\\Users\\Administrator\\Desktop\\6.jpeg");
        PdfContentByte under = pdfStamper.getOverContent(pageNo);
        //设置图片大小
        image.scaleAbsolute(signRect.getWidth(), signRect.getHeight());
        //设置图片位置
        image.setAbsolutePosition(x, y);
        under.addImage(image);*/

        //设置不可编辑
        pdfStamper.setFormFlattening(true);
        pdfStamper.close();
    }
}
