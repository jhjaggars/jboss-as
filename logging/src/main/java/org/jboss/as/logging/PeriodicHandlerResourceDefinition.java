/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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

package org.jboss.as.logging;

import static org.jboss.as.logging.CommonAttributes.APPEND;
import static org.jboss.as.logging.CommonAttributes.AUTOFLUSH;
import static org.jboss.as.logging.CommonAttributes.FILE;
import static org.jboss.as.logging.CommonAttributes.PERIODIC_ROTATING_FILE_HANDLER;
import static org.jboss.as.logging.CommonAttributes.SUFFIX;

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.services.path.ResolvePathHandler;
import org.jboss.logmanager.handlers.PeriodicRotatingFileHandler;

/**
 * @author <a href="mailto:tomaz.cerar@redhat.com">Tomaz Cerar</a>
 */
class PeriodicHandlerResourceDefinition extends AbstractFileHandlerDefinition {

    static final PathElement PERIODIC_HANDLER_PATH = PathElement.pathElement(PERIODIC_ROTATING_FILE_HANDLER);
    static final AttributeDefinition[] ATTRIBUTES = Logging.join(DEFAULT_ATTRIBUTES, AUTOFLUSH, APPEND, FILE, SUFFIX);

    public PeriodicHandlerResourceDefinition(final ResolvePathHandler resolvePathHandler, final boolean includeLegacyAttributes) {
        super(PERIODIC_HANDLER_PATH, PeriodicRotatingFileHandler.class, resolvePathHandler,
                (includeLegacyAttributes ? Logging.join(ATTRIBUTES, LEGACY_ATTRIBUTES) : ATTRIBUTES));
    }

}
