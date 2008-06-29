/**
 * 
 */
package hudson.security;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.ldap.InitialDirContextFactory;
import org.acegisecurity.ldap.LdapDataAccessException;
import org.acegisecurity.providers.ldap.LdapAuthoritiesPopulator;
import org.acegisecurity.providers.ldap.populator.DefaultLdapAuthoritiesPopulator;
import org.acegisecurity.userdetails.ldap.LdapUserDetails;
import org.springframework.util.Assert;

/**
 * Implementation of {@link LdapAuthoritiesPopulator} that defers creation of a
 * {@link DefaultLdapAuthoritiesPopulator} until one is needed. This is done to
 * ensure that the groupSearchBase property can be set.
 * 
 * @author justinedelson
 */
public class DeferredCreationLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    /**
     * A default role which will be assigned to all authenticated users if set.
     */
    private String defaultRole = null;

    /**
     * An initial context factory is only required if searching for groups is
     * required.
     */
    private InitialDirContextFactory initialDirContextFactory = null;

    /**
     * Controls used to determine whether group searches should be performed
     * over the full sub-tree from the base DN.
     */
    private boolean searchSubtree = false;

    /**
     * The ID of the attribute which contains the role name for a group
     */
    private String groupRoleAttribute = "cn";

    /**
     * The base DN from which the search for group membership should be
     * performed
     */
    private String groupSearchBase = null;

    /**
     * The pattern to be used for the user search. {0} is the user's DN
     */
    private String groupSearchFilter = "(member={0})";

    private String rolePrefix = "ROLE_";

    private boolean convertToUpperCase = true;

    /**
     * Constructor.
     * 
     * @param initialDirContextFactory
     *            supplies the contexts used to search for user roles.
     * @param groupSearchBase
     *            if this is an empty string the search will be performed from
     *            the root DN of the context factory.
     */
    public DeferredCreationLdapAuthoritiesPopulator(
            InitialDirContextFactory initialDirContextFactory, String groupSearchBase) {
        this.setInitialDirContextFactory(initialDirContextFactory);
        this.setGroupSearchBase(groupSearchBase);
    }

    public GrantedAuthority[] getGrantedAuthorities(LdapUserDetails userDetails)
            throws LdapDataAccessException {
        return create().getGrantedAuthorities(userDetails);
    }

    public void setConvertToUpperCase(boolean convertToUpperCase) {
        this.convertToUpperCase = convertToUpperCase;
    }

    public void setDefaultRole(String defaultRole) {
        this.defaultRole = defaultRole;
    }

    public void setGroupRoleAttribute(String groupRoleAttribute) {
        this.groupRoleAttribute = groupRoleAttribute;
    }

    public void setGroupSearchBase(String groupSearchBase) {
        this.groupSearchBase = groupSearchBase;
    }

    public void setGroupSearchFilter(String groupSearchFilter) {
        this.groupSearchFilter = groupSearchFilter;
    }

    public void setInitialDirContextFactory(InitialDirContextFactory initialDirContextFactory) {
        this.initialDirContextFactory = initialDirContextFactory;
    }

    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    public void setSearchSubtree(boolean searchSubtree) {
        this.searchSubtree = searchSubtree;
    }

    /**
     * Create a new DefaultLdapAuthoritiesPopulator object.
     * 
     * @return a DefaultLdapAuthoritiesPopulator.
     */
    private DefaultLdapAuthoritiesPopulator create() {
        Assert
                .hasText(groupSearchBase,
                        "groupSearchBase must be non-null by the time DefaultLdapAuthoritiesPopulator is instantiated.");
        DefaultLdapAuthoritiesPopulator populator = new DefaultLdapAuthoritiesPopulator(
                initialDirContextFactory, groupSearchBase);
        populator.setConvertToUpperCase(convertToUpperCase);
        if (defaultRole != null) {
            populator.setDefaultRole(defaultRole);
        }
        populator.setGroupRoleAttribute(groupRoleAttribute);
        populator.setGroupSearchFilter(groupSearchFilter);
        populator.setRolePrefix(rolePrefix);
        populator.setSearchSubtree(searchSubtree);
        return populator;
    }

}