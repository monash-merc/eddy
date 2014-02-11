package au.edu.monash.merc.capture.ws.client.ldapws;

/**
 * Created with IntelliJ IDEA.
 * User: simonyu
 * Date: 7/02/2014
 * Time: 11:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class WSConfig {

    /**
     * ignore the certificate error flag
     */
    private boolean ignoreCertError;

    /**
     * The LDAP Authentication Web Service Host Name
     */
    private String ldapAuthenServiceHost;

    /**
     * The LDAP Authentication Web Service port number.
     */
    private int ldapAuthenServicePort;

    /**
     * The LDAP Authentication Web Service  path name (web application context name).
     */
    private String ldapAuthenServicePath;

    /**
     * The LDAP Authentication Web Service  authentication method
     */
    private String ldapAuthenServiceMethod;


    public boolean isIgnoreCertError() {
        return ignoreCertError;
    }

    public void setIgnoreCertError(boolean ignoreCertError) {
        this.ignoreCertError = ignoreCertError;
    }

    public String getLdapAuthenServiceHost() {
        return ldapAuthenServiceHost;
    }

    public void setLdapAuthenServiceHost(String ldapAuthenServiceHost) {
        this.ldapAuthenServiceHost = ldapAuthenServiceHost;
    }

    public int getLdapAuthenServicePort() {
        return ldapAuthenServicePort;
    }

    public void setLdapAuthenServicePort(int ldapAuthenServicePort) {
        this.ldapAuthenServicePort = ldapAuthenServicePort;
    }

    public String getLdapAuthenServicePath() {
        return ldapAuthenServicePath;
    }

    public void setLdapAuthenServicePath(String ldapAuthenServicePath) {
        this.ldapAuthenServicePath = ldapAuthenServicePath;
    }

    public String getLdapAuthenServiceMethod() {
        return ldapAuthenServiceMethod;
    }

    public void setLdapAuthenServiceMethod(String ldapAuthenServiceMethod) {
        this.ldapAuthenServiceMethod = ldapAuthenServiceMethod;
    }
}
