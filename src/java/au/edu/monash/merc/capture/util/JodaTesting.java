/*
 * Copyright (c) 2010-2013, Monash e-Research Centre
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

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.DurationFieldType;
import org.joda.time.Instant;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author Simon Yu
 *         <p/>
 *         Email: xiaoming.yu@monash.edu
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        Date: 17/04/13 11:35 AM
 */
public class JodaTesting {

    public static void main(String[] args) throws Exception {
        try {
            new JodaTesting().run();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private void run() {
        runInstant();
        System.out.println();
        runDateTime();
        System.out.println();
    }

    private void runInstant() {
        System.out.println("Instant");
        System.out.println("=======");
        System.out.println("Instant stores a point in the datetime continuum as millisecs from 1970-01-01T00:00:00Z");
        System.out.println("Instant is immutable and thread-safe");
        System.out.println("                      in = new Instant()");
        Instant in = new Instant();
        System.out.println("Millisecond time:     in.getMillis():           " + in.getMillis());
        System.out.println("ISO string version:   in.toString():            " + in.toString());
        System.out.println("ISO chronology:       in.getChronology():       " + in.getChronology());
        System.out.println("UTC time zone:        in.getDateTimeZone():     " + in.getZone());
        System.out.println("Change millis:        in.withMillis(0):         " + in.withMillis(0L));
        System.out.println("");
        System.out.println("Convert to Instant:   in.toInstant():           " + in.toInstant());
        System.out.println("Convert to DateTime:  in.toDateTime():          " + in.toDateTime());
        System.out.println("Convert to MutableDT: in.toMutableDateTime():   " + in.toMutableDateTime());
        System.out.println("Convert to Date:      in.toDate():              " + in.toDate());
        System.out.println("");
        System.out.println("                      in2 = new Instant(in.getMillis() + 10)");
        Instant in2 = new Instant(in.getMillis() + 10);
        System.out.println("Equals ms and chrono: in.equals(in2):           " + in.equals(in2));
        System.out.println("Compare millisecond:  in.compareTo(in2):        " + in.compareTo(in2));
        System.out.println("Compare millisecond:  in.isEqual(in2):          " + in.isEqual(in2));
        System.out.println("Compare millisecond:  in.isAfter(in2):          " + in.isAfter(in2));
        System.out.println("Compare millisecond:  in.isBefore(in2):         " + in.isBefore(in2));
    }

    private void runDateTime() {
        System.out.println("DateTime");
        System.out.println("=======");
        System.out.println("DateTime stores a the date and time using millisecs from 1970-01-01T00:00:00Z internally");
        System.out.println("DateTime is immutable and thread-safe");
        System.out.println("                      in = new DateTime()");
        DateTime in = new DateTime();
        System.out.println("Millisecond time:     in.getMillis():           " + in.getMillis());
        System.out.println("ISO string version:   in.toString():            " + in.toString());
        System.out.println("ISO chronology:       in.getChronology():       " + in.getChronology());
        System.out.println("Your time zone:       in.getDateTimeZone():     " + in.getZone());
        System.out.println("Change millis:        in.withMillis(0):         " + in.withMillis(0L));
        System.out.println("");
        System.out.println("Get year:             in.getYear():             " + in.getYear());
        System.out.println("Get monthOfYear:      in.getMonthOfYear():      " + in.getMonthOfYear());
        System.out.println("Get dayOfMonth:       in.getDayOfMonth():       " + in.getDayOfMonth());
        System.out.println("...");
        System.out.println("Property access:      in.dayOfWeek().get():                   " + in.dayOfWeek().get());
        System.out.println("Day of week as text:  in.dayOfWeek().getAsText():             " + in.dayOfWeek().getAsText());
        System.out.println("Day as short text:    in.dayOfWeek().getAsShortText():        " + in.dayOfWeek().getAsShortText());
        System.out.println("Day in french:        in.dayOfWeek().getAsText(Locale.FRENCH):" + in.dayOfWeek().getAsText(Locale.FRENCH));
        System.out.println("Max allowed value:    in.dayOfWeek().getMaximumValue():       " + in.dayOfWeek().getMaximumValue());
        System.out.println("Min allowed value:    in.dayOfWeek().getMinimumValue():       " + in.dayOfWeek().getMinimumValue());
        System.out.println("Copy & set to Jan:    in.monthOfYear().setCopy(1):            " + in.monthOfYear().setCopy(1));
        System.out.println("Copy & add 14 months: in.monthOfYear().addCopy(14):           " + in.monthOfYear().addToCopy(14));
        System.out.println("Add 14 mnths in field:in.monthOfYear().addWrapFieldCopy(14):  " + in.monthOfYear().addWrapFieldToCopy(14));
        System.out.println("...");
        System.out.println("Convert to Instant:   in.toInstant():           " + in.toInstant());
        System.out.println("Convert to DateTime:  in.toDateTime():          " + in.toDateTime());
        System.out.println("Convert to MutableDT: in.toMutableDateTime():   " + in.toMutableDateTime());
        System.out.println("Convert to Date:      in.toDate():              " + in.toDate());
        System.out.println("Convert to Calendar:  in.toCalendar(Locale.UK): " + in.toCalendar(Locale.UK).toString().substring(0, 46));
        System.out.println("Convert to GregCal:   in.toGregorianCalendar(): " + in.toGregorianCalendar().toString().substring(0, 46));
        System.out.println("");
        System.out.println("                      in2 = new DateTime(in.getMillis() + 10)");
        DateTime in2 = new DateTime(in.getMillis() + 10);
        System.out.println("Equals ms and chrono: in.equals(in2):           " + in.equals(in2));
        System.out.println("Compare millisecond:  in.compareTo(in2):        " + in.compareTo(in2));
        System.out.println("Compare millisecond:  in.isEqual(in2):          " + in.isEqual(in2));
        System.out.println("Compare millisecond:  in.isAfter(in2):          " + in.isAfter(in2));
        System.out.println("Compare millisecond:  in.isBefore(in2):         " + in.isBefore(in2));


        Date day1 = CaptureUtil.formatDate("2013-03-06 00:00:01");

        Date day2 = CaptureUtil.formatDate("2014-04-06 23:59:59");


        DateTime dateTime = new DateTime(day1);
        System.out.println("== day1 date: " + dateTime);
        DateTime endDate = dateTime.plusMonths(18);
        System.out.println("== After 18 months:" + endDate);

        DateTime dateTime2 = new DateTime(day2);
        System.out.println("end date is after day2? " + endDate.isAfter(dateTime2));

        DateTime today = new DateTime();
        Days days = Days.daysBetween(today, dateTime2);
        System.out.println(days.get(DurationFieldType.days()));

        Date startDate = CaptureUtil.formatDate("2013-02-8 00:00:00");

        Date finalEndDate = CaptureUtil.formatDate("2013-07-10 00:00:00");

        Date currentDate = CaptureUtil.getToday();

        DateTime startDateTime = new DateTime(startDate);

        DateTime finalEndDateTime = new DateTime(finalEndDate);

        DateTime currentDateTime = new DateTime(currentDate);

        int numDaysFromTodayToMaxEndDate = Days.daysBetween(currentDateTime, finalEndDateTime).get(DurationFieldType.days());

        int gapDaysForDayOfMonth = currentDateTime.getDayOfMonth() - startDateTime.getDayOfMonth();


        System.out.println("=============== numDaysFromTodayToMaxEndDate: " + numDaysFromTodayToMaxEndDate);


        System.out.println("=============== today: " + currentDateTime);

        if (gapDaysForDayOfMonth >= 0) {
            int abGapDays = Math.abs(gapDaysForDayOfMonth) - 1;
            System.out.println("=============== gap days: " + gapDaysForDayOfMonth);
            if (numDaysFromTodayToMaxEndDate > (30 - abGapDays)) {
                System.out.println("1. default selected from today: " + (30 - abGapDays) + " , " + currentDateTime.plusDays(30 - abGapDays));
            } else {
                System.out.println("1. default selected is max end date");
            }
        } else {
            int abGapDays = Math.abs(gapDaysForDayOfMonth) + 1;
            System.out.println("=============== gap days: " + gapDaysForDayOfMonth);
            if (numDaysFromTodayToMaxEndDate > (30 + abGapDays)) {
                System.out.println("2. default selected from today: " + (30 + abGapDays) + " , " + currentDateTime.plusDays(30 + abGapDays));
            } else {
                System.out.println("2. default selected is max end date");
            }
        }

        System.out.println("===============> today is before today: " + currentDateTime.isBefore(currentDateTime));


    }

}
