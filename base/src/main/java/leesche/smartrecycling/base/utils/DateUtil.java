package leesche.smartrecycling.base.utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import leesche.smartrecycling.base.entity.DevConfigEntity;

public class DateUtil {
    // 日期格式
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT2 = "yyyy_MM_dd";
    public static final String DATE_FORMAT3 = "yyyyMMdd";
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT4 = "MM/dd/yyyy";
    public static final String FORMAT_YYYY = "yyyy";
    public static final String FORMAT_HH_MM = "HH:mm";
    public static final String FORMAT_HH_MM3 = "HH_mm_ss_";
    public static final String FORMAT_HH_MM2 = "MM月dd号 HH:mm";
    public static final String FORMAT_HH_MM_SS = "HH:mm:ss";
    public static final String FORMAT_MM_SS = "mm:ss";
    public static final String FORMAT_MM_DD_HH_MM = "MM-dd HH:mm";
    public static final String FORMAT_MM_DD_HH_MM_SS = "MM-dd HH:mm:ss";
    public static final String FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_YYYY2MM2DD = "yyyy.MM.dd";
    public static final String FORMAT_YYYY2MM2DD_HH_MM = "yyyy.MM.dd HH:mm";
    public static final String FORMAT_MMCDD_HH_MM = "MM月dd日 HH:mm";
    public static final String FORMAT_MMCDD = "MM月dd日";
    public static final String FORMAT_YYYYCMMCDD = "yyyy年MM月dd日";
    public static final String FORMAT_ORDERID = "yyyyMMddHHmmss";
    public static final String FORMAT_DATE = "yyMMdd";
    public static final String TIME_FORMAT4 = "MM/dd/yyyy HH:mm";
    public static final String TIME_FORMAT5 = "dd/MM/yyyy HH:mm";
    public static long timestamp;

    static final private SimpleDateFormat sLogFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");

    static public String getLogStr() {
        return sLogFormat.format(new Date());
    }

    public static final long ONE_DAY = 1000 * 60 * 60 * 24;

    //判断选择的日期是否是本周（分从周日开始和从周一开始两种方式）
    public static boolean isThisWeek(Date time) {
//        //周日开始计算
//      Calendar calendar = Calendar.getInstance();

        //周一开始计算
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        calendar.setTime(time);

        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        if (paramWeek == currentWeek) {
            return true;
        }
        return false;
    }

    //判断选择的日期是否是今天
    public static boolean isToday(Date time) {
        return isThisTime(time, "yyyy-MM-dd");
    }

    public static boolean isToday(String time) {
        return isThisTime(time, "yyyyMMdd");
    }

    //判断选择的日期是否是本月
    public static boolean isThisMonth(Date time) {
        return isThisTime(time, "yyyy-MM");
    }

    //判断选择的日期是否是本月
    public static boolean isThisYear(Date time) {
        return isThisTime(time, "yyyy");
    }

    //判断选择的日期是否是昨天
    public static boolean isYesterDay(Date time) {
        Calendar cal = Calendar.getInstance();
        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        if ((ct - lt) == 1) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isThisTime(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        String param = sdf.format(date);//参数时间

        String now = sdf.format(new Date());//当前时间

        if (param.equals(now)) {
            return true;
        }
        return false;
    }

    private static boolean isThisTime(String date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String now = sdf.format(new Date());//当前时间

        if (date.equals(now)) {
            return true;
        }
        return false;
    }

    /**
     * 获取某年某月有多少天
     */
    public static int getDayOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, 0); //输入类型为int类型
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取两个时间相差的天数
     *
     * @return time1 - time2相差的天数
     */
    public static int getDayOffset(long time1, long time2) {
        // 将小的时间置为当天的0点
        long offsetTime;
        if (time1 > time2) {
            offsetTime = time1 - getDayStartTime(getCalendar(time2)).getTimeInMillis();
        } else {
            offsetTime = getDayStartTime(getCalendar(time1)).getTimeInMillis() - time2;
        }
        return (int) (offsetTime / ONE_DAY);
    }

    public static Calendar getCalendar(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar;
    }

