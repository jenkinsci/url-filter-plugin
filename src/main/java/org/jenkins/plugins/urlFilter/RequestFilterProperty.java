package org.jenkins.plugins.urlFilter;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Model class for Request Filter properties. Extends {@link AbstractDescribableImpl}
 */
public class RequestFilterProperty extends AbstractDescribableImpl<RequestFilterProperty> {

    private String filterRegex;
    private String excludedUsers;

    @DataBoundConstructor
    public RequestFilterProperty(String filterRegex, String excludedUsers) {
        this.filterRegex = filterRegex;
        this.excludedUsers = excludedUsers;
    }

    public String getFilterRegex() {
        return filterRegex;
    }

    @DataBoundSetter
    public void setFilterRegex(String filterRegex) {
        this.filterRegex = filterRegex;
    }

    public String getExcludedUsers() {
        return excludedUsers;
    }

    @DataBoundSetter
    public void setExcludedUsers(String excludedUsers) {
        this.excludedUsers = excludedUsers;
    }

    @Extension
    public static class RequestFilterDescriptor extends Descriptor<RequestFilterProperty>{
        @Override
        public String getDisplayName() {
            return "Filter";
        }
    }
}
