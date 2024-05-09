package Sici.Partida.Acesso;

import Funcoes.Db;
import Funcoes.VariaveisGlobais;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class StringToDocumentToString {
    private static Db conn = VariaveisGlobais.conexao;
    
    public static void main(String[] args) {        
        String xmlStr = "<?xml version=\"1.0\"?><menu><Cadastros><Sub code=\"1\" inclusao=\"true\" alteracao=\"true\" exclusao=\"true\" inativar=\"true\" carteira=\"false\" baixar=\"true\" fotos=\"false\" /><Sub code=\"2\" /><Sub code=\"3\" /></Cadastros><Caixa><Sub code=\"4\" /><Sub code=\"5\" /><Sub code=\"6\" /><Sub code=\"7\" /><Sub code=\"9\" /><Sub code=\"11\" /><Sub code=\"10\" /><Sub code=\"8\" /><Sub code=\"12\" /></Caixa><Movimento><Sub code=\"13\" /><Sub code=\"14\" /><Sub code=\"15\" /><Sub code=\"16\" /><Sub code=\"17\" /><Sub code=\"18\" /><Sub code=\"22\" /><Sub code=\"24\" /></Movimento><Relatorios><Sub code=\"23\" /><Sub code=\"25\" /><Sub code=\"26\" /><Sub code=\"28\" /><Sub code=\"29\" /><Sub code=\"30\" /><Sub code=\"31\" /><Sub code=\"32\" /><Sub code=\"33\" /><Sub code=\"34\" /><Sub code=\"35\" /><Sub code=\"36\" /><Sub code=\"39\" /><Sub code=\"43\" /><Sub code=\"42\" /></Relatorios><Gerencia><Sub code=\"44\" /><Sub code=\"45\" /><Sub code=\"46\" /><Sub code=\"47\" /><Sub code=\"49\" /><Sub code=\"52\" /><Sub code=\"55\" /></Gerencia></menu>";
        
        List<String> subMenus = pegaListSubMenus(xmlStr);
        
        String _Cadastros = subMenus.get(0);
        
        subMenuConverter(_Cadastros);
        
        //String str = convertDocumentToString(doc);
        //System.out.println(str);
    }

    public static List<String> pegaListSubMenus(String xmlStr) {
        List<String> retorno = new ArrayList<>();
        
        String _Cadastros = "", _Caixa = "", _Movimento = "";
        String _Relatorios = "", _Gerencia = "";
        
        int iniPos = -1; int fimPos = -1;
        // Cadastros
        iniPos = xmlStr.indexOf("<Cadastros>");
        fimPos = xmlStr.indexOf("</Cadastros>");      
        if (iniPos > -1) {
            _Cadastros = xmlStr.substring(iniPos, fimPos + "</Cadastros>".length());
            retorno.add(_Cadastros);
        } else retorno.add(null);
        
        // Caixa
        iniPos = xmlStr.indexOf("<Caixa>");
        fimPos = xmlStr.indexOf("</Caixa>");      
        if (iniPos > -1) { 
            _Caixa = xmlStr.substring(iniPos, fimPos + "</Caixa>".length());
            retorno.add(_Caixa);
        } else retorno.add(null);
                
        // Movimento
        iniPos = xmlStr.indexOf("<Movimento>");
        fimPos = xmlStr.indexOf("</Movimento>");      
        if (iniPos > -1) { 
            _Movimento = xmlStr.substring(iniPos, fimPos + "</Movimento>".length());
            retorno.add(_Movimento);
        } else retorno.add(null);
                
        // Relatorios
        iniPos = xmlStr.indexOf("<Relatorios>");
        fimPos = xmlStr.indexOf("</Relatorios>");      
        if (iniPos > -1) { 
            _Relatorios = xmlStr.substring(iniPos, fimPos + "</Relatorios>".length());
            retorno.add(_Relatorios);
        } else retorno.add(null);
                
        // Gerencia
        iniPos = xmlStr.indexOf("<Gerencia>");
        fimPos = xmlStr.indexOf("</Gerencia>");      
        if (iniPos > -1) { 
            _Gerencia = xmlStr.substring(iniPos, fimPos + "</Gerencia>".length());
            retorno.add(_Gerencia);
        } else retorno.add(null);        
        
        return retorno;
    }
    
    public static List<classSubMenu> subMenuConverter(String xmlStr) {
        if (xmlStr == null) return null;
        List<classSubMenu> subMenu = new ArrayList<>();
        
        Document doc = convertStringToDocument(xmlStr);
        NodeList listNode = doc.getChildNodes();
        int menus = listNode.item(listNode.getLength() - 1).getChildNodes().getLength();
        for (int i = 0; i < menus; i++) {
            int ntam = listNode.item(0).getChildNodes().item(i).getAttributes().getLength();
            int _id = -1; String _botoes = "";
            for (int j = 0; j < ntam; j++) {
                String _name = listNode.item(0).getChildNodes().item(i).getAttributes().item(j).getNodeName();
                String _value = listNode.item(0).getChildNodes().item(i).getAttributes().item(j).getNodeValue();
                if (listNode.item(0).getChildNodes().item(i).getAttributes().item(j).getNodeName().equalsIgnoreCase("code")) {
                    _id = Integer.parseInt(_value);
                } else {
                    _botoes += _name + "=\"" + _value + "\" ";
                }
            }
            Object[][] menuItens = null;
            try { 
                menuItens = conn.ReadFieldsTable(new String[] {"nome","rotina"}, "menuicones", "autoid = :id", new Object[][] {{"int", "id", _id}}); 
            } catch (SQLException SWLEx) {}
            String _nome = ""; String _rotina = "";
            if (menuItens != null) {
                _nome = menuItens[0][3].toString();
                _rotina = menuItens[1][3].toString();
            }
            subMenu.add(
                    new classSubMenu(
                        _id, 
                        _botoes.isEmpty() ? null : _botoes.substring(0, _botoes.length() - 1),
                        _nome,
                        _rotina
            ));
        }    
        return subMenu;
    }
    
    private static String menuConverter(String xmlStr) {
        Document doc = convertStringToDocument(xmlStr);
        NodeList listNode = doc.getChildNodes();
        int menus = listNode.item(listNode.getLength() - 1).getChildNodes().getLength();
        for (int i = 0; i < menus; i++) {
            System.out.println("Menus: " + listNode.item(0).getChildNodes().item(i).getNodeName());
            int ntam = listNode.item(0).getChildNodes().item(i).getChildNodes().getLength();
            for (int j = 0; j < ntam; j++) {
                System.out.println("  --> " + listNode.item(0).getChildNodes().item(i).getChildNodes().item(j).getNodeName());
                int mtam = listNode.item(0).getChildNodes().item(i).getChildNodes().item(j).getAttributes().getLength();
                for (int z = 0; z < mtam; z++) {
                    System.out.println(" = " + 
                            listNode.item(0).getChildNodes().item(i).getChildNodes().item(j).getAttributes().item(z).getNodeName() + 
                            " = " + listNode.item(0).getChildNodes().item(i).getChildNodes().item(j).getAttributes().item(z).getNodeValue());
                }
            }
        }
        return null;
    }
            
    public static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;  
        try  
        {  
            builder = factory.newDocumentBuilder();  
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) ); 
            return doc;
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
        return null;
    }

    public static int findInSubMenu(int id, List<classSubMenu> subMenus) {
        int nPos = -1;
        for (int i = 0; i < subMenus.size(); i++) {
            if (subMenus.get(i).getId() == id) {
                nPos = i;
                break;
            }
        }
        return nPos;
    }    
}