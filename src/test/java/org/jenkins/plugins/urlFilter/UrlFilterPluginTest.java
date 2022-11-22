package org.jenkins.plugins.urlFilter;


import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.security.AbstractPasswordBasedSecurityRealm;
import hudson.security.GroupDetails;
import jenkins.model.Jenkins;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.springframework.dao.DataAccessException;

public class UrlFilterPluginTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    private JenkinsRule.WebClient wc = jenkins.createWebClient();
    private String filterRegex = ".*whoAmI.*";
    private String userAlice = "alice";
    private String userBob = "bob";
    private String userMichaelRegex = "mic.*";
    private String excludedUsers = userAlice + " " + userBob + " " + userMichaelRegex;

    @Before
    public void setup() throws Exception {
        this.configure(jenkins, wc, filterRegex, excludedUsers);
    }

    @Test
    public void testRequestFilterWithDifferentUrl() throws Exception {
        wc.goTo("configure");
    }

    @Test
    public void testRequestFilterWithoutUser() throws Exception {
        wc.assertFails("whoAmI", 403);
    }

    @Test
    public void testRequestFilterWithUser() throws Exception {
        jenkins.jenkins.setSecurityRealm(new SecurityRealmImpl());
        jenkins.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().
                grant(Jenkins.ADMINISTER).everywhere().to("alice"));
        wc.login("alice");
        wc.goTo("whoAmI");
    }

    @Test
    public void testRequestFilterWithNoMatchingUser() throws Exception {
        jenkins.jenkins.setSecurityRealm(new SecurityRealmImpl());
        jenkins.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().
                grant(Jenkins.ADMINISTER).everywhere().to("janice"));
        wc.login("janice");
        wc.assertFails("whoAmI", 403);
    }

    @Test
    public void testRequestFilterWithUsers() throws Exception {
        jenkins.jenkins.setSecurityRealm(new SecurityRealmImpl());
        jenkins.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy()
                .grant(Jenkins.ADMINISTER).everywhere().to("alice")
                .grant(Jenkins.READ).everywhere().to("bob")
        );
        wc.login("alice");
        wc.goTo("whoAmI");
        wc.login("bob");
        wc.goTo("whoAmI");
    }

    @Test
    public void testRequestFilterWithRegex() throws Exception {
        jenkins.jenkins.setSecurityRealm(new SecurityRealmImpl());
        jenkins.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy()
                .grant(Jenkins.READ).everywhere().to("michael")
                .grant(Jenkins.READ).everywhere().to("alice")
                .grant(Jenkins.READ).everywhere().to("bob")
        );
        wc.login("alice");
        wc.goTo("whoAmI");
        wc.login("bob");
        wc.goTo("whoAmI");
        wc.login("michael");
        wc.goTo("whoAmI");
    }

    @Test
    public void testRequestFilterWithNotMatchingRegex() throws Exception {
        jenkins.jenkins.setSecurityRealm(new SecurityRealmImpl());
        jenkins.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy()
                .grant(Jenkins.READ).everywhere().to("jack")
        );
        wc.login("jack");
        wc.assertFails("whoAmI", 403);
    }

    public void configure(JenkinsRule j, JenkinsRule.WebClient wc, String filterRegex, String excludedUsers) throws Exception {
        HtmlPage configure = wc.goTo("configure");
        HtmlForm form = configure.getFormByName("config");
        j.getButtonByCaption(form, "Add Filter").click();
        wc.waitForBackgroundJavaScript(2000);
        form.getInputByName("_.filterRegex").setValueAttribute(filterRegex);
        form.getInputByName("_.excludedUsers").setValueAttribute(excludedUsers);
        j.submit(form);
    }

    private static class SecurityRealmImpl extends AbstractPasswordBasedSecurityRealm {

        @Override
        protected UserDetails authenticate(String username, String password) throws AuthenticationException {
            return createUserDetails(username);
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
            return createUserDetails(username);
        }

        @Override
        public GroupDetails loadGroupByGroupname(String groupname) throws UsernameNotFoundException, DataAccessException {
            return null;
        }

        private UserDetails createUserDetails(String username) {
            return new UserDetails() {

                @Override
                public String getUsername() {
                    return username;
                }

                @Override
                public String toString() {
                    return "[toString()=S3cr3t]";
                }

                @Override
                public GrantedAuthority[] getAuthorities() {
                    return new GrantedAuthority[0];
                }

                @Override
                public String getPassword() {
                    return null;
                }

                @Override
                public boolean isAccountNonExpired() {
                    return true;
                }

                @Override
                public boolean isAccountNonLocked() {
                    return true;
                }

                @Override
                public boolean isCredentialsNonExpired() {
                    return true;
                }

                @Override
                public boolean isEnabled() {
                    return true;
                }
            };
        }
    }

}