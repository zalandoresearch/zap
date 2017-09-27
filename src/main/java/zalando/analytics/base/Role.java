package zalando.analytics.base;

/**
 *
 * Class for a Role of a Frame. Each role is represented by a Token (its syntactic head), and its role label.
 *
 * Created by Alan Akbik on 8/29/17.
 */
public class Role {

    // Label of this role.
    String roleLabel;

    // Syntactic head of this role.
    Token roleHead;

    /**
     * Constructor for role.
     * @param roleLabel Label of this role
     * @param roleHead Syntactic head of this role
     */
    public Role(String roleLabel, Token roleHead) {
        this.roleLabel = roleLabel;
        this.roleHead = roleHead;
    }

    // ------------------------------------------------------------------------
    // Getters and setters
    // ------------------------------------------------------------------------
    public String getRoleLabel() {
        return roleLabel;
    }

    public Token getRoleHead() {
        return roleHead;
    }


}
