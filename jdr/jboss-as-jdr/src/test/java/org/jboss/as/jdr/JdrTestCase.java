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

import org.jboss.as.jdr.util.BlackListFilter;
import org.jboss.as.jdr.util.PatternSanitizer;
import org.jboss.as.jdr.util.XMLSanitizer;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
        PatternSanitizer s = new PatternSanitizer("password=*");
        InputStream res = s.sanitize(is);
        byte [] buf = new byte [res.available()];
        res.read(buf);
        assertEquals("password=*", new String(buf));
    }
}
