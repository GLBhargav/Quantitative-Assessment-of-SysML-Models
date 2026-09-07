import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Arrays;
import java.util.Map;

public class Complexity {

    private static int requirementComplexity;
    private static double estimatedComplexity;

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("XML Files", "xml"));

        Object[] options = {"Analyze 1 Model", "Compare 2 Models"};
        int userChoice = JOptionPane.showOptionDialog(frame,
                "Do you want to analyze one model or compare two models?",
                "Model Analysis Choice",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (userChoice == JOptionPane.YES_OPTION) {
            analyzeSingleModel(frame, fileChooser);
        } else if (userChoice == JOptionPane.NO_OPTION) {
            compareTwoModels(frame, fileChooser);
        } else {
            JOptionPane.showMessageDialog(frame, "No option selected, exiting.", "Exit", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    private static void analyzeSingleModel(JFrame frame, JFileChooser fileChooser) {
        JOptionPane.showMessageDialog(frame, "Select the XML file for the model.");
        int option = fileChooser.showOpenDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            String xmlFilePath = fileChooser.getSelectedFile().getPath();
            StringBuilder htmlResults = new StringBuilder("<html><table border='1'><tr><th colspan='7'>Model: " + xmlFilePath + "</th></tr><tr><th>Block Name</th><th>Attributes</th><th>Methods</th><th>Signals</th><th>Connectors</th><th>States</th><th>Complexity</th></tr>");

            askQuestions("Model");
            double totalComplexity = computeModelComplexity(xmlFilePath, htmlResults);
            htmlResults.append(String.format("<tr><td colspan='6' style='text-align:center;'><b>Total Complexity of the Model</b></td><td>%.2f</td></tr>", totalComplexity));
            htmlResults.append("</table><br><br>");

            // Add complexity of capturing requirements
            htmlResults.append("Complexity of Capturing Requirements: ").append(requirementComplexity).append("<br>");

            // Add estimated effort to build the system
            htmlResults.append("Estimated Effort to Build the System: ").append(estimatedComplexity).append("<br>");

            htmlResults.append("</html>");

            System.out.println("\nUse Case Diagram Information:");
            UseCase.extractUseCaseDiagramInfo(xmlFilePath);

            JOptionPane.showMessageDialog(frame, htmlResults.toString(), "Complexity Analysis Result", JOptionPane.INFORMATION_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(frame, "No file selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private static void compareTwoModels(JFrame frame, JFileChooser fileChooser) {
        StringBuilder htmlResults = new StringBuilder("<html><body><table><tr><td>");

        // Model 1
        JOptionPane.showMessageDialog(frame, "Select the XML file for the first model.");
        String xmlFilePath1 = selectFile(frame, fileChooser, "Model 1");
        StringBuilder model1Results = new StringBuilder("<table border='1'><tr><th>Block Name</th><th>Attributes</th><th>Methods</th><th>Signals</th><th>Connectors</th><th>States</th><th>Complexity</th></tr>");
        double totalComplexityModel1 = 0;
        if (!xmlFilePath1.isEmpty()) {
            askQuestions("Model 1");
            System.out.println("\nUse Case Diagram Information:");
            UseCase.extractUseCaseDiagramInfo(xmlFilePath1);
            totalComplexityModel1 = computeModelComplexity(xmlFilePath1, model1Results);
            model1Results.append(String.format("<tr><td colspan='6' style='text-align:center;'><b>Total Complexity of Model 1</b></td><td>%.2f</td></tr>", totalComplexityModel1));
            // Add requirement complexity and estimated effort
            model1Results.append("<tr><td colspan='7'>Requirement Complexity: ").append(requirementComplexity).append(", Estimated Effort to Build the System: ").append(estimatedComplexity).append("</td></tr>");
        }
        model1Results.append("</table>");

        // Model 2
        JOptionPane.showMessageDialog(frame, "Select the XML file for the second model.");
        String xmlFilePath2 = selectFile(frame, fileChooser, "Model 2");
        StringBuilder model2Results = new StringBuilder("<table border='1'><tr><th>Block Name</th><th>Attributes</th><th>Methods</th><th>Signals</th><th>Connectors</th><th>States</th><th>Complexity</th></tr>");
        double totalComplexityModel2 = 0;
        if (!xmlFilePath2.isEmpty()) {
            askQuestions("Model 2");
            System.out.println("\nUse Case Diagram Information:");
            UseCase.extractUseCaseDiagramInfo(xmlFilePath2);
            totalComplexityModel2 = computeModelComplexity(xmlFilePath2, model2Results);
            model2Results.append(String.format("<tr><td colspan='6' style='text-align:center;'><b>Total Complexity of Model 2</b></td><td>%.2f</td></tr>", totalComplexityModel2));
            // Add requirement complexity and estimated effort
            model2Results.append("<tr><td colspan='7'>Requirement Complexity: ").append(requirementComplexity).append(", Estimated Effort to Build the System: ").append(estimatedComplexity).append("</td></tr>");
        }

        model2Results.append("</table>");

        htmlResults.append(model1Results.toString())
                   .append("</td><td>") // Separate the model tables
                   .append(model2Results.toString())
                   .append("</td></tr></table></body></html>");

        JOptionPane.showMessageDialog(frame, htmlResults.toString(), "Complexity Comparison Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private static String selectFile(JFrame frame, JFileChooser fileChooser, String modelName) {
        JOptionPane.showMessageDialog(frame, "Select the XML file for " + modelName + ".");
        int option = fileChooser.showOpenDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getPath();
        } else {
            JOptionPane.showMessageDialog(frame, "No file selected for " + modelName + ".", "Error", JOptionPane.ERROR_MESSAGE);
            return ""; // Return an empty string to indicate no file was selected
        }
    }

	private static double computeModelComplexity(String xmlFilePath, StringBuilder results) {
        Map<String, BlockDiagram.BlockInfo> blockInfoMap = BlockDiagram.extractBlockDiagramInfo(xmlFilePath);
        Map<String, SMD.StateMachineInfo> stateMachineInfoMap = SMD.extractStateMachineInfo(xmlFilePath);

        double totalComplexity = 0;
        for (Map.Entry<String, BlockDiagram.BlockInfo> blockEntry : blockInfoMap.entrySet()) {
            String blockName = blockEntry.getKey();
            BlockDiagram.BlockInfo blockInfo = blockEntry.getValue();
            double blockComplexity = 0;

            if (stateMachineInfoMap.containsKey(blockName)) {
                SMD.StateMachineInfo smInfo = stateMachineInfoMap.get(blockName);
                blockComplexity = blockInfo.numberOfAttributes + blockInfo.numberOfMethods +
                                   (double) blockInfo.signals.size() *
                                   ((smInfo.numberOfConnectors - smInfo.numberOfStates + 2) / (double) smInfo.numberOfStates);

                totalComplexity += blockComplexity;

                // Append individual block complexity details to the results
                results.append(String.format("<tr><td>%s</td><td>%d</td><td>%d</td><td>%d</td><td>%d</td><td>%d</td><td>%.2f</td></tr>",
                                             blockName, blockInfo.numberOfAttributes, blockInfo.numberOfMethods,
                                             blockInfo.signals.size(), smInfo.numberOfConnectors, smInfo.numberOfStates, 
                                             blockComplexity));
            }
        }
        return totalComplexity;
    }

	 private static void askQuestions(String modelName) {
	        // Question 1: Hours worked
	        String[] hoursOptions = {
	                "0 hrs", "1-8 hrs", "9-40 hrs", "41-160 hrs", "161-320 hrs", "321-480 hrs"
	        };
	        String hoursResponse = (String) JOptionPane.showInputDialog(
	                null,
	                modelName + ": How many hours did it take to work on the requirements?",
	                "Requirement Analysis - Hours",
	                JOptionPane.QUESTION_MESSAGE,
	                null,
	                hoursOptions,
	                hoursOptions[0]
	        );

	        // Assign complexity index to the selected option
	        int hoursComplexityIndex = Arrays.asList(hoursOptions).indexOf(hoursResponse);
	        int hoursComplexity = 0;
	        switch (hoursComplexityIndex) {
	            case 0:
	                hoursComplexity = 0;
	                break;
	            case 1:
	                hoursComplexity = 1;
	                break;
	            case 2:
	                hoursComplexity = 2;
	                break;
	            case 3:
	                hoursComplexity = 3;
	                break;
	            case 4:
	                hoursComplexity = 4;
	                break;
	            case 5:
	                hoursComplexity = 5;
	                break;
	        }

        // Question 2: Skill level
        String[] skillOptions = {
            "Highly competent employee, long time at company, previous similar projects",
            "Competent employee, adequate time at company, some experience in similar projects",
            "Short time at company, 1-2 similar projects",
            "New employee, industry experience, no project experience",
            "New employee, no industry or project experience"
        };
        String skillResponse = (String) JOptionPane.showInputDialog(
                null,
                modelName + ": What was the skill level of the person or the team as a whole?",
                "Requirement Analysis - Skill Level",
                JOptionPane.QUESTION_MESSAGE,
                null,
                skillOptions,
                skillOptions[0]
        );

        // Assign complexity index to the skill level option
        int skillComplexityIndex = Arrays.asList(skillOptions).indexOf(skillResponse);
        int skillComplexity = 0; // Default complexity index
        switch (skillComplexityIndex) {
            case 0:
                skillComplexity = 1;
                break;
            case 1:
                skillComplexity = 2;
                break;
            case 2:
                skillComplexity = 3;
                break;
            case 3:
                skillComplexity = 4;
                break;
            case 4:
                skillComplexity = 5;
                break;
        }

        // Calculate requirement complexity by multiplying the complexity indices
        requirementComplexity = hoursComplexity * skillComplexity;

        // Get UUCW, UAW, TCF, ECF, and estimated man hours
        String uucwInput = JOptionPane.showInputDialog(null, "Enter Unadjusted Use Case Weight (UUCW):");
        int uucw = Integer.parseInt(uucwInput);

        String uawInput = JOptionPane.showInputDialog(null, "Enter Unadjusted Actors Weight (UAW):");
        int uaw = Integer.parseInt(uawInput);

        String tcfInput = JOptionPane.showInputDialog(null, "Enter Technical Complexity Factor (TCF):");
        double tcf = Double.parseDouble(tcfInput);

        String ecfInput = JOptionPane.showInputDialog(null, "Enter Environmental Complexity Factor (ECF):");
        double ecf = Double.parseDouble(ecfInput);

        String manHoursInput = JOptionPane.showInputDialog(null, modelName + ": Enter estimated man hours for each UCP:", "Use Case Diagram - Man Hours", JOptionPane.QUESTION_MESSAGE);
        double manHours = Integer.parseInt(manHoursInput);
        
        // Calculate Use Case Points (UCP)
        double ucp = (uucw + uaw) * tcf * ecf;
        
        // Calculate the estimated effort or complexity of the project
        estimatedComplexity = (double) (ucp * manHours);

        // Display the requirement complexity
        System.out.println("Requirement Complexity: " + requirementComplexity);
        System.out.println("Estimated Effort to Build the System: " + estimatedComplexity);
    }
}