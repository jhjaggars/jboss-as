package org.jboss.as.jdr;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import org.junit.Test;

import static org.junit.Assert.*;

public class JdrTestCase {

    @Test
    public void testBlackListFilter() {
        FileFilter blf = new BlackListFilter();
        assertFalse(blf.accept(new File("/foo/bar/baz/mgmt-users.properties"))); 
        assertFalse(blf.accept(new File("/foo/bar/baz/application-users.properties"))); 
    }

    @Test
    public void testSanitizer() throws Exception {
        String xml = "<test><password>foobar</password></test>";
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        XMLSanitizer s = new XMLSanitizer("//password");
        InputStream res = s.sanitize(is);
        byte [] buf = new byte [res.available()];
        res.read(buf);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><test><password/></test>", new String(buf));
    }
}
