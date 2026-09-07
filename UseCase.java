import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;

public class UseCase {

    public static void extractUseCaseDiagramInfo(String xmlFilePath) {
        try {
            File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            NodeList diagramList = document.getElementsByTagName("UseCaseDiagramPanel");
            for (int i = 0; i < diagramList.getLength(); i++) {
                Node diagramNode = diagramList.item(i);
                if (diagramNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element diagramElement = (Element) diagramNode;
                    System.out.println("Use Case Diagram: " + diagramElement.getAttribute("name"));

                    // Process COMPONENT elements for actors and use cases
                    NodeList componentList = diagramElement.getElementsByTagName("COMPONENT");
                    for (int j = 0; j < componentList.getLength(); j++) {
                        Element component = (Element) componentList.item(j);
                        NodeList infoParams = component.getElementsByTagName("infoparam");
                        for (int k = 0; k < infoParams.getLength(); k++) {
                            Element infoParam = (Element) infoParams.item(k);
                            System.out.println(infoParam.getAttribute("name") + ": " + infoParam.getAttribute("value"));
                        }
                    }

                    // Extracting connector information
                    NodeList connectorList = diagramElement.getElementsByTagName("CONNECTOR");
                    System.out.println("Connectors:");
                    for (int j = 0; j < connectorList.getLength(); j++) {
                        Element connector = (Element) connectorList.item(j);
                        String connectorType = connector.getAttribute("type");
                        NodeList infoParams = connector.getElementsByTagName("infoparam");
                        for (int k = 0; k < infoParams.getLength(); k++) {
                            Element infoParam = (Element) infoParams.item(k);
                            String name = infoParam.getAttribute("name");
                            String value = infoParam.getAttribute("value");
                            System.out.println("Connector Type: " + connectorType + ", " + name + ": " + value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
