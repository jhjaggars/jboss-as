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

import org.jboss.as.jdr.resource.ResourceFactory;
import org.jboss.as.jdr.resource.Resource;
import org.jboss.as.jdr.resource.Utils;
import org.jboss.as.jdr.resource.filter.AndFilter;
import org.jboss.as.jdr.resource.filter.RegexBlacklistFilter;
import org.jboss.as.jdr.resource.filter.RegexpPathFilter;
import org.jboss.as.jdr.resource.filter.ResourceFilter;
import org.jboss.as.jdr.util.FilteredSanitizer;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class CopyDir extends JdrCommand {

    ResourceFilter filter = ResourceFilter.TRUE;
    ResourceFilter blacklistFilter = new RegexBlacklistFilter();
    LinkedList<FilteredSanitizer> sanitizers = new LinkedList<FilteredSanitizer>();

    public CopyDir(ResourceFilter filter) {
        this.filter = filter;
    }

    public CopyDir(String pattern) {
        this.filter = new RegexpPathFilter(pattern);
    }

    public CopyDir blacklist(ResourceFilter blacklistFilter) {
        this.blacklistFilter = blacklistFilter;
        return this;
    }

    public CopyDir sanitizer(FilteredSanitizer sanitizer) {
        this.sanitizers.add(sanitizer);
        return this;
    }

    @Override
    public void execute() throws Exception {
        Resource root = ResourceFactory.getResource(this.env.getJbossHome());
        List<Resource> matches = root.getChildrenRecursively(new AndFilter(this.filter, this.blacklistFilter));
        for( Resource f : matches ) {
            InputStream stream = f.openStream();
            for (FilteredSanitizer sanitizer : this.sanitizers) {
                if(sanitizer.accepts(f)){
                    stream = sanitizer.sanitize(stream);
                }
            }
            this.env.getZip().add(f, stream);
            Utils.safelyClose(stream);
        }
    }
}
