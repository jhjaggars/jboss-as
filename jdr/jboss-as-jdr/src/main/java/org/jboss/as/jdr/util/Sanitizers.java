package org.jboss.as.jdr.util;

import org.jboss.as.jdr.resource.filter.PathSuffixFilter;

public class Sanitizers {

    public static Sanitizer pattern(String pattern, String replacement) throws Exception {
        return new PatternSanitizer(pattern, replacement, new PathSuffixFilter(".properties"));
    }

    public static Sanitizer xml(String xpath) throws Exception {
        return new XMLSanitizer(xpath, new PathSuffixFilter(".xml"));
    }
}
