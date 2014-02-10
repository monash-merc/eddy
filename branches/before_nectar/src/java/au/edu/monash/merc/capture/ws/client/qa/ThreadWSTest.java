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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import au.edu.monash.merc.capture.dto.ActivityBean;
import au.edu.monash.merc.capture.dto.PartyBean;
import au.edu.monash.merc.capture.dto.ProjectBean;
import au.edu.monash.merc.capture.ws.client.rm.AndsRMWSClient;

public class ThreadWSTest implements Runnable, TestConstant {

	private Thread wsThread;

	public static Logger logger = Logger.getLogger(ThreadWSTest.class.getName());

	private String authencateId;

	private int callTimes;

	private long callInterval = 1000;

	private static AndsRMWSClient wsclient;

	public ThreadWSTest() {
		this.wsThread = new Thread(this);
	}

	public ThreadWSTest(String authencateId, int times, long callInterval) {
		this.wsThread = new Thread(this);
		this.authencateId = authencateId;
		this.callTimes = times;
		this.callInterval = callInterval;
	}

	public void startWS() {
		if (this.wsThread == null) {
			this.wsThread = new Thread(this);
		}
		this.wsThread.start();
	}

	public void run() {
		Thread runThread = Thread.currentThread();
		if ((wsThread != null) && (runThread == wsThread)) {
			for (int i = 0; i < callTimes; i++) {
				WSTestResult reporter = rmws(this.authencateId, wsThread.getId(), (i + 1));
				reporter.setFinished(true);
				WSResultsReporter.testReporter.add(reporter);
				try {
					Thread.sleep(callInterval);
				} catch (Exception e) {
					// ignore whatever.
				}
			}
		}
	}

	private WSTestResult rmws(String authencateId, long sessionId, int no) {
		if (wsclient == null) {
			wsclient = new AndsRMWSClient();
			wsclient.setTimeout(30000);
			wsclient.serviceInit();
		}
		String nlaid = null;
		WSTestResult reporter = new WSTestResult();
		reporter.setAuthencateId(authencateId);
		reporter.setSessionId(sessionId);
		reporter.setCallNo(no);

		try {
			nlaid = wsclient.getNlaId(authencateId);
			reporter.setResult(format_pattern2 + "getNlaId(\"" + authencateId + "\")" + format_pattern2 + "success" + format_pattern2 + nlaid);
		} catch (Exception e) {
			reporter.setResult(format_pattern2 + "getNlaId(\"" + authencateId + "\")" + format_pattern2 + "failure" + format_pattern2
					+ e.getMessage());
		}

		try {
			PartyBean pb = wsclient.getPartyRegistryObject(nlaid);
			reporter.setResult(format_pattern4 + "getPartyRegistryObject(\"" + nlaid + "\")" + format_pattern3 + "success" + format_pattern2
					+ pb.getPersonTitle() + " " + pb.getPersonGivenName() + " " + pb.getPersonFamilyName());
		} catch (Exception e) {
			reporter.setResult(format_pattern4 + "getPartyRegistryObject(\"" + nlaid + "\")" + format_pattern3 + "failure" + format_pattern2
					+ e.getMessage());
		}

		List<ProjectBean> projs = new ArrayList<ProjectBean>();
		try {
			projs = wsclient.getProjects(nlaid);
			reporter.setResult(format_pattern2 + "getProjects(\"" + nlaid + "\")" + format_pattern2 + "success" + format_pattern2 + " total "
					+ projs.size() + " activity summary(ies)");
		} catch (Exception e) {
			reporter.setResult(format_pattern3 + "getProjects(\"" + nlaid + "\")" + format_pattern3 + "failure" + format_pattern2 + e.getMessage());
		}

		if (projs == null) {
			System.out.println("projects are null");
		} else {
			if (projs.size() == 0) {
				reporter.setResult(format_pattern3 + "getActivityRegistryObject(\"  \")" + format_pattern3 + "    " + format_pattern3
						+ " NOT Invoked. The project id not available");
			} else {
				for (ProjectBean pb : projs) {
					try {
						ActivityBean ab = wsclient.getActivityRegistryObject(pb.getActivityKey());
						reporter.setResult(format_pattern4 + "getActivityRegistryObject(\"" + pb.getActivityKey() + "\")" + format_pattern3
								+ "success" + format_pattern2 + ab.getActivityKey());
					} catch (Exception e) {
						reporter.setResult(format_pattern4 + "getActivityRegistryObject(\"" + pb.getActivityKey() + "\")" + format_pattern3
								+ "failure" + format_pattern2 + e.getMessage());
					}
				}
			}
		}
		return reporter;
	}

	public String getAuthencateId() {
		return authencateId;
	}

	public void setAuthencateId(String authencateId) {
		this.authencateId = authencateId;
	}

	public int getCallTimes() {
		return callTimes;
	}

	public void setCallTimes(int callTimes) {
		this.callTimes = callTimes;
	}

	public long getCallInterval() {
		return callInterval;
	}

	public void setCallInterval(long callInterval) {
		this.callInterval = callInterval;
	}

	public long getSessionId() {
		return this.wsThread.getId();
	}

	public static void main(String[] args) {
		List<ThreadWSTest> callers = new ArrayList<ThreadWSTest>();

		int totalCallTimes = 2;
		long callInterval = 4000;
		ThreadWSTest test1 = new ThreadWSTest();
		test1.setAuthencateId("calvinc");
		test1.setCallTimes(totalCallTimes);
		test1.setCallInterval(callInterval);

		callers.add(test1);

		ThreadWSTest test2 = new ThreadWSTest();
		test2.setAuthencateId("virginig");
		test2.setCallTimes(totalCallTimes);
		test2.setCallInterval(callInterval);

		callers.add(test2);

		ThreadWSTest test3 = new ThreadWSTest();

		test3.setAuthencateId("xiyu");
		test3.setCallTimes(totalCallTimes);
		test3.setCallInterval(callInterval);

		callers.add(test3);

		logger.info(format_pattern1 + " Research Master Web Service  Concurrency QA Test Report " + format_pattern1);
		logger.info(format_pattern1 + " Totol Testing Users : " + callers.size() + "  Total WS Call Times: " + totalCallTimes + " " + format_pattern1);

		// start the multiple threads to call ws.
		for (ThreadWSTest tws : callers) {
			logger.info(format_pattern1 + " Testing User Authencate Id :  " + tws.getAuthencateId() + " WS Session Id: " + tws.getSessionId() + " "
					+ format_pattern1);
			tws.startWS();
		}

		// run another thread to check the result.
		ResultChecker checker = new ResultChecker(callers.size(), totalCallTimes);
		checker.checkResult();
	}
}
