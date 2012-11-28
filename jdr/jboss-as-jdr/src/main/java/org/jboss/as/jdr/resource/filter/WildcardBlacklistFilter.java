package org.jboss.as.jdr.resource.filter;

import org.jboss.as.jdr.resource.Resource;
import org.jboss.as.jdr.util.WildcardPattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jjaggars
 * Date: 11/9/12
 * Time: 6:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class WildcardBlacklistFilter implements ResourceFilter {

    private final List<String> patterns;

    public WildcardBlacklistFilter(){
        this.patterns = Arrays.asList("*-users.properties");
    }

    public WildcardBlacklistFilter(String... patterns){
        this.patterns = new ArrayList<String>(patterns.length);
        this.add(patterns);
    }

    public void add(String ... patterns) {
        Collections.addAll(this.patterns, patterns);
    }

    @Override
    public boolean accepts(Resource resource) {
        for(String p: this.patterns){
            if(WildcardPattern.matches(p, resource.getName())) {
                return false;
            }
        }
        return true;
    }
}
