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

import java.util.List;

import org.apache.log4j.Logger;

public class ResultChecker implements Runnable, TestConstant {

	private Thread checker;

	private int totalUsers;

	private int totalRunTimes;

	public static Logger logger = Logger.getLogger(ResultChecker.class.getName());

	public ResultChecker() {
		if (this.checker == null) {
			checker = new Thread(this);
		}
	}

	public void checkResult() {
		if (this.checker == null) {
			checker = new Thread(this);
		}
		checker.start();
	}

	public ResultChecker(int totalUsers, int totalRunTimes) {
		if (this.checker == null) {
			checker = new Thread(this);
		}
		this.totalUsers = totalUsers;
		this.totalRunTimes = totalRunTimes;
	}

	public void run() {
		Thread ckrunt = Thread.currentThread();
		boolean unfinished = true;
		if ((checker != null) && (this.checker == ckrunt)) {
			while (unfinished) {
				List<WSTestResult> results = WSResultsReporter.testReporter;
				if (results.size() != totalUsers * totalRunTimes) {
					unfinished = true;
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						// ignore whatever
					}
				} else {
					for (WSTestResult rs : results) {
						if (rs.isFinished()) {
							unfinished = false;
						}
					}
				}
			}
			logger.info(format_pattern1 + " Total Testing Users :  " + totalUsers + ", called WS : " + totalRunTimes + " times for each user "
					+ format_pattern1);
			for (WSTestResult reporters : WSResultsReporter.testReporter) {
				List<String> results = reporters.getResults();
				for (String rs : results) {
					logger.info(reporters.getAuthencateId() + " -- WS Session Id: " + reporters.getSessionId() + " -- WS Call No. "
							+ reporters.getCallNo() + " -- " + rs);
				}
			}
		}
	}

	public int getTotalUsers() {
		return totalUsers;
	}

	public void setTotalUsers(int totalUsers) {
		this.totalUsers = totalUsers;
	}

	public int getTotalRunTimes() {
		return totalRunTimes;
	}

	public void setTotalRunTimes(int totalRunTimes) {
		this.totalRunTimes = totalRunTimes;
	}

}
