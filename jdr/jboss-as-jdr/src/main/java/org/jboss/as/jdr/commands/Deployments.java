package org.jboss.as.jdr.commands;

import org.jboss.as.jdr.resource.ResourceFactory;
import org.jboss.as.jdr.resource.Resource;
import org.jboss.as.jdr.resource.Utils;
import org.jboss.as.jdr.util.Find;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

/**
 * User: jhjaggars
 * Date: 10/31/12
 * Time: 10:20 AM
 */
public class Deployments extends JdrCommand {

    Map<String, String> hashToName;
    Resource jbossHome;
    final XPath xPath;
    final XPathExpression pattern;
    Collection<Resource> deployments;

    public Deployments () throws XPathExpressionException {
        hashToName = new HashMap<String, String>();
        deployments = new ArrayList<Resource>();
        xPath = XPathFactory.newInstance().newXPath();
        pattern = xPath.compile("./deployments/deployment");
    }

    private void prepare() throws Exception {
        Collection<Resource> candidates = new ArrayList<Resource>();
        for (String dir : Arrays.asList("standalone", "domain")) {
            Resource path = jbossHome.getChild(dir);
            candidates.addAll(Find.walk(path.getChild("configuration"), ".*\\.xml"));
            deployments.addAll(Find.walk(path.getChild("deployments")));
            deployments.addAll(Find.walk(path, "content"));
        }

        for (Resource f : candidates) {
            String xml = Utils.resourceToString(f);
            NodeList deployments = (NodeList) pattern.evaluate(new InputSource(new StringReader(xml)), XPathConstants.NODESET);
            for (int i = 0; i < deployments.getLength(); ++i) {
                Node node = deployments.item(i);
                String name = node.getAttributes().getNamedItem("name").toString();
                String sha1 = node.getChildNodes().item(0).getAttributes().getNamedItem("sha1").toString();
                hashToName.put(sha1, name);
            }
        }
    }

    @Override
    public void execute() throws Exception {
        jbossHome = ResourceFactory.getResource(this.env.getJbossHome());
        prepare();
        for (Map.Entry<String, String> item : hashToName.entrySet()) {
            System.out.println(item.getKey() + " -> " + item.getValue());
        }
        for (Resource r : deployments) {
            System.out.println(r.getPath());
        }
    }
}
