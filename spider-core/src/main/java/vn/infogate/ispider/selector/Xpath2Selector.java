package vn.infogate.ispider.selector;

import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.lib.NamespaceConstant;
import net.sf.saxon.xpath.XPathEvaluator;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Xpath2Selector implements Selector {

    private final String xpathStr;
    private XPathExpression xPathExpression;

    public Xpath2Selector(String xpathStr) {
        this.xpathStr = xpathStr;
        try {
            init();
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException("XPath error!", e);
        }
    }

    enum XPath2NamespaceContext implements NamespaceContext {

        INSTANCE;

        private final Map<String, String> prefix2NamespaceMap = new ConcurrentHashMap<>();

        private final Map<String, List<String>> namespace2PrefixMap = new ConcurrentHashMap<>();

        private void put(String prefix, String namespaceURI) {
            prefix2NamespaceMap.put(prefix, namespaceURI);
            List<String> prefixes = namespace2PrefixMap.get(namespaceURI);
            if (prefixes == null) prefixes = new ArrayList<>();
            prefixes.add(prefix);
            namespace2PrefixMap.put(namespaceURI, prefixes);
        }

        XPath2NamespaceContext() {
            put("fn", NamespaceConstant.FN);
            put("xslt", NamespaceConstant.XSLT);
        }

        @Override
        public String getNamespaceURI(String prefix) {
            return prefix2NamespaceMap.get(prefix);
        }

        @Override
        public String getPrefix(String namespaceURI) {
            List<String> prefixes = namespace2PrefixMap.get(namespaceURI);
            if (prefixes == null || prefixes.size() < 1) {
                return null;
            }
            return prefixes.get(0);
        }

        @Override
        public Iterator getPrefixes(String namespaceURI) {
            List<String> prefixes = namespace2PrefixMap.get(namespaceURI);
            if (prefixes == null || prefixes.size() < 1) {
                return null;
            }
            return prefixes.iterator();
        }
    }

    private void init() throws XPathExpressionException {
        XPathEvaluator xPathEvaluator = new XPathEvaluator();
        xPathEvaluator.setNamespaceContext(XPath2NamespaceContext.INSTANCE);
        xPathExpression = xPathEvaluator.compile(xpathStr);
    }

    @Override
    public String select(String text) {
        try {
            Object result = doSelect(text);
            if (result instanceof NodeList) {
                NodeList nodeList = (NodeList) result;
                if (nodeList.getLength() == 0) {
                    return null;
                }
                Node item = nodeList.item(0);
                if (item.getNodeType() == Node.ATTRIBUTE_NODE || item.getNodeType() == Node.TEXT_NODE) {
                    return item.getTextContent();
                } else {
                    StreamResult xmlOutput = new StreamResult(new StringWriter());
                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                    transformer.transform(new DOMSource(item), xmlOutput);
                    return xmlOutput.getWriter().toString();
                }
            }
            return result.toString();
        } catch (Exception e) {
            log.error("select text error! " + xpathStr, e);
        }
        return null;
    }

    private Object doSelect(String text) throws Exception {
        var htmlCleaner = new HtmlCleaner();
        var tagNode = htmlCleaner.clean(text);
        var document = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
        try {
            return xPathExpression.evaluate(document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            return xPathExpression.evaluate(document, XPathConstants.STRING);
        }
    }

    @Override
    public List<String> selectList(String text) {
        List<String> results = new ArrayList<>();
        try {
            Object result = doSelect(text);
            if (result instanceof NodeList) {
                NodeList nodeList = (NodeList) result;
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                StreamResult xmlOutput = new StreamResult();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node item = nodeList.item(i);
                    if (item.getNodeType() == Node.ATTRIBUTE_NODE || item.getNodeType() == Node.TEXT_NODE) {
                        results.add(item.getTextContent());
                    } else {
                        xmlOutput.setWriter(new StringWriter());
                        transformer.transform(new DOMSource(item), xmlOutput);
                        results.add(xmlOutput.getWriter().toString());
                    }
                }
            } else {
                results.add(result.toString());
            }
        } catch (Exception e) {
            log.error("select text error! " + xpathStr, e);
        }
        return results;
    }
}
