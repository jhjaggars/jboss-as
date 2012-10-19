package org.jboss.as.jdr.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: jhjaggars
 * Date: 10/18/12
 * Time: 12:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class PatternSanitizer implements Sanitizer {
    String pattern;
    String replacement;

    public PatternSanitizer (String pattern) throws Exception {
        this.pattern = pattern;
    }

    public InputStream sanitize(InputStream in) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for(String line : IOUtils.readLines(in)) {
           if(FilenameUtils.wildcardMatch(line, pattern)) {
               output.write(pattern.getBytes());
           }
           else {
               output.write(line.getBytes());
           }
        }
        return new ByteArrayInputStream(output.toByteArray());
    }
}
