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
package org.jboss.as.jdr.util;

import org.jboss.as.jdr.resource.Resource;

//TODO: csams - Change this to WildcardPathFilter to handle globbing instead of full regex
import org.jboss.as.jdr.resource.filter.RegexpPathFilter;
import org.jboss.as.jdr.resource.filter.ResourceFilter;

import java.util.ArrayList;
import java.util.List;

public class Find {

    public static List<Resource> walk(Resource root) throws Exception {
        return walk(root, ResourceFilter.TRUE);
    }

    public static List<Resource> walk(Resource root, String pattern) throws Exception {
        return walk(root, new RegexpPathFilter(pattern));
    }

    public static List<Resource> walk(Resource root, ResourceFilter filter) throws Exception {
        ArrayList<Resource> results = new ArrayList<Resource>();
        for( Resource f : root.getChildren() ) {
            if(f.isDirectory()) {
                results.addAll(walk(f, filter));
            }
            else {
                if( filter.accepts(f) ) {
                    results.add(f);
                }
            }
        }
        return results;
    }
}
