package co.yixiang.tools.utils;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author long.yu
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */

import co.yixiang.utils.StringUtils;

import java.lang.ref.SoftReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class DateUtil {

    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    public static final String SIMPLE_NEW_PATTERN = "yyyy-MM-dd HH:mm";

    public static final String SIMPLE_PATTERN = "yyyy-MM-dd";

    public static final String MILLISECOND_PATTERN = "yyyy-MM-dd HH:mm:ss.S";

    /**
     * 英文简写（默认）如：2010-12
     */
    public static final String FORMAT_SHORT_01 = "yyyy-MM";

    public static final String NEW_PATTERN = "yyyyMMdd";

    /**
     * 一月的开始
     */
    public static final String MON_START = "-01 00:00:00";

    public static Date parseDate(String date) throws ParseException {
        return parseDate(date, DEFAULT_PATTERN);
    }

    public static Date parseDate(String date, String pattern) throws ParseException {
        SimpleDateFormat formatter = DateFormatHolder.formatFor(pattern);
        return formatter.parse(date);
    }

    public static String formatDate(Date date) {
        return formatDate(date, DEFAULT_PATTERN);
    }

    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            throw new IllegalArgumentException("date is null");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("pattern is null");
        }

        SimpleDateFormat formatter = DateFormatHolder.formatFor(pattern);
        return formatter.format(date);
    }

    private DateUtil() {
    }

    final static class DateFormatHolder {

        private static final ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>> THREADLOCAL_FORMATS = new ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>>() {

            @Override
            protected SoftReference<Map<String, SimpleDateFormat>> initialValue() {
                return new SoftReference<Map<String, SimpleDateFormat>>(new HashMap<String, SimpleDateFormat>());
            }

        };

        public static SimpleDateFormat formatFor(String pattern) {
            SoftReference<Map<String, SimpleDateFormat>> ref = THREADLOCAL_FORMATS.get();
            Map<String, SimpleDateFormat> formats = ref.get();
            if (formats == null) {
                formats = new HashMap<String, SimpleDateFormat>();
                THREADLOCAL_FORMATS.set(new SoftReference<Map<String, SimpleDateFormat>>(formats));
            }

            SimpleDateFormat format = formats.get(pattern);
            if (format == null) {
                format = new SimpleDateFormat(pattern);
                formats.put(pattern, format);
            }

            return format;
        }

    }

    public static String formatMinutes(int minutes) {
        if (minutes > 60) {
            int hour = minutes / 60;
            int min = minutes % 60;
            return hour + "小时" + (min > 0 ? min + "分钟" : "");
        } else {
            return minutes + "分钟";
        }
    }

    /**
     * 功能描述: 获取日期的开始时间<br>
     *
     * @param date
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static Date getDateStart(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = sdf.parse(formatDate(date, "yyyy-MM-dd") + " 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getDatePattern() {
        return DEFAULT_PATTERN;
    }

    /**
     * 获取本月开始时间
     *
     * @return
     */
    public static Date getMonStart() {
        return parse(getNow(DateUtil.FORMAT_SHORT_01) + MON_START);
    }

    public static Date getMonthEnd() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        String last = format.format(ca.getTime());
        String lastTime = last + " 23:59:59";
        try {
            return parseDate(lastTime, DateUtil.DEFAULT_PATTERN);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据用户格式返回当前日期
     *
     * @param format
     * @return
     */
    public static String getNow(String format) {
        return format(new Date(), format);
    }

    /**
     * 使用用户格式格式化日期
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return
     */
    public static String format(Date date, String pattern) {
        String returnValue = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return returnValue;
    }

    /**
     * 使用预设格式提取字符串日期
     *
     * @param strDate 日期字符串
     * @return
     */
    public static Date parse(String strDate) {
        return parseDate02(strDate, getDatePattern());
    }

    /**
     * 使用用户格式提取字符串日期
     *
     * @param strDate 日期字符串
     * @param pattern 日期格式
     * @return
     */
    public static Date parseDate02(String strDate, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 功能描述: 获取日期的结束时间<br>
     *
     * @param date
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static Date getDateEnd(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = sdf.parse(formatDate(date, "yyyy-MM-dd") + " 23:59:59");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 功能描述: 获取日期的结束时间<br>
     *
     * @param date
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static Date getDateEnd2(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = sdf.parse(formatDate(addDay(date, 1), "yyyy-MM-dd") + " 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 判断是否处于某一时间段内 半开半闭区间
     *
     * @param start
     * @param end
     * @return
     */
    public static boolean between(Integer start, Integer end) {
        Calendar cal = Calendar.getInstance();
        Integer hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour < start || hour >= end) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 小轨在线时间，8:30--20:00
     *
     * @return
     */
    public static boolean crsOnline() {
        Integer start = 8;
        Integer end = 20;
        Calendar cal = Calendar.getInstance();
        Integer hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour < start || hour >= end) {
            return false;
        } else {
            if (hour.equals(start)) {
                Integer min = cal.get(Calendar.MINUTE);
                if (min < 30) {
                    return false;
                }
            }
            return true;
        }

    }

    /**
     * 获取每天零点的日期
     *
     * @return
     */
    public static Date getZeroDate() {

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return today.getTime();
    }

    public static boolean isSpringFestival() {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2016);
        cal.set(Calendar.MONTH, 3);
        cal.set(Calendar.DATE, 30);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startTime = cal.getTime();
        cal.set(Calendar.MONTH, 4);
        cal.set(Calendar.DATE, 2);
        Date endTime = cal.getTime();
        return now.after(startTime) && now.before(endTime);
    }

    /**
     * 获取每周一零点的日期
     *
     * @return
     */
    public static Date getDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 在日期上增加数个整月
     *
     * @param date 日期
     * @param n    要增加的月数
     * @return
     */
    public static Date addMonth(Date date, int n) {
        return dateAdd(date, Calendar.MONTH, n);
    }

    /**
     * 获取固定年、月、日的日期
     *
     * @return
     */
    public static Date getDate(final int year, final int month, final int day) {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);// month从0-11,所以减去1
        cal.set(Calendar.DATE, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取日期的LINUX时间戳
     *
     * @throws ParseException
     */
    public static long getLinuxTimestamp(final Date date, final String pattern) throws ParseException {
        return getLinuxTimestamp(formatDate(date, pattern), pattern);
    }

    /**
     * 获取日期的LINUX时间戳
     *
     * @throws ParseException
     */
    public static long getLinuxTimestamp(final String dateStr) throws ParseException {
        return parseDate(dateStr).getTime() / 1000;
    }

    /**
     * 获取日期的LINUX时间戳
     *
     * @throws ParseException
     */
    public static long getLinuxTimestamp(final String dateStr, final String pattern) throws ParseException {
        return parseDate(dateStr, pattern).getTime() / 1000;
    }

    /**
     * 获取日期的LINUX时间戳
     *
     * @throws ParseException
     */
    public static long getLinuxTimestamp(final Date date) throws ParseException {
        return getLinuxTimestamp(date, DEFAULT_PATTERN);
    }

    /**
     * LINUX时间戳转换成日期
     */
    public static Date parseLinuxTime2Date(final long linuxTime) {
        return new Date(linuxTime * 1000);
    }

    /**
     * 秒转换为时：分：秒
     *
     * @param second
     */
    public static String formatSecond(Integer second) {

        if (second == null) {
            return "0";
        }
        String format;
        Object[] array;
        int hours = second / (60 * 60);
        int minutes = second / 60 - hours * 60;
        int seconds = second - minutes * 60 - hours * 60 * 60;
        format = "%1$02d:%2$02d:%3$02d";
        array = new Object[]{hours, minutes, seconds};
        // if (hours > 0) {
        // } else if (minutes > 0) {
        // format = "%1$,d:%2$,d";
        // array = new Object[] {minutes, seconds};
        // } else {
        // format = "%1$,d";
        // array = new Object[] {seconds};
        // }

        return String.format(format, array);

    }

    // 判断两个时间相差多少天
    public static int getSpacingDays(Date end, Date start) {
        if (start == null) {
            return 0;
        }
        return (int) ((end.getTime() - start.getTime()) / 1000 / 60 / 60 / 24);
    }

    /**
     * 客服国庆消息推送
     */
    public static boolean isGuoQingFestival() {
        final Calendar cal = Calendar.getInstance();
        if (cal.get(Calendar.MONTH) != 9) {
            return false;
        }
        if (cal.get(Calendar.DAY_OF_MONTH) >= 8) {
            return false;
        }
        if (cal.get(Calendar.DAY_OF_MONTH) == 1 || cal.get(Calendar.DAY_OF_MONTH) == 2
                || cal.get(Calendar.DAY_OF_MONTH) == 3) {
            return true;
        }

        if (cal.get(Calendar.HOUR_OF_DAY) < 9 || cal.get(Calendar.HOUR_OF_DAY) >= 18) {
            return true;
        }
        return false;
    }

    public static boolean compareDate(Date start, Date end, Date date) {
        if (date.getTime() >= start.getTime() && date.getTime() <= end.getTime()) {
            return true;
        }
        return false;
    }

    public static Date getPassDay(Integer passDay) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, passDay);// 把日期往后增加一天.整数往后推,负数往前移动
        Date dateResult = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        return dateResult;
    }

    /**
     * 获取特殊格式日期
     *
     * @param （天），不足24小时（小时），不足1小时（分钟） ， 道理开播时间 或者超过开播时间 （马上开播）；
     * @return
     */
    public static String getTimeSpecialStr(Long livingStartTime) {

        if (livingStartTime == null) {
            return null;
        }

        Long currentTime = System.currentTimeMillis();
        long nd = 1000 * 24 * 60 * 60;//一天的毫秒数
        long nh = 1000 * 60 * 60;//一小时的毫秒数
        long nm = 1000 * 60;//一分钟的毫秒数
        //获得两个时间的毫秒时间差异
        long diff = livingStartTime - currentTime;
        long day = diff / nd;//计算差多少天
        long hour = diff % nd / nh;//计算差多少小时
        long min = diff % nd % nh / nm;//计算差多少分钟

        if (diff <= 0) {
            return "马上开播";
        } else if (day > 0) {
            return "还有" + day + "天开播";
        } else if (hour > 0) {
            return "还有" + hour + "小时开播";
        } else if (min > 0) {
            return "还有" + min + "分钟开播";
        }

        return null;
    }

    /**
     * 指定日期加上天数后的日期
     *
     * @param num 为增加的天数
     * @return
     * @throws ParseException
     */
    public static Date plusDay(int num) {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DATE, num);// num为增加的天数，可以改变的
        Date currdate = ca.getTime();
        return currdate;
    }

    /**
     * 下个月的第一天
     *
     * @return
     */
    public static String getPerFirstDayOfMonth() {
        SimpleDateFormat dft = new SimpleDateFormat(SIMPLE_PATTERN);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return dft.format(calendar.getTime());
    }

    public static Date getTimeByHour(int hour) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + hour);

        return calendar.getTime();

    }

    // 判断两个时间相差多少天
    public static int getSpacingHour(Date end, Date start) {
        if (start == null) {
            return 0;
        }
        return (int) ((end.getTime() - start.getTime()) / 1000 / 60 / 60);
    }

    /**
     * 时间戳转日期
     *
     * @param ms
     * @return
     */
    public static Date transForDate(Long ms, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date temp = null;
        if (ms != null) {
            try {
                String str = sdf.format(ms);
                System.out.println(str);
                temp = sdf.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return temp;
    }

    /**
     * 在日期上增加天数
     *
     * @param date 日期
     * @param n    要增加的天数
     * @return
     */
    public static Date addDay(Date date, int n) {
        return dateAdd(date, Calendar.DATE, n);
    }

    /**
     * @param date
     * @param t
     * @param n
     * @return
     */
    public static Date dateAdd(Date date, int t, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(t, n);
        return cal.getTime();
    }

    public static String calculateTime(Date setTime) {
        long nowTime = System.currentTimeMillis(); // 获取当前时间的毫秒数
        String msg = "刚刚";
        long reset = setTime.getTime(); // 获取指定时间的毫秒数
        long dateDiff = nowTime - reset;

        if (dateDiff < 0) {
            msg = "刚刚";
        } else {
            long dateTemp1 = dateDiff / 1000; // 秒
            long dateTemp2 = dateTemp1 / 60; // 分钟
            long dateTemp3 = dateTemp2 / 60; // 小时
            long dateTemp4 = dateTemp3 / 24; // 天数
            long dateTemp5 = dateTemp4 / 30; // 月数
            long dateTemp6 = dateTemp5 / 12; // 年数
            if (dateTemp6 > 0) {
                msg = dateTemp6 + "年前";

            } else if (dateTemp5 > 0) {
                msg = dateTemp5 + "个月前";

            } else if (dateTemp4 > 0) {
                msg = dateTemp4 + "天前";

            } else if (dateTemp3 > 0) {
                msg = dateTemp3 + "小时前";

            } else if (dateTemp2 > 0) {
                msg = dateTemp2 + "分钟前";

            } else if (dateTemp1 > 0) {
                msg = "刚刚";
            }
        }
        return msg;
    }

    public static String calculateTime2(Date setTime) {
        long nowTime = System.currentTimeMillis(); // 获取当前时间的毫秒数
        String msg = null;
        long reset = setTime.getTime(); // 获取指定时间的毫秒数
        long dateDiff = nowTime - reset;

        if (dateDiff < 0) {
            msg = "输入的时间不对";
        } else {
            long dateTemp = dateDiff / 1000 / 60 / 60 / 24;

            if (dateTemp == 0) {
                msg = "今天";
            } else if (dateTemp > 0 && dateTemp <= 1) {
                msg = "昨天";
            } else {
                msg = formatDate(setTime, "yyyy年MM月dd日");
            }
        }
        return msg;
    }

    /**
     * @param @param  date
     * @param @param  strDateBegin
     * @param @param  strDateEnd
     * @param @return 设定文件
     * @return boolean    返回类型
     * @throws
     * @Title: isInDate
     * @Description: 判断一个时间段（YYYY-MM-DD）是否在一个区间
     */
    public static boolean isInDate(Date date, String strDateBegin, String strDateEnd) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String strDate = sdf.format(date);   //2017-04-11
        // 截取当前时间年月日 转成整型
        int tempDate = Integer.parseInt(strDate.split("-")[0] + strDate.split("-")[1] + strDate.split("-")[2]);
        // 截取开始时间年月日 转成整型
        int tempDateBegin = Integer.parseInt(strDateBegin.split("-")[0] + strDateBegin.split("-")[1] + strDateBegin.split("-")[2]);
        // 截取结束时间年月日   转成整型
        int tempDateEnd = Integer.parseInt(strDateEnd.split("-")[0] + strDateEnd.split("-")[1] + strDateEnd.split("-")[2]);

        if ((tempDate >= tempDateBegin && tempDate <= tempDateEnd)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获得当前年和当前月组成的字符串，作为七牛云上的相对路径
     */
    public static String getYearAndMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        return year + "-" + month;
    }

    public static String getDateWeek() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
        }
        return null;
    }

    public static Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }

    public static Date geLastWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(date));
        cal.add(Calendar.DATE, -7);
        return cal.getTime();
    }

    public static Date getNextWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(date));
        cal.add(Calendar.DATE, 7);
        return cal.getTime();
    }

    /**
     * 传的时间进行加减传入的分钟数
     */
    public static Date addMinute(Date date, int h, int m) {
        long timeHour = h * 60 * 60 * 1000;
        long time = m * 60 * 1000;
        return new Date(date.getTime() + timeHour + time);
    }

    /**
     * 获取某年某月的第一天
     */
    public static String getFisrtDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String firstDayOfMonth = sdf.format(cal.getTime());

        return firstDayOfMonth;
    }

    /**
     * 获取某年某月的最后一天
     */
    public static String getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //获取某月最小天数
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String firstDayOfMonth = sdf.format(cal.getTime());

        return firstDayOfMonth;
    }

    /**
     * 获取日期年份
     *
     * @param date 日期
     * @return
     */
    public static String getYear(Date date) {
        String year = formatDate(date, SIMPLE_PATTERN);
        return year.substring(0, 4);
    }

    /**
     * 获取日期月份
     *
     * @param date 日期
     * @return
     */
    public static String getMonth(Date date) {
        String year = formatDate(date, SIMPLE_PATTERN);
        return year.substring(5, 7);
    }

    /**
     * 获取日期日
     *
     * @param date 日期
     * @return
     */
    public static String getDay(Date date) {
        String year = formatDate(date, SIMPLE_PATTERN);
        return year.substring(8);
    }

    /**
     * 获取日期日
     *
     * @param date 日期
     * @return
     */
    public static String getYearMonthDay(Date date) {
        String year = formatDate(date, SIMPLE_PATTERN);
        return year.substring(0,10);
    }
    /**
     * 当前日期，第N天的日期（负数表示N天前，正数表示N天后）
     *
     * @param amount
     * @return
     */
    public static Date addOrLessDay(int amount) {
        //当前前一天所有数据
        Date date = new Date();
        Date now = date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, amount);
        date = calendar.getTime();
        return date;
    }

    /**
     * 获得输入日期的第N天的日期（负数表示N天前，正数表示N天后）
     * 用来遍历历史数据
     *
     * @param amount date
     * @return
     */
    public static Date addOrLessDayDate(int amount, Date date) {
        //当前前一天所有数据
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, amount);
        date = calendar.getTime();
        return date;
    }

    public static String randomDate(String startDate, String endDate) {
        try {
            Date start = parseDate(startDate, SIMPLE_PATTERN);
            Date end = parseDate(endDate, SIMPLE_PATTERN);
            if (start.getTime() >= end.getTime()) {
                return null;
            }
            long date = randomTime(start.getTime(), end.getTime());
            return format(new Date(date), SIMPLE_PATTERN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long randomTime(long start, long end) {
        long rnt = start + (long) (Math.random() * (end - start));
        if (rnt == start || rnt == end) {
            return randomTime(start, end);
        }
        return rnt;
    }

    private static SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat longHourSdf = new SimpleDateFormat("yyyy-MM-dd HH");
    private static SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 当前年的结束时间，即2012-12-31 23:59:59
     *
     * @return
     */
    public static Date getCurrentYearEndTime() {
        Calendar c = Calendar.getInstance();
        Date now = null;
        try {
            c.set(Calendar.MONTH, 11);
            c.set(Calendar.DATE, 31);
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 当前季度的结束时间，即2012-03-31 23:59:59
     *
     * @return
     */
    public static Date getCurrentQuarterEndTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3) {
                c.set(Calendar.MONTH, 2);
                c.set(Calendar.DATE, 31);
            } else if (currentMonth >= 4 && currentMonth <= 6) {
                c.set(Calendar.MONTH, 5);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 7 && currentMonth <= 9) {
                c.set(Calendar.MONTH, 8);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 10 && currentMonth <= 12) {
                c.set(Calendar.MONTH, 11);
                c.set(Calendar.DATE, 31);
            }
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 获取前/后半年的结束时间
     *
     * @return
     */
    public static Date getHalfYearEndTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 6) {
                c.set(Calendar.MONTH, 5);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 7 && currentMonth <= 12) {
                c.set(Calendar.MONTH, 11);
                c.set(Calendar.DATE, 31);
            }
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_PATTERN);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static Integer getAge(String birthDate){
        Integer age = 0;
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // 身份证上的年份
        String year = birthDate.substring(0, 4);
        // 身份证上的月份
        String yue = birthDate.substring(5).substring(0, 2);
        // 当前年份
        String fyear = format.format(date).substring(0, 4);
        // 当前月份
        String fyue = format.format(date).substring(5, 7);
        // 当前月份大于用户出身的月份表示已过生日
        if (Integer.parseInt(yue) <= Integer.parseInt(fyue)) {
            age = Integer.parseInt(fyear) - Integer.parseInt(year) + 1;
            // 当前用户还没过生日
        } else {
            age = Integer.parseInt(fyear) - Integer.parseInt(year);
        }


        return age;
    }


    public static void main(String[] args) {
        System.out.println(getHalfYearEndTime());
        System.out.println(getCurrentQuarterEndTime());
        System.out.println(getCurrentYearEndTime());
        String birthdate = "2016年09月";
        System.out.println(getAge("2021年09月"));

    }
}
