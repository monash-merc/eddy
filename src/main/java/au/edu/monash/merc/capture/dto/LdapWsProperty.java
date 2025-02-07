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

package au.edu.monash.merc.capture.dto;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: simonyu
 * Date: 10/02/2014
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class LdapWsProperty implements Serializable {

    private boolean ldapWsEnabled;

    private String ldapWsServer;

    private int ldapWsPort;

    private boolean certErrorIgnore;

    public boolean isLdapWsEnabled() {
        return ldapWsEnabled;
    }

    public void setLdapWsEnabled(boolean ldapWsEnabled) {
        this.ldapWsEnabled = ldapWsEnabled;
    }

    public String getLdapWsServer() {
        return ldapWsServer;
    }

    public void setLdapWsServer(String ldapWsServer) {
        this.ldapWsServer = ldapWsServer;
    }

    public int getLdapWsPort() {
        return ldapWsPort;
    }

    public void setLdapWsPort(int ldapWsPort) {
        this.ldapWsPort = ldapWsPort;
    }

    public boolean isCertErrorIgnore() {
        return certErrorIgnore;
    }

    public void setCertErrorIgnore(boolean certErrorIgnore) {
        this.certErrorIgnore = certErrorIgnore;
    }
}
