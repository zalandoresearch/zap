package zalando.analytics.base;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Class to hold a semantic frame. Each frame has a label and a set of Roles.
 * <p>
 * Created by Alan Akbik on 8/29/17.
 */
public class Frame {

    // The frame label, for instance "buy.01".
    private String label;

    // List of all roles bound to this frame.
    private List<Role> roles = Lists.newArrayList();

    /**
     * Constructor for frame object, requires the frame label. Access is protected, i.e. create a new Frame using
     * the addNewFrame() method in Token.
     *
     * @param label Label of this frame.
     */
    Frame(String label) {
        this.label = label;
    }

    // ------------------------------------------------------------------------
    // Methods that pertain to the frame Roles
    // ------------------------------------------------------------------------

    /**
     * Add a new Role to this frame.
     * @param role new Role
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Check if this Frame binds Token in a role.
     *
     * @param token Token to check
     * @return True if this Frame binds Token in a role
     */
    public boolean hasTokenRole(Token token) {

        for (Role role : roles) {
            if (role.roleHead.equals(token)) return true;
        }
        return false;
    }

    /**
     * Get role label of a given Token
     *
     * @param token Token for which to retrieve role label
     * @return role label of given Token
     */
    public String getTokenRole(Token token) {

        for (Role role : roles) {
            if (role.roleHead.equals(token)) return role.roleLabel;
        }
        return null;
    }

    // ------------------------------------------------------------------------
    // Getters and setters
    // ------------------------------------------------------------------------
    public String getLabel() {
        return label;
    }

    public List<Role> getRoles() {
        return this.roles;
    }


}
