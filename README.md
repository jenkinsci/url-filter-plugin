URL Filter Plugin
====================================

This plugin enables filtering servlet/http/url requests in Jenkins and blocking the ones which are matched with the given Regex Pattern.

# Description
Most of the actions in Jenkins UI/CLI are processed over Http protocol. 
Because of security reasons, some of these requests may need to be blocked.

In summary, with Administrative Filter Plugin,
-   You can define filter patterns (Regex) for Http Request URIs
-   You can define users for excluding from filter patterns.

# Configuration
Plugin configuration is stored under Jenkins - Configure System page.
Under the ```URL Filter``` section in this page, you can add filters as much as you want.

For adding a new filter click on ```Add Filter``` button.

Filter definition requires a valid regex pattern and excluded user list (optional).

After adding filters click ```Save```

# Logging and Debugging
Every matched and blocked request will be logged in Jenkins log.

Additionally, if you need to see more details about plugin flow, you can set logger in ```System Log``` section in Jenkins.

Add a Logger with this package name ```org.jenkins.plugins.urlFilter``` and set log level to ```ALL```.