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
import org.jboss.as.jdr.resource.filter.ResourceFilter;
import org.jboss.as.jdr.resource.filter.WildcardPathFilter;
import org.jboss.as.jdr.util.FilteredSanitizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CollectFiles extends JdrCommand {

    private ResourceFilter filter = ResourceFilter.TRUE;
    private ResourceFilter blacklistFilter = new RegexBlacklistFilter();
    private LinkedList<FilteredSanitizer> sanitizers = new LinkedList<FilteredSanitizer>();
    private Comparator<Resource> sorter = null;

    // -1 means no limit
    private long limit = -1;

    public CollectFiles(ResourceFilter filter) {
        this.filter = filter;
    }

    public CollectFiles(String pattern) {
        this.filter = new WildcardPathFilter(pattern);
    }

    public CollectFiles blacklist(ResourceFilter blacklistFilter) {
        this.blacklistFilter = blacklistFilter;
        return this;
    }

    public CollectFiles sanitizer(FilteredSanitizer sanitizer) {
        this.sanitizers.add(sanitizer);
        return this;
    }

    public CollectFiles sorter(Comparator<Resource> sorter){
        this.sorter = sorter;
        return this;
    }

    public CollectFiles limit(final long limit){
        this.limit = limit;
        return this;
    }

    @Override
    public void execute() throws Exception {
        Resource root = ResourceFactory.getResource(this.env.getJbossHome());
        List<Resource> matches = root.getChildrenRecursively(new AndFilter(this.filter, this.blacklistFilter));

        // order the files in some arbitrary way.. basically prep for the limiter so things like log files can
        // be gotten in chronological order.  Keep in mind everything that might be collected per the filter for
        // this collector (if the filter is too broad, you may collect unrelated logs, sort them, and then
        // get some limit on that set, which probably would be wrong).
        if(sorter != null){
            Collections.sort(matches, sorter);
        }

        // limit how much data we collect
        Limiter limiter = new Limiter(limit);
        Iterator<Resource> iter = matches.iterator();

        while(iter.hasNext() && !limiter.isDone()) {

            Resource f = iter.next();
            InputStream stream = limiter.limit(f.openStream(), f.getSize());

            for (FilteredSanitizer sanitizer : this.sanitizers) {
                if(sanitizer.accepts(f)){
                    stream = sanitizer.sanitize(stream);
                }
            }

            this.env.getZip().add(f, stream);
            Utils.safelyClose(stream);
        }
    }

    private static class Limiter {

        private long amountRead = 0;
        private long limit = -1;
        private boolean done = false;

        public Limiter(long limit){
            this.limit = limit;
        }

        public boolean isDone(){
            return done;
        }

        /**
         * Implemented to return an InputStream to cut down on memory usage from slurping the entire file
         * into memory.
         *
         * @param is
         * @param totalSize
         * @return
         * @throws IOException
         */
        public InputStream limit(final InputStream is, final long totalSize) throws IOException {

            // if we're limiting and know we're not going to consume the whole file, we skip
            // ahead so that we get the tail of the file instead of the beginning of it.
            if(limit != -1){
                long leftToRead = limit - amountRead;
                if(leftToRead < totalSize){
                    is.skip(totalSize - leftToRead);
                }
            }

            return new InputStream() {

                @Override
                public int read() throws IOException {

                    //we've read as much as we should
                    if(amountRead == limit){
                        done = true;
                        return -1;
                    }
                    amountRead++;
                    return is.read();
                }

                public int available() throws IOException {
                    return is.available();
                }

                public void close() throws IOException {
                    is.close();
                }

                public void mark(int readlimit) {
                    is.mark(readlimit);
                }

                public void reset() throws IOException {
                    is.reset();
                }

                public boolean markSupported() {
                    return is.markSupported();
                }

                public int read(byte[] b) throws IOException {
                    return is.read(b);
                }

                public int read(byte[] b, int off, int len) throws IOException {
                    return is.read(b, off, len);
                }

                public long skip(long n) throws IOException {
                    return is.skip(n);
                }
            };
        }

    }
}
