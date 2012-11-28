package org.jboss.as.jdr.resource.filter;

import org.jboss.as.jdr.resource.Resource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: csams
 * Date: 11/4/12
 * Time: 6:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegexpPathFilter implements ResourceFilter {

    private final Pattern pattern;

    public RegexpPathFilter(String pattern){
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public boolean accepts(Resource resource) {
        return pattern.matcher(resource.getPath()).matches();
    }
}
