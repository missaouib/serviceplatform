package co.yixiang.modules.yiyaobao.demo;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * @version 1.0
 * @author: Ma Ji
 * @since: 2009-11-23
 */
public class JsonDateValueProcessor implements JsonValueProcessor {
    private String format = "yyyy-MM-dd";

    public JsonDateValueProcessor() {
    }

    public JsonDateValueProcessor(String format) {
        this.format = format;
    }


    @Override
    public Object processArrayValue(Object value, JsonConfig jcf) {
        String[] obj = {};
        if (value instanceof Date[]) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date[] dates = (Date[]) value;
            obj = new String[dates.length];
            for (int i = 0; i < dates.length; i++) {
                obj[i] = sdf.format(dates[i]).trim();
            }
        }
        return obj;
    }
    @Override
    public Object processObjectValue(String key, Object value, JsonConfig jcf) {
        if (value instanceof Date) {
            String str = new SimpleDateFormat(format).format((Date) value);
            return str.trim();
        }
        return value == null ? null : value.toString();
    }
}

