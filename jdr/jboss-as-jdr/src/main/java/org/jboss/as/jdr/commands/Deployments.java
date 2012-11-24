package org.jboss.as.jdr.commands;

import org.jboss.as.jdr.util.Utils;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
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

/**
 * User: jhjaggars
 * Date: 10/31/12
 * Time: 10:20 AM
 */
public class Deployments extends JdrCommand {

    Map<String, String> hashToName;
    VirtualFile jbossHome;
    final XPath xPath;
    final XPathExpression pattern;

    public Deployments () throws XPathExpressionException {
        hashToName = new HashMap<String, String>();
        jbossHome = VFS.getChild(this.env.getJbossHome());
        xPath = XPathFactory.newInstance().newXPath();
        pattern = xPath.compile("./deployments/deployment");
    }

    private void buildMap() throws Exception {
        Collection<VirtualFile> candidates = new ArrayList<VirtualFile>();
        candidates.addAll(jbossHome.getChild("standalone").getChildrenRecursively());
        candidates.addAll(jbossHome.getChild("domain").getChildrenRecursively());

        for (VirtualFile f : candidates) {
            String xml = Utils.toString(f);
            NodeList deployments = (NodeList) pattern.evaluate(new InputSource(new StringReader(xml)), XPathConstants.NODESET);
            for (int i = 0; i < deployments.getLength(); ++i) {
                Node node = deployments.item(i);
                String name = node.getAttributes().getNamedItem("name").toString();
                String sha1 = node.getChildNodes().item(0).getAttributes().getNamedItem("sha1").toString();
                hashToName.put(sha1, name);
            }
        }
    }

    public void execute() {

    }
}
