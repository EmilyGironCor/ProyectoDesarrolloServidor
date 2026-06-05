/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Domain;

import org.jdom.Element;

/**
 *
 * @author emily
 */
public interface XMLConvertible {
    public void toObject(Element element);//en el elemnt esta el objeto en xml. No retornamos nada por que solo ocupamos cambiar el estado del objeto
        public Element toXMLElement();//toXMLElement
}//fin clase
