package org.jenkins.plugins.urlFilter;

import hudson.Extension;
import hudson.init.Initializer;
import hudson.util.PluginServletFilter;
import jenkins.model.Jenkins;
import lombok.NoArgsConstructor;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;


/**
 * This class implements javax.Servlet Filter Interface for cathing all requests in Jenkins.
 * These requests are controlled against filter definitions.
 */
@Extension
@NoArgsConstructor
public class RequestFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(RequestFilter.class.getName());

    @Initializer
    public static void init() throws ServletException {
        LOGGER.fine("Adding Request Filter to PluginServlet Filter");
        PluginServletFilter.addFilter(new RequestFilter());
    }

    @Override
    public void init(FilterConfig filterConfig) {
        LOGGER.fine("Initializing RequestFilter");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String requestURI = httpServletRequest.getRequestURI();

        if (checkIfFilterMatches(requestURI)) {
            LOGGER.fine("Filter matches with RequestURI:" + requestURI + " returning HTTP 403");
            httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    @Override
    public void destroy() {}


    /**
     * Gets logged in user from Jenkins
     * @return Name of the user
     */
    private String getLoggedInUser() {
        LOGGER.fine("Getting logged in user");
        return Jenkins.getAuthentication2().getName();
    }

    /**
     * Check if Request Uri matches with any RequestFilters defined in plugin configuration
     * @param requestUri HttpServletRequest Request Uri
     * @return True If matches, False if not
     */
    private boolean checkIfFilterMatches(String requestUri) {
        LOGGER.fine("Start checking request uri:"+requestUri+" if matches with defined RequestFilters");
        UrlFilterPlugin urlFilterPlugin = UrlFilterPlugin.get();
        List<RequestFilterProperty> requestFilterProperties = urlFilterPlugin.getRequestFilterProperties();
        String loggedInUser = this.getLoggedInUser();
        LOGGER.fine("Retrieve logged in user:" + loggedInUser);
        for (RequestFilterProperty requestFilterProperty : requestFilterProperties) {
            LOGGER.fine("Processing for RegexFilterProperty filterRegex:" + requestFilterProperty + " excludedUsers:" + requestFilterProperty.getExcludedUsers());
            String filterRegex = requestFilterProperty.getFilterRegex();
            if (requestUri.matches(filterRegex)) {
                LOGGER.fine("Uri matches with filter");
                //Check if Logged In user in exclude list
                if (!isUserExcluded(loggedInUser, requestFilterProperty)) {
                    LOGGER.fine("Logged in user is not excluded. Blocking request");
                    LOGGER.info("Request: " + requestUri + " is blocked for user: " + loggedInUser);
                    return true;
                }
            }
        }
        LOGGER.fine("No filter matched with request uri. Not blocking request");
        return false;
    }

    /**
     * Checks if given user matches with any of the users in given {@link RequestFilterProperty}
     * @param loggedInUser Name of the logged in user
     * @param requestFilterProperty {@link RequestFilterProperty}
     * @return True if matches, false if not
     */
    private boolean isUserExcluded(String loggedInUser, RequestFilterProperty requestFilterProperty) {
        LOGGER.fine("Checking if user is excluded user list.");
        LOGGER.fine("loggedInUser:" + loggedInUser);
        LOGGER.fine("excludedUsers:" + requestFilterProperty.getExcludedUsers());
        String excludedUsers = requestFilterProperty.getExcludedUsers();
        String[] excludesUsersList = excludedUsers.split(" ");
        for (String excludedUser : excludesUsersList) {
            LOGGER.fine("Checking if logged in user matches with user:" + excludedUser);
            if (loggedInUser.equals(excludedUser) || loggedInUser.matches(excludedUser)) {
                LOGGER.fine("Users are matched. Returning true");
                return true;
            }
        }
        LOGGER.fine("No users are matched. Returning false");
        return false;
    }
}
