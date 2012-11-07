/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.jdr;

import org.jboss.as.jdr.resource.FileResource;
import org.jboss.as.jdr.resource.Resource;
import org.jboss.as.jdr.resource.filter.ResourceFilter;
import org.jboss.as.jdr.resource.filter.WildcardPathFilter;
import org.jboss.as.jdr.util.BlackListFilter;
import org.jboss.as.jdr.util.PatternSanitizer;
import org.jboss.as.jdr.util.XMLSanitizer;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class JdrTestCase {

    @Test
    public void testBlackListFilter() {
        FileFilter blf = new BlackListFilter();
        assertFalse(blf.accept(new File("/foo/bar/baz/mgmt-users.properties")));
        assertFalse(blf.accept(new File("/foo/bar/baz/application-users.properties")));
    }

    @Test
    public void testXMLSanitizer() throws Exception {
        String xml = "<test><password>foobar</password></test>";
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        XMLSanitizer s = new XMLSanitizer("//password");
        InputStream res = s.sanitize(is);
        byte [] buf = new byte [res.available()];
        res.read(buf);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><test><password/></test>", new String(buf));
    }

    @Test
    public void testPatternSanitizer() throws Exception {
        String propf = "password=123456";
        InputStream is = new ByteArrayInputStream(propf.getBytes());
        PatternSanitizer s = new PatternSanitizer("password=.*", "password=*");
        InputStream res = s.sanitize(is);
        byte [] buf = new byte [res.available()];
        res.read(buf);
        assertEquals("password=*", new String(buf));
    }

    @Test
    public void testWildcardFilterAcceptAnything() throws Exception {
        ResourceFilter filter = new WildcardPathFilter("*");
        Resource good = new FileResource(new File("/this/is/a/test.txt"));
        assertTrue(filter.accepts(good));
    }

    @Test
    public void testWildcardFilterPrefixGlob() throws Exception {
        ResourceFilter filter = new WildcardPathFilter("*.txt");
        Resource good = new FileResource(new File("/this/is/a/test.txt"));
        Resource bad = new FileResource(new File("/this/is/a/test.xml"));
        assertTrue(filter.accepts(good));
        assertFalse(filter.accepts(bad));
    }

    @Test
    public void testWildcardFilterSuffixGlob() throws Exception {
        ResourceFilter filter = new WildcardPathFilter("/this/is*");
        Resource good = new FileResource(new File("/this/is/a/test.txt"));
        Resource bad = new FileResource(new File("/that/is/a/test.txt"));
        assertTrue(filter.accepts(good));
        assertFalse(filter.accepts(bad));
    }

    @Test
    public void testWildcardFilterMiddleGlob() throws Exception {
        ResourceFilter filter = new WildcardPathFilter("/this*test.txt");
        Resource good = new FileResource(new File("/this/is/a/test.txt"));
        Resource bad1 = new FileResource(new File("/that/is/a/test.txt"));
        Resource bad2 = new FileResource(new File("/this/is/a/test.xml"));
        assertTrue(filter.accepts(good));
        assertFalse(filter.accepts(bad1));
        assertFalse(filter.accepts(bad2));
    }

    @Test
    public void testWildcardFilterPrefixSingle() throws Exception {
        ResourceFilter filter = new WildcardPathFilter("?this/is/a/test.txt");
        Resource good = new FileResource(new File("/this/is/a/test.txt"));
        Resource bad = new FileResource(new File("/that/is/a/test.txt"));
        assertTrue(filter.accepts(good));
        assertFalse(filter.accepts(bad));

        ResourceFilter filter2 = new WildcardPathFilter("?????/is/a/test.txt");
        Resource good2 = new FileResource(new File("/this/is/a/test.txt"));
        Resource bad2 = new FileResource(new File("/this/was/a/test.txt"));
        assertTrue(filter2.accepts(good2));
        assertFalse(filter2.accepts(bad2));
    }

    @Test
    public void testWildcardFilterPostfixSingle() throws Exception {
        ResourceFilter filter1 = new WildcardPathFilter("/this/is/a/test.tx?");
        Resource good1 = new FileResource(new File("/this/is/a/test.txt"));
        Resource bad1 = new FileResource(new File("/that/is/a/test.dat"));
        assertTrue(filter1.accepts(good1));
        assertFalse(filter1.accepts(bad1));

        ResourceFilter filter2 = new WildcardPathFilter("/this/is/a/test.???");
        Resource good2 = new FileResource(new File("/this/is/a/test.txt"));
        Resource bad2 = new FileResource(new File("/that/is/a/blah.txt"));
        assertTrue(filter2.accepts(good2));
        assertFalse(filter2.accepts(bad2));

    }

    @Test
    public void testWildcardFilterMiddleSingle() throws Exception {
        ResourceFilter filter1 = new WildcardPathFilter("/this/???/a/test.txt");
        Resource good1 = new FileResource(new File("/this/iss/a/test.txt"));
        Resource bad1 = new FileResource(new File("/that/was/no/test.dat"));
        assertTrue(filter1.accepts(good1));
        assertFalse(filter1.accepts(bad1));

        ResourceFilter filter2 = new WildcardPathFilter("/????/is/a/????.txt");
        Resource good2 = new FileResource(new File("/this/is/a/test.txt"));
        Resource bad2 = new FileResource(new File("/that/is/no/test.txt"));
        assertTrue(filter2.accepts(good2));
        assertFalse(filter2.accepts(bad2));

    }
}
