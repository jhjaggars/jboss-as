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

import java.util.List;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.controller.parsing.ParseUtils;
import org.jboss.as.controller.persistence.SubsystemMarshallingContext;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLElementWriter;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

public final class JdrReportSubsystemParser implements XMLStreamConstants, XMLElementReader<List<ModelNode>>,
        XMLElementWriter<SubsystemMarshallingContext> {

    static final JdrReportSubsystemParser INSTANCE = new JdrReportSubsystemParser();

    public static JdrReportSubsystemParser getInstance() {
        return INSTANCE;
    }

    public void readElement(final XMLExtendedStreamReader reader, final List<ModelNode> list) throws XMLStreamException {
        ParseUtils.requireNoAttributes(reader);
        ParseUtils.requireNoContent(reader);

        final ModelNode subsystem = Util.createAddOperation(PathAddress.pathAddress(JdrReportExtension.SUBSYSTEM_PATH));
        list.add(subsystem);

        while (reader.hasNext() && reader.nextTag() != END_ELEMENT) {
            if (!reader.getLocalName().equals("plugins")) {
                throw ParseUtils.unexpectedElement(reader);
            }
            while (reader.hasNext() && reader.nextTag() != END_ELEMENT) {
                if (reader.isStartElement()) {
                    readPlugin(reader, list);
                }
            }
        }
    }

    private void readPlugin(final XMLExtendedStreamReader reader, final List<ModelNode> list) throws XMLStreamException {
        if (!reader.getLocalName().equals("plugin")) {
            throw ParseUtils.unexpectedElement(reader);
        }
        ModelNode addTypeOperation = new ModelNode();
        addTypeOperation.get(OP).set(ModelDescriptionConstants.ADD);

        String className = null;
        for (int i; i < reader.getAttributeCount(); i++) {
            String attr = reader.getAttributeLocalName(i);
            String value = reader.getAttributeValue(i);
            if (attr.equals("class")) {
                
            }
        }

        //Add the 'add' operation for each 'type' child
        PathAddress addr = PathAddress.pathAddress(SUBSYSTEM_PATH, PathElement.pathElement(TYPE, pluginClassName));
        addTypeOperation.get(OP_ADDR).set(addr.toModelNode());
        list.add(addTypeOperation);
    }

    /**
     * {@inheritDoc}
     */
    public void writeContent(final XMLExtendedStreamWriter writer, final SubsystemMarshallingContext context)
            throws XMLStreamException {
        context.startSubsystemElement(org.jboss.as.jdr.Namespace.CURRENT.getUriString(), false);
        writer.writeStartElement("plugins");
        ModelNode node = context.getModelNode();
        ModelNode type = node.get(TYPE);
        for (Property property : type.asPropertyList()) {
            writer.writeStartElement("plugin");
            writer.writeElementText(property.getValue());
        }

        writer.writeEndElement();
    }

}
