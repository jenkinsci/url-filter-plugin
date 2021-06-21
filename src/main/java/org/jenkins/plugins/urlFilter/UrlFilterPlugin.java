package org.jenkins.plugins.urlFilter;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Plugin Configuration class extends {@link GlobalConfiguration}
 */
@Extension
public class UrlFilterPlugin extends GlobalConfiguration {

    private List<RequestFilterProperty> requestFilterProperties = new ArrayList<>();

    public UrlFilterPlugin() {
        load();
    }

    public List<RequestFilterProperty> getRequestFilterProperties() {
        return requestFilterProperties;
    }

    @DataBoundSetter
    public void setRequestFilterProperties(List<RequestFilterProperty> requestFilterProperties) {
        this.requestFilterProperties = requestFilterProperties;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        save();
        return true;
    }

    /**
     * Returns Singleton configuration of this plugin
     * @return RequestFilterPlugin
     */
    public static UrlFilterPlugin get(){
        return GlobalConfiguration.all().get(UrlFilterPlugin.class);
    }

}