    public static Calendar getDayStartTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static String getDurationInString(long time) {
        String durStr = "";
        if (time == 0) {
            return "0秒";
        }
        time = time / 1000;
        long hour = time / (60 * 60);
        time = time - (60 * 60) * hour;
        long min = time / 60;
        time = time - 60 * min;
        long sec = time;
        if (hour != 0) {
            durStr = hour + "时" + min + "分" + sec + "秒";
        } else if (min != 0) {
            durStr = min + "分" + sec + "秒";
        } else {
            durStr = sec + "秒";
        }
        return durStr;
    }

    /**
     * 获取当前时间是星期几
     *
     * @param dt
     * @return
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"Sun.", "Mon.", "Tue.", "Wed.", "Thur.", "Fri.", "Sat."};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static String getCurDate_en() {
        String[] weekDays = {"Sun.", "Mon.", "Tue.", "Wed.", "Thur.", "Fri.", "Sat."};
        String week = weekDays[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1];
        String[] months = {"Jun", "Feb", "Mar", "Apr", "May", "Jun", "Jan", "Aug", "Sept", "Oct", "Nov", "Dec"};
        String month = months[Calendar.getInstance().get(Calendar.MONTH)];
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        return week + "|" + day + "|" + month;
    }

    public static String getCurDate_ch() {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        String week = weekDays[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1];
        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
        String month = months[Calendar.getInstance().get(Calendar.MONTH)];
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        return week + "|" + day + "|" + month;
    }


    /**
     * 将日期格式的字符串转换为长整型
     *
     * @param date
     * @param format
     * @return
     */
    public static long convertToLong(String date, String format) {
        try {
            if (!TextUtils.isEmpty(date)) {
                if (TextUtils.isEmpty(format)) {
                    format = TIME_FORMAT;
                }
                SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
                return formatter.parse(date).getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 将长整型数字转换为日期格式的字符串
     *
     * @param time
     * @return
     */
    public static String convertToString(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        if (time == 0) {
            return formatter.format(new Date());
        }
        Date date = new Date(time);
        return formatter.format(date);
    }

    /**
     * 获取今天
     *
     * @return String
     */
    public static String getToday() {
        return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date());
    }

    public static String getToday2() {
        return new SimpleDateFormat(DATE_FORMAT2, Locale.getDefault()).format(new Date());
    }

    public static String getTodayFormat(String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date());
    }

    /**
     * 获取当前日期时间
     */
    public static String getPowerCurTime() {
        return new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM, Locale.getDefault()).format(new Date());
    }

    /**
     * 通过时间格式来获取当前时间
     */
    public static String getCurTime(String formatTime) {
        return new SimpleDateFormat(formatTime, Locale.getDefault()).format(new Date());
    }

