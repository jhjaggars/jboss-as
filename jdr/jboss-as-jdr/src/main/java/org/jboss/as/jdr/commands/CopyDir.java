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
package org.jboss.as.jdr.commands;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DelegateFileFilter;
import org.jboss.as.jdr.util.BlackListFilter;
import org.jboss.as.jdr.util.Sanitizer;
import org.jboss.as.jdr.util.Find;
import org.jboss.as.jdr.util.WildcardPathFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;

public class CopyDir extends JdrCommand {

    FileFilter filter;
    FileFilter blacklistFilter = new BlackListFilter();
    LinkedList<Sanitizer> sanitizers = new LinkedList<Sanitizer>();

    public CopyDir(FileFilter filter) {
        this.filter = filter;
    }

    public CopyDir(String pattern) {
        this.filter = new WildcardPathFilter(pattern);
    }

    public CopyDir blacklist(FileFilter blacklistFilter) {
        this.blacklistFilter = blacklistFilter;
        return this;
    }

    public CopyDir sanitizer(Sanitizer sanitizer) {
        this.sanitizers.add(sanitizer);
        return this;
    }

    @Override
    public void execute() throws Exception {
        Collection<File> matches = Find.walk(
            new File(this.env.getJbossHome()),
            new AndFileFilter(
                new DelegateFileFilter(this.filter),
                new DelegateFileFilter(this.blacklistFilter)
            )
        );
        for( File f : matches ) {
            System.out.println(f.getPath());
            InputStream stream = new FileInputStream(f);
            for (Sanitizer s : this.sanitizers) {
                stream = s.sanitize(stream);
            }
            this.env.getZip().add(f, stream);
        }
    }
}
