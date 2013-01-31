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
package au.edu.monash.merc.capture.ws.client.qa;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import au.edu.monash.merc.capture.dto.ActivityBean;
import au.edu.monash.merc.capture.ws.client.rm.AndsRMWSClient;

public class WSActivityTest implements TestConstant {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public void callActivityWS(String activityId, int times) {
		logger.info(format_pattern1 + " Research Master Web Service - QA - Report " + format_pattern1);

		logger.info(format_pattern1 + " Test Activity Id: " + activityId + " , Call RM WS: " + times + "  times " + format_pattern1);
		logger.info("\n");
		for (int i = 0; i < times; i++) {
			logger.info("\n" + format_pattern1 + format_pattern1 + " Testing No: " + (i + 1) + " " + format_pattern1 + format_pattern1 + "\n");
			logger.info("\n" + format_pattern1 + " GetActivityRegistryObject WS Action " + format_pattern1 + " Status " + format_pattern1
					+ " Comments " + format_pattern1 + "\n");
			long startTime = System.currentTimeMillis();
			activityws(activityId);
			long endTime = System.currentTimeMillis();
			logger.info(format_pattern1 + format_pattern1 + " Total WS Times: " + (endTime - startTime) / 1000 + " Seconds " + format_pattern1
					+ format_pattern1);
		}
	}

	private void activityws(String activityId) {
		try {
			AndsRMWSClient ws = new AndsRMWSClient();
			ws.setTimeout(30000);
			ws.serviceInit();

			ActivityBean ab = ws.getActivityRegistryObject(activityId);
			logger.error(format_pattern3 + "getActivityRegistryObject(\"" + activityId + "\")" + format_pattern3 + "success" + format_pattern2
					+ ab.getActivityKey() + " - " + ab.getNamePartValue());
		} catch (Exception e) {
			logger.error(format_pattern3 + "getActivityRegistryObject(\"" + activityId + "\")" + format_pattern3 + "failure" + format_pattern2
					+ e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("\nGet Activity WS testing ...");
		System.out.print("\nPlease enter the activity summary id: ");
		InputStreamReader keyInput = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(keyInput);
		String activityId = in.readLine();

		System.out.println("");
		boolean validNum = true;
		int times = 0;
		String calltimes = null;
		do {
			try {
				System.out.print("Please enter how many times you want to call rm ws: ");

				keyInput = new InputStreamReader(System.in);
				in = new BufferedReader(keyInput);

				calltimes = in.readLine();
				times = Integer.valueOf(calltimes);
				validNum = true;
			} catch (Exception e) {
				System.err.println("Invalid number: " + calltimes + "\n");
				validNum = false;
			}

		} while (!validNum);
		WSActivityTest test = new WSActivityTest();
		test.callActivityWS(activityId, times);
	}

}
