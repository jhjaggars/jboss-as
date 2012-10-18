package org.jboss.as.jdr.util;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: jhjaggars
 * Date: 10/18/12
 * Time: 12:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class XMLSanitizer implements Sanitizer {

    XPathExpression expression;
    DocumentBuilder builder;
    Transformer transformer;

    public XMLSanitizer (String pattern) throws Exception {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        expression = xpath.compile(pattern);

        DocumentBuilderFactory DBfactory = DocumentBuilderFactory.newInstance();
        DBfactory.setNamespaceAware(true);
        builder = DBfactory.newDocumentBuilder();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformer = transformerFactory.newTransformer();
    }

    public InputStream sanitize(InputStream in) throws Exception {
        Document doc = builder.parse(in);
        Object result = expression.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); i++) {
            nodes.item(i).setTextContent("");
        }
        DOMSource source = new DOMSource(doc);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        StreamResult outStream = new StreamResult(output);
        transformer.transform(source, outStream);
        return new ByteArrayInputStream(output.toByteArray());
    }
}
