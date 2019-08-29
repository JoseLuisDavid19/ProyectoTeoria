/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package afd_minimizador;

/**
 *
 * @author josel_0v10him
 */
import java.io.File;
import java.io.IOException;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class jFlapFile {

    String path;
    String type;
    boolean continuar;
    Hashtable<String, Estado> TodosEstados;
    Distinguible DistinArray[][];

    public jFlapFile(String path) {
        this.path = path;
    }

    public boolean newJflapFile() throws TransformerConfigurationException, TransformerException {
        continuar = false;
        try {

            File file = new File(path);

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            if (doc.hasChildNodes()) {
                type = doc.getElementsByTagName("type").item(0).getTextContent();
                setStates(doc.getElementsByTagName("state"));
                if (TodosEstados.isEmpty()) {
                    return false;
                }
                setTransitions(doc.getElementsByTagName("transition"));
                deleteUnReachable();
                setDistinguible(0);
                printStates();
                saveDocument();
            }

            return true;
        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    private void deleteUnReachable() {
        int amountState = TodosEstados.size();
        boolean reachable[] = new boolean[amountState];
        Set<String> keys = TodosEstados.keySet();
        for (String key : keys) {
            for (Arista to : TodosEstados.get(key).to) {
                if (reachable[to.to] == false || TodosEstados.get(key).initialState || TodosEstados.get(key).finalState) {
                    reachable[to.to] = true;
                }
            }
        }
        int count = 0;
        for (boolean array : reachable) {
            if (!array) {
                TodosEstados.remove("q" + count);
            }
            count++;
        }
    }

    private void setDistinguible(int value) {
        int amountState = TodosEstados.size();
        DistinArray = new Distinguible[amountState - 1][amountState - 1];
        FillDistinguibleArray(amountState);

        for (int x = 0; x < amountState - 1; x++) {
            for (int y = 0; y <= x; y++) {
                Distinguible distin = DistinArray[x][y];
                Estado state = TodosEstados.get(distin.Estado1);
                Estado state2 = TodosEstados.get(distin.Estado2);
                if (state.finalState && !state2.finalState || state2.finalState && !state.finalState) {
                    if (distin.value == -2) {
                        distin.value = value;
                        DistinArray[x][y] = distin;
                    }
                }else{
                        distin.value = -1;
                        DistinArray[x][y] = distin;
                    }
            }
        }
        paso1(1);
        printDistin();
    }

    private void paso1(int value) {
        int amountState = TodosEstados.size();
         continuar=false;
        for (int x = 0; x < amountState - 1; x++) {
            for (int y = 0; y <= x; y++) {
                Distinguible distin = DistinArray[x][y];
                Estado state = TodosEstados.get(distin.Estado1);
                Estado state2 = TodosEstados.get(distin.Estado2);
                for (int to = 0; to < state.to.size(); to++) {
                    if (distin.value == -1) {
                        String entry=state.to.get(to).entradas;
                        String entry2=getState(entry,state2);
                        
                        if(entry.equalsIgnoreCase(entry2)){
                            String estado;
                            String estado2;
                            int parOrdenado=state.to.get(to).to;
                            int parOrdenado2=state2.to.get(to).to;
                            if(parOrdenado>parOrdenado2){
                                estado="q"+parOrdenado;
                                estado2="q"+parOrdenado2;
                            }else{
                                estado="q"+parOrdenado2;
                                estado2="q"+parOrdenado;
                            }
                            if(setValueFromArray(estado,estado2,value)){
                                distin.value=value;
                                continuar=true;
                            }
                        }
                    }
                }

            }
        }
        if(continuar)
            paso1(value++);
    }

    private String getState(String entry,Estado listto){
         for (int to = 0; to < listto.to.size(); to++) {
            if(listto.to.get(to).entradas.equalsIgnoreCase(entry)){
                return listto.to.get(to).entradas;
            }
         }
        
    return "";
    }
    private boolean setValueFromArray(String estado, String estado2, int value){
        int amount=TodosEstados.size();
        for (int x = 0; x <amount-1 ; x++) {
            for (int y = 0; y <= x; y++) {
                if(!DistinArray[x][y].Estado1.equalsIgnoreCase(estado))break;
                if(DistinArray[x][y].Estado2.equalsIgnoreCase(estado2)){
                    if(DistinArray[x][y].value==value-1){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private void FillDistinguibleArray(int amount) {
        int id1 = 1;
        int id2 = 0;
        for (int x = 0; x < amount - 1; x++) {
            if (TodosEstados.containsKey("q" + id1)) {
                id2 = 0;
                for (int y = 0; y <= x; y++) {
                    id2 = id2 == amount ? id2 - 1 : id2;
                    if (TodosEstados.containsKey("q" + id2)) {
                        DistinArray[x][y] = new Distinguible("q" + id1, "q" + id2);
                        id2++;
                    } else {
                        id2++;
                        y--;
                    }
                }
                id1++;
            } else {
                id1++;
                x--;
            }
        }
    }

    private static Node getCompany(Document doc, String id, String name, String age, String role) {
        Element company = doc.createElement("Company");
        company.setAttribute("id", id);
        company.appendChild(getCompanyElements(doc, company, "Name", name));
        company.appendChild(getCompanyElements(doc, company, "Type", age));
        company.appendChild(getCompanyElements(doc, company, "Employees", role));
        return company;
    }

    // utility method to create text node
    private static Node getCompanyElements(Document doc, Element element, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

    private void saveDocument() throws TransformerException {
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
        try {
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            Element mainRootElement = doc.createElement("structure");
            doc.appendChild(mainRootElement);
            mainRootElement.appendChild(getCompany(doc, "1", "Paypal", "Payment", "1000"));
            mainRootElement.appendChild(getCompany(doc, "2", "eBay", "Shopping", "2000"));
            mainRootElement.appendChild(getCompany(doc, "3", "Google", "Search", "3000"));
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(new File("./xml.xml"));
            transformer.transform(domSource, streamResult);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(jFlapFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void printStates() {
        Set<String> keys = TodosEstados.keySet();
        for (String key : keys) {
            TodosEstados.get(key).print();
        }
    }

    private void setStates(NodeList nodeList) {
        TodosEstados = new Hashtable<>();

        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                boolean initial = false;
                boolean finalState = false;
                Element eElement = (Element) tempNode;
                NodeList node = eElement.getChildNodes();
                String tag = node.item(node.getLength() - 2).getNodeName();
                if (tag.equalsIgnoreCase("initial")) {
                    initial = true;
                }
                if (tag.equalsIgnoreCase("final")) {
                    finalState = true;
                }
                String name = tempNode.getAttributes().item(1).getNodeValue();
                int id = (int) parseDouble(tempNode.getAttributes().item(0).getNodeValue());
                int x = (int) parseDouble(eElement.getElementsByTagName("x").item(0).getTextContent());
                int y = (int) parseDouble(eElement.getElementsByTagName("y").item(0).getTextContent());
                TodosEstados.put(name, new Estado(name, id, initial, finalState, x, y));
            }
        }
    }

    private void setTransitions(NodeList nodeList) {
        for (int count = 0; count < nodeList.getLength(); count++) {

            Node tempNode = nodeList.item(count);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) tempNode;
                int stateFrom = parseInt(eElement.getElementsByTagName("from").item(0).getTextContent());
                Estado state = getTodosEstados(stateFrom);
                int to = parseInt(eElement.getElementsByTagName("to").item(0).getTextContent());
                Arista transition = new Arista(to);
                NodeList entries = eElement.getElementsByTagName("read");
                transition.entradas = (entries.item(0).getTextContent());
                state.AddTransition(transition);
            }

        }
    }

    private Estado getTodosEstados(int id) {
        Set<String> keys = TodosEstados.keySet();
        for (String key : keys) {
            if (TodosEstados.get(key).id == id) {
                return TodosEstados.get(key);
            }
        }
        return null;
    }

    private void printNote(NodeList nodeList) {

        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) tempNode;

                System.out.println("From " + eElement.getElementsByTagName("from").item(0).getTextContent());
                System.out.println("To " + eElement.getElementsByTagName("to").item(0).getTextContent());
            }

        }

    }

    private void printDistin() {
        for (int x = 0; x < 6; x++) {
            
            for (int y = 0; y <= x; y++) {
                if(y<1){System.out.print("["+DistinArray[x][y].Estado1+"]");}
                String show=DistinArray[x][y].value!=-1?DistinArray[x][y].value+"":" ";
                System.out.print("[" + show + "]");
            }
            System.out.println("");
        }
    }

}
