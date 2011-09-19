package au.edu.monash.merc.capture.dto;

/**
 * Created by IntelliJ IDEA.
 * User: simonyu
 * Date: 19/09/11
 * Time: 10:41 AM
 * To change this template use File | Settings | File Templates.
 */
public enum ManagablePermType {

    DELETE("delete"), UPDATE("update"), NEW("new"), IGNORE("ignore");

    private String code;

    ManagablePermType(String code) {
        this.code = code;
    }

    public String type() {
        return code;
    }

    public String toString() {
        switch (this) {
            case DELETE:
                return "delete";
            case UPDATE:
                return "update";
            case NEW:
                return "new";
            case IGNORE:
                return "ignore";
            default:
                return "ignore";
        }
    }
}
