/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utility;

import java.io.IOException;
import java.io.StringReader;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author saray
 */
public class GestionXML {

    public static Element stringTOXML(String eString) throws IOException, JDOMException {
        SAXBuilder saxBuilder = new SAXBuilder();
        StringReader stringReader = new StringReader(eString);
        //org.jdom.Document doc = saxBuilder.build(stringReader);
        Document doc = saxBuilder.build(stringReader);
        return doc.getRootElement();
    }

    public static String xmlToString(Element element) {
        XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());
        String xmlStringElement = outputter.outputString(element);
        xmlStringElement = xmlStringElement.replace("\n", "");
        return xmlStringElement;

    }//xmlToString

}//fin clase
