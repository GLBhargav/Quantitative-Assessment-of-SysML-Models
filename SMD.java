import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.HashMap;
import java.util.Map;

public class SMD {

    // A helper class to store state machine information
    static class StateMachineInfo {
        String name;
        int numberOfStates;
        int numberOfConnectors;

        StateMachineInfo(String name) {
            this.name = name;
            this.numberOfStates = 0;
            this.numberOfConnectors = 0;
        }
    }

    public static Map<String, StateMachineInfo> extractStateMachineInfo(String xmlFilePath) {
        File xmlFile = new File(xmlFilePath);
        Map<String, StateMachineInfo> stateMachineInfoMap = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            NodeList diagramList = document.getElementsByTagName("AVATARStateMachineDiagramPanel");

            for (int i = 0; i < diagramList.getLength(); i++) {
                Element diagramElement = (Element) diagramList.item(i);
                String diagramName = diagramElement.getAttribute("name");
                StateMachineInfo smInfo = new StateMachineInfo(diagramName);

                NodeList stateList = diagramElement.getElementsByTagName("infoparam");
                for (int j = 0; j < stateList.getLength(); j++) {
                    Element stateElement = (Element) stateList.item(j);
                    String name = stateElement.getAttribute("name");

                    if (name.startsWith("state0") || name.startsWith("State")) {
                        smInfo.numberOfStates++;
                    }
                }

                NodeList connectorList = diagramElement.getElementsByTagName("CONNECTOR");
                smInfo.numberOfConnectors = connectorList.getLength();

                stateMachineInfoMap.put(diagramName, smInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stateMachineInfoMap;
    }
}
