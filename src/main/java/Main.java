import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String xmlName = "data.xml";
        //List<Employee> list = parseCSV(columnMapping, fileName);
        //String json = listToJson(list);
        //writeString(json);
        List<Employee> employees = parseXML(xmlName);
        String json = listToJson(employees);
        writeString(json);
    }

    public static List<Employee> parseXML(String xmlName) throws ParserConfigurationException, SAXException, IOException {
        List<Employee> employees = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(xmlName));

        Element staffElement = document.getDocumentElement();
        NodeList nodeList = staffElement.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element employeeElement = (Element) node;
                Employee employee = new Employee();
//                System.out.println(employeeElement.getNodeName());
                NodeList employeeElementChildNodes = employeeElement.getChildNodes();
                for (int j = 0; j < employeeElementChildNodes.getLength(); j++) {
                    Node employeeNode = employeeElementChildNodes.item(j);
                    if (Node.ELEMENT_NODE ==  employeeNode.getNodeType()) {
                        Element employeeChildElement = (Element) employeeNode;
//                        System.out.println("    " + employeeChildElement.getNodeName() + "=" + employeeChildElement.getTextContent());
                        if(employeeChildElement.getNodeName().equals("id")) {
                            employee.id = Long.parseLong(employeeChildElement.getTextContent());
                        }

                        if(employeeChildElement.getNodeName().equals("firstName")) {
                            employee.firstName = employeeChildElement.getTextContent();
                        }

                        if(employeeChildElement.getNodeName().equals("lastName")) {
                            employee.lastName = employeeChildElement.getTextContent();
                        }

                        if(employeeChildElement.getNodeName().equals("country")) {
                            employee.country = employeeChildElement.getTextContent();
                        }

                        if(employeeChildElement.getNodeName().equals("age")) {
                            employee.age = Integer.parseInt(employeeChildElement.getTextContent());
                        }
                    }
                }
                employees.add(employee);
            }
        }
        return employees;
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = null;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader).
                    withMappingStrategy(strategy).
                    build();
            staff = csv.parse();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return staff;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json) {
        File file = new File("new_data.json");
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(json);
            fileWriter.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
