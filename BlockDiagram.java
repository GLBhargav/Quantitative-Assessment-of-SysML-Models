import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockDiagram {

    public static class BlockInfo {
        private String name;
        List<String> signals = new ArrayList<>();
        int numberOfAttributes;
        int numberOfMethods;

        public BlockInfo(String name) {
            this.name = name;
        }

        public void addSignal(String signal) {
            signals.add(signal);
        }

        public void incrementAttributes() {
            numberOfAttributes++;
        }

        public void incrementMethods() {
            numberOfMethods++;
        }

        public String getName() {
            return name;
        }

        public List<String> getSignals() {
            return signals;
        }

        public int getNumberOfAttributes() {
            return numberOfAttributes;
        }

        public int getNumberOfMethods() {
            return numberOfMethods;
        }
    }

    public static Map<String, BlockInfo> extractBlockDiagramInfo(String xmlFilePath) {
        File xmlFile = new File(xmlFilePath);
        Map<String, BlockInfo> blockInfoMap = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            NodeList componentList = document.getElementsByTagName("COMPONENT");

            for (int i = 0; i < componentList.getLength(); i++) {
                Element component = (Element) componentList.item(i);
                NodeList infoParams = component.getElementsByTagName("infoparam");

                for (int j = 0; j < infoParams.getLength(); j++) {
                    Element infoParam = (Element) infoParams.item(j);
                    String paramName = infoParam.getAttribute("name");

                    if (paramName.startsWith("Block")) {
                        String blockName = infoParam.getAttribute("value");
                        BlockInfo blockInfo = new BlockInfo(blockName);

                        Element extraparam = (Element) component.getElementsByTagName("extraparam").item(0);
                        NodeList children = extraparam.getChildNodes();

                        for (int k = 0; k < children.getLength(); k++) {
                            if (children.item(k) instanceof Element) {
                                Element child = (Element) children.item(k);
                                switch (child.getTagName()) {
                                    case "Signal":
                                        blockInfo.addSignal(child.getAttribute("value"));
                                        break;
                                    case "Attribute":
                                        blockInfo.incrementAttributes();
                                        break;
                                    case "Method":
                                        blockInfo.incrementMethods();
                                        break;
                                }
                            }
                        }

                        blockInfoMap.put(blockName, blockInfo);
                    }
                }
            }
            
         // Output the block information
            System.out.println("Block Diagram Information:");
            blockInfoMap.forEach((blockName, blockInfo) -> {
                System.out.println("Block: " + blockName);
                System.out.println("Number of Attributes: " + blockInfo.numberOfAttributes);
                System.out.println("Number of Methods: " + blockInfo.numberOfMethods);
                System.out.println("Number of Signals: " + blockInfo.signals.size());
                if (!blockInfo.signals.isEmpty()) {
                    System.out.println("Signals: ");
                    blockInfo.signals.forEach(signal -> System.out.println("   - " + signal));
                } else {
                    System.out.println("No Signals");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return blockInfoMap;
    }
}