    /**
     * 获取明天
     *
     * @return String
     */
    public static String getPowerOnTime(String _time) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time) + " " + _time;
    }

    /**
     * 获取关机时间
     *
     * @return String
     */
    public static String getPowerOffTime(String _time) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " " + _time;
    }

    public static String getTimeByFormat(String formatTime) {
        Date date = new Date();
        timestamp = date.getTime() / 1000;
        return new SimpleDateFormat(formatTime).format(date);
    }

    public static String getTimeByFormat(String formatTime, Date date) {
        return new SimpleDateFormat(formatTime).format(date);
    }

    public static long getTimestamp() {
        return timestamp;
    }

    /**
     * 获取昨天
     *
     * @return String
     */
    public static String getYesterday(String formatTime) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date time = cal.getTime();
        return new SimpleDateFormat(formatTime).format(time);
    }

    /**
     * 获取前天
     *
     * @return String
     */
    public static String getBeforeYestoday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time);
    }

    public static String getDateStr(int diff) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -(diff - 1));
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time);
    }

    public static String getMouthOfFirstDayStr(int diff) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, diff);//设置为1号,当前日期既为本月第一天
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyyMMdd").format(time);
    }

    public static String transTimeSecondToDateTime(int year, int addSecond) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, 1, 1);
        cal.add(Calendar.SECOND, addSecond);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy/MM/dd HH:ss").format(time);
    }

    public static String getTimeDiffToday(int timeDiff) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, timeDiff);
        Date time = cal.getTime();
        return new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM).format(time);
    }

    /**
     * 判断当前系统时间是否在指定时间的范围内
     *
     * @param beginHour 开始小时，例如22
     * @param beginMin  开始小时的分钟数，例如30
     * @param endHour   结束小时，例如 8
     * @param endMin    结束小时的分钟数，例如0
     * @return true表示在范围内，否则false
     */
    public static boolean isCurrentInTimeScope(int beginHour, int beginMin, int endHour, int endMin) {
        Calendar cal = Calendar.getInstance();// 当前日期
        int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
        int minute = cal.get(Calendar.MINUTE);// 获取分钟
        int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
        final int start = beginHour * 60 + beginMin;// 起始时间 8:30的分钟数
        final int end = endHour * 60 + endMin;// 结束时间 18:00的分钟数
        return minuteOfDay >= start && minuteOfDay <= end;
    }

    public static boolean isCurrentInTimeScope(String fromTime, String toTime) {
        String[] startTimeS = fromTime.split(":");
        String[] endTimeS = toTime.split(":");
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int minuteOfDay = hour * 60 + minute;
        final int start = Integer.parseInt(startTimeS[0]) * 60 + Integer.parseInt(startTimeS[1]);
        final int end = Integer.parseInt(endTimeS[0]) * 60 + Integer.parseInt(endTimeS[1]);
        return minuteOfDay >= start && minuteOfDay <= end;
    }

    public static boolean isEffectiveDate(String timePeriodStr) {
        String[] timePeriod = timePeriodStr.split("-");
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        try {
            String dateStr = getTodayFormat(DATE_FORMAT);
            Date startTime = sdf.parse(dateStr + " " + timePeriod[0]);
            Date endTime = sdf.parse(dateStr + " " + timePeriod[1]);
            Date nowTime = new Date();
            if (nowTime.getTime() == startTime.getTime()
                    || nowTime.getTime() == endTime.getTime()) {
                return true;
            }
            Calendar date = Calendar.getInstance();
            date.setTime(nowTime);
            Calendar begin = Calendar.getInstance();
            begin.setTime(startTime);
            Calendar end = Calendar.getInstance();
            end.setTime(endTime);
            return date.after(begin) && date.before(end);
        } catch (Exception e) {
//            Logger.i("【判断分类价格时间段】 " + e.getMessage());
        }
        return false;
    }

    public static boolean isCurrentEffectiveDate(String dateStr) {
//        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
//        String curDate = sdf.format(new Date());
//        Logger.i("【日期比对】 显示日期：" + dateStr + " 系统日期：" + curDate);
        return dateStr.equals("1970-01-01");
    }

    public static long getTimeMillis(String strTime) {
        long returnMillis = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM);
        Date d = null;
        try {
            d = sdf.parse(strTime);
            returnMillis = d.getTime();
        } catch (ParseException e) {

        }
        return returnMillis;
    }

    public static long getTimeMillis(String strTime, String format) {
        long returnMillis = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date d = null;
        try {
            d = sdf.parse(strTime);
            returnMillis = d.getTime();
        } catch (ParseException e) {

        }
        return returnMillis;
    }

    public static long getTimeExpend(String startTime, String endTime) {
        long longStart = getTimeMillis(startTime); //获取开始时间毫秒数
        long longEnd = getTimeMillis(endTime);  //获取结束时间毫秒数
        long longExpend = longEnd - longStart;  //获取时间差

        long longHours = longExpend / (60 * 60 * 1000); //根据时间差来计算小时数
        long longMinutes = (longExpend - longHours * (60 * 60 * 1000)) / (60 * 1000);   //根据时间差来计算分钟数

        return longHours * 60 * 60 + longMinutes * 60;
    }

    public static long getTimeExpendToUpdate(String startTime, String endTime, String timeFormat) {
        long longStart = getTimeMillis(startTime, timeFormat); //获取开始时间毫秒数
        long longEnd = getTimeMillis(endTime, timeFormat);  //获取结束时间毫秒数
        long longExpend = longEnd - longStart;  //获取时间差

        long longHours = longExpend / (60 * 60 * 1000); //根据时间差来计算小时数
        long longMinutes = (longExpend - longHours * (60 * 60 * 1000)) / (60 * 1000);   //根据时间差来计算分钟数

        return longHours * 60 * 60 + longMinutes * 60;
    }

    public static long getTimeMillis2(String strTime) {
        long returnMillis = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_ORDERID);
        Date d = null;
        try {
            d = sdf.parse(strTime);
            returnMillis = d.getTime();
        } catch (ParseException e) {

        }
        return returnMillis;
    }

    public static long getTimeExpend2(String startTime) {
        long longStart = getTimeMillis2(startTime); //获取开始时间毫秒数
        long longEnd = new Date().getTime();
        long longExpend = longEnd - longStart;  //获取时间差

        long longHours = longExpend / (60 * 60 * 1000); //根据时间差来计算小时数
        long longMinutes = (longExpend - longHours * (60 * 60 * 1000)) / (60 * 1000);   //根据时间差来计算分钟数
        long timeExpend = longHours * 60 * 60 + longMinutes * 60;
        return timeExpend;
    }

    public static long getTimeExpend3(long longStart) {
        long longEnd = new Date().getTime();
        long longExpend = longEnd - longStart;  //获取时间差

        long longHours = longExpend / (60 * 60 * 1000); //根据时间差来计算小时数
        long longMinutes = (longExpend - longHours * (60 * 60 * 1000)) / (60 * 1000);   //根据时间差来计算分钟数
        long timeExpend = longHours * 60 * 60 + longMinutes * 60;
        return timeExpend;
    }

    public static String timeStamp2DateStr(int unixTimestamp) {
        long timestamp = (long) (unixTimestamp * 1000);
        String date = new SimpleDateFormat(TIME_FORMAT).format(new Date(timestamp));
        return date;
    }

    public static String addDate(String day, int x) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");//24小时制
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//12小时制
        Date date = null;
        try {
            date = format.parse(day);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (date == null) return "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, 24);//24小时制
        //cal.add(Calendar.HOUR, x);12小时制
        date = cal.getTime();
        System.out.println("front:" + date);
        cal = null;
        return format.format(date);
    }

    public static int getDiffFormToday(String minDateStr) {
        int day = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date minDate = null;
        Date maxDate = null;
        try {
            minDate = sdf.parse(minDateStr);
            maxDate = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(minDate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(maxDate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        if (between_days > 0) {
            day = Integer.parseInt(String.valueOf(between_days));
        }
        return day;
    }

    public static int getDiff2FormToday(String dateStr, String format) {
        int day = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date minDate = null;
        Date maxDate = null;
        try {
            minDate = sdf.parse(dateStr);
            maxDate = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(minDate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(maxDate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        if (between_days > 0) {
            day = Integer.parseInt(String.valueOf(between_days));
        }
        return day;
    }

    public static int getDiffFormToday(String minDateStr, String maxDateStr) {
        int day = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date minDate = null;
        Date maxDate = null;
        try {
            minDate = sdf.parse(minDateStr);
            maxDate = sdf.parse(maxDateStr);
        } catch (ParseException e) {
//            Logger.i("人脸识别引擎激活：" + maxDateStr);
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(minDate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(maxDate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        if (between_days > 0) {
            day = Integer.parseInt(String.valueOf(between_days));
        }
        return day;
    }

    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM);
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(strDate, pos);
    }

    public static String changeUtcToBeijing(String utcStr) {
        try {
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            date = sdf.parse(utcStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 8);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isStsExpired(String updateStsServer) {
        if (TextUtils.isEmpty(updateStsServer)) {
            return true;
        }
        long curTime = new Date().getTime();
        long expiredTime = DateUtil.getTimeMillis(updateStsServer);
        long leftTime = (expiredTime - curTime) / (60 * 1000);
        return leftTime <= 5;
    }


    public static String generateUniqueKey() {
        Random random = new Random();
        // 随机数的量 自由定制，这是9位随机数
        Integer r = random.nextInt(900000000) + 100000000;
        // 返回  13位时间
        Long timeMillis = System.currentTimeMillis();
        // 返回  17位时间
        DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timeStr = sdf.format(new Date());
        // 13位毫秒+9位随机数
        ///return  timeMillis + String.valueOf(r);
        // 17位时间+9位随机数
        return timeStr + r;
    }

    public static String beforeSameSecondToNowDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, -15);
        return getTimeByFormat(TIME_FORMAT, calendar.getTime());
    }

}
