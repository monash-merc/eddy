/*
 * Copyright (c) 2010-2011, Monash e-Research Centre
 * (Monash University, Australia)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of the Monash University nor the names of its
 * 	  contributors may be used to endorse or promote products derived from
 * 	  this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package au.edu.monash.merc.capture.util;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import au.edu.monash.merc.capture.struts2.action.ActConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;

import au.edu.monash.merc.capture.exception.DataCaptureException;

public class CaptureUtil {

    private static final Object lock = new Object();

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT2 = "yyyyMMddHHmmss";

    private static final String DATE_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private static final String DATE_W3CDTF = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private static final String DATE_YYYYMMDD = "yyyy-MM-dd";

    private static final String FIRST_TIME_OF_DAY = " 00:00:00";

    private static final String MMDDYYYY_DATE_FORMAT = "MM/dd/yy";

    public static Date formatDate(final String dateStr) {
        Date date = null;
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        try {
            date = df.parse(dateStr);
        } catch (ParseException e) {
            throw new DataCaptureException(e.getMessage());
        }
        return date;
    }

    public static Date formatMMDDYY(final String mmddyyStr) {
        Date date = null;
        DateFormat df = new SimpleDateFormat(MMDDYYYY_DATE_FORMAT);
        try {
            date = df.parse(mmddyyStr);
        } catch (ParseException e) {
            throw new DataCaptureException(e.getMessage());
        }
        return date;
    }

    public static Date getToday() {
        Date date = GregorianCalendar.getInstance().getTime();
        String endtimeStr = CaptureUtil.dateToYYYYMMDDStr(date);
        return formatDate(endtimeStr + FIRST_TIME_OF_DAY);
    }

    public static String formatDateToUTC(final Date date) {
        DateFormat simpleDateFormat = new SimpleDateFormat(DATE_UTC_FORMAT, Locale.US);
        return simpleDateFormat.format(date);
    }

    public static String formateDateToW3CDTF(final Date date) {
        SimpleDateFormat w3cdtf = new SimpleDateFormat(DATE_W3CDTF);
        return w3cdtf.format(date);
        //int index = s.length() - 2;
        //return s.substring(0, index) + ":" + s.substring(index);
    }

    public static String genCurrentTimestamp() {
        Date date = GregorianCalendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat(DATE_FORMAT2);
        return df.format(date);
    }

    public static boolean isToday(Date dateTime) {
        Calendar someCa = GregorianCalendar.getInstance();
        someCa.setTime(dateTime);

        int syear = someCa.get(Calendar.YEAR);
        int smonth = someCa.get(Calendar.MONTH);
        int sday = someCa.get(Calendar.DAY_OF_YEAR);

        Calendar currentCa = GregorianCalendar.getInstance();

        int cyear = currentCa.get(Calendar.YEAR);
        int cmonth = currentCa.get(Calendar.MONTH);
        int cday = currentCa.get(Calendar.DAY_OF_YEAR);
        if (syear == cyear && smonth == cmonth && sday == cday) {
            return true;
        }
        return false;
    }

    public static String dateToYYYYMMDDStr(Date dateTime) {
        SimpleDateFormat sdfDestination = new SimpleDateFormat("yyyy-MM-dd");
        return sdfDestination.format(dateTime);
    }

    public static String dateToYYYY(Date dateTime) {
        SimpleDateFormat sdfDestination = new SimpleDateFormat("yyyy");
        return sdfDestination.format(dateTime);
    }

    public static String generateIdBasedOnTimeStamp() {
        String suffix = null;
        synchronized (lock) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                // ignore whatever
            }
            // Long time = new Long("12219292800000");
            Long time = System.currentTimeMillis();
            long currentTime = new Date().getTime() + time.longValue();
            suffix = encode(currentTime);
        }
        return suffix;
    }

    private static String encode(long num) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < 9; i++) {
            buf.append(removeVowels(num % 31));
            num = num / 31;
        }
        return buf.toString().toLowerCase();
    }

    /**
     * Remove the vowels.
     *
     * @param num - The integer value.
     * @return a char.
     */
    private static char removeVowels(long num) {
        char formattedChar = 0;

        // System.out.println("num: = " + num);
        if (num < 10) {
            formattedChar = (char) (num + '0');
        } else if (num < 13) {
            formattedChar = (char) (num - 10 + 'B');
        } else if (num < 16) {
            formattedChar = (char) (num - 9 + 'B');
        } else if (num < 21) {
            formattedChar = (char) (num - 8 + 'B');
        } else if (num < 26) {
            formattedChar = (char) (num - 7 + 'B');
        } else {
            formattedChar = (char) (num - 6 + 'B');
        }
        return formattedChar;
    }

    public static boolean notGTFixedLength(final String str, int length) {
        if (StringUtils.isBlank(str)) {
            return true;
        }
        if (str.trim().length() > length) {
            return false;
        }
        return true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map<String, String> sortByValue(Map<String, String> map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        Map<String, String> result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put((String) entry.getKey(), (String) entry.getValue());
        }
        return result;
    }

    public static String normalizePath(String path) {
        if (StringUtils.endsWith(path, "/")) {
            return StringUtils.removeEnd(path, "/");
        } else {
            return path;
        }
    }

    public static String genMonUUID(String prefix) {
        UUID uuid = UUID.randomUUID();
        return prefix + uuid.toString();

    }

    public static String genUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static String pathEncode(String fileName) throws Exception {
        String encodedStr = URLEncoder.encode(fileName, "UTF-8");
        return encodedStr;
    }

    public static String normalizeCoverageStr(String coverage) {
        String tmp = StringUtils.remove(coverage, " ");
        tmp = StringUtils.remove(tmp, "\n");
        tmp = StringUtils.replace(tmp, "),(", " ");
        tmp = StringUtils.remove(tmp, "(");
        tmp = StringUtils.remove(tmp, ")");
        return tmp;
    }

    public static String replaceURLAmpsands(String url) {
        return StringUtils.replace(url, "&", "&amp;");
    }

    /**
     * Validate the email adress.
     *
     * @param email The email address.
     * @return true if it is a valid email address.
     */
    public static boolean validateEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }

    public static void main(String[] args) throws Exception {
        // for (int i = 0; i < 10; i++) {
        // System.out.println(CaptureUtil.generateIdBasedOnTimeStamp());
        // }
        //

        AtomicLong sequence = new AtomicLong(1000);

        for (int i = 0; i < 10; i++) {
            long somenum = sequence.incrementAndGet();
            System.out.println("====> " + somenum);

        }


        System.out.println("=========== get current time stamp: " + genCurrentTimestamp());
        System.out.println("=========== get today date: " + getToday());
        System.out.println(" Format the date: 2010-04-20 12:51:22 " + CaptureUtil.formatDate("2010-04-20 12:51:22"));

        System.out.println(" Format the date: 5/1/13 ->" + CaptureUtil.formatMMDDYY("5/1/13"));

        Date day = CaptureUtil.formatDate("2010-12-06 23:59:59");

        System.out.println(" Format the date: 2010-12-06 23:59:59 " + day);
        Date anotherDay = CaptureUtil.formatDate("1901-01-01 12:51:22:35");
        System.out.println(" UTC another date format: " + CaptureUtil.formatDateToUTC(anotherDay));
        System.out.println(" W3CDTF another date format: " + CaptureUtil.formateDateToW3CDTF(anotherDay));

        Date date = GregorianCalendar.getInstance().getTime();
        System.out.println(" W3CDTF current date format: " + CaptureUtil.formateDateToW3CDTF(date));

        System.out.println("is today? " + CaptureUtil.isToday(day));

        System.out.println("==date to string: " + dateToYYYYMMDDStr(day));

        System.out.println("==date to year string: " + dateToYYYY(day));

        String endwithSplashStr = "/opt/test/simontestdir";

        System.out.println(CaptureUtil.normalizePath(endwithSplashStr));

        System.out.println(CaptureUtil.genMonUUID("MON"));

        System.out.println("File name encoded str: " + CaptureUtil.pathEncode("/simon test/MON:3fcb74e1-28/?#=&;23-41f3-80eb-41799ca065f2.xml"));

        System.out.println("Party File name encoded: " + CaptureUtil.pathEncode("MON:0000015173.xml"));

        CaptureUtil
                .normalizeCoverageStr("(-16.078487909091955,      131.62366874999998\n)   ,   (-26.854409810258062, 119.14319999999998),\n(-30.704954868466302, 132.50257499999998),(-27.79141376887074, 143.04944999999998)");

        System.out.println(CaptureUtil.normalizePath("https://test.com.au/"));

        System.out.println(CaptureUtil
                .replaceURLAmpsands("http://localhost:8080/ands/pub/viewColDetails.jspx?collection.id=1&collection.owner.id=4&viewType=anonymous"));

    }
}
