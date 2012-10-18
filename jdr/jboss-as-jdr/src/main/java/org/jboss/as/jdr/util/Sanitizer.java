package org.jboss.as.jdr.util;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: jhjaggars
 * Date: 10/18/12
 * Time: 12:11 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Sanitizer {
    InputStream sanitize(InputStream in) throws Exception;
}
