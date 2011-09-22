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
package au.edu.monash.merc.capture.dto;

import java.io.Serializable;

public class NavigationBar implements Serializable {

	private String startNavName;

	private String startNavLink;

	private String secondNavName;

	private String secondNavLink;

	private String thirdNavName;

	private String thirdNavLink;

	public NavigationBar() {

	}

	public NavigationBar(String startNavName, String startNavLink, String secondNavName, String secondNavLink, String thirdNavName,
			String thirdNavLink) {

		this.startNavName = startNavName;
		this.startNavLink = startNavLink;

		this.secondNavName = secondNavName;
		this.secondNavLink = secondNavLink;

		this.thirdNavName = thirdNavName;
		this.thirdNavLink = thirdNavLink;
	}

	public String getStartNavName() {
		return startNavName;
	}

	public void setStartNavName(String startNavName) {
		this.startNavName = startNavName;
	}

	public String getStartNavLink() {
		return startNavLink;
	}

	public void setStartNavLink(String startNavLink) {
		this.startNavLink = startNavLink;
	}

	public String getSecondNavName() {
		return secondNavName;
	}

	public void setSecondNavName(String secondNavName) {
		this.secondNavName = secondNavName;
	}

	public String getSecondNavLink() {
		return secondNavLink;
	}

	public void setSecondNavLink(String secondNavLink) {
		this.secondNavLink = secondNavLink;
	}

	public String getThirdNavName() {
		return thirdNavName;
	}

	public void setThirdNavName(String thirdNavName) {
		this.thirdNavName = thirdNavName;
	}

	public String getThirdNavLink() {
		return thirdNavLink;
	}

	public void setThirdNavLink(String thirdNavLink) {
		this.thirdNavLink = thirdNavLink;
	}

}
