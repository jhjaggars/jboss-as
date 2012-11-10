package org.jboss.as.jdr.resource.filter;

import org.jboss.as.jdr.resource.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: csams
 * Date: 11/4/12
 * Time: 2:40 PM
 */
public class RegexBlacklistFilter implements ResourceFilter{

    private final List<Pattern> patterns;

    public RegexBlacklistFilter(){
        this.patterns = Arrays.asList(Pattern.compile(".*-users.properties"));
    }

    public RegexBlacklistFilter(String... patterns){
        this.patterns = new ArrayList<Pattern>(patterns.length);
        this.add(patterns);
    }

    public void add(String ... patterns) {
        for (String p : patterns) {
            this.patterns.add(Pattern.compile(p));
        }
    }

    @Override
    public boolean accepts(Resource resource) {
        for(Pattern p: this.patterns){
            if(p.matcher(resource.getName()).matches()){
                return false;
            }
        }
        return true;
    }

}
