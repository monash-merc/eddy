package au.edu.monash.merc.capture.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: simonyu
 * Date: 19/09/11
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class ManagablePerm<T> implements Serializable {

    private T perm;

    private ManagablePermType managablePermType;

    public ManagablePerm() {

    }

    public T getPerm() {
        return perm;
    }

    public void setPerm(T perm) {
        this.perm = perm;
    }

    public ManagablePermType getManagablePermType() {
        return managablePermType;
    }

    public void setManagablePermType(ManagablePermType managablePermType) {
        this.managablePermType = managablePermType;
    }
}
