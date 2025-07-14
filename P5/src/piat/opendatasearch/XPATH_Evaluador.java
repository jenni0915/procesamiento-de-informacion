package piat.opendatasearch;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


/**
 * Clase para evaluar las expresiones XPath
 * Contiene un método estático, llamado evaluar() que se encarga de realizar las consultas XPath al fichero XML que se le pasa como parámetro 
 */
public class XPATH_Evaluador{

	/**
	 * Método que se encarga de evaluar las expresiones XPath sobre el fichero XML generado en la práctica 4
	 * @param	ficheroXML	Fichero XML a evaluar
	 * @return	Una lista con la propiedad resultante de evaluar cada expresión XPath
	 * @throws	IOException
	 * @throws	XPathExpressionException 
	 * @throws	ParserConfigurationException 
	 * @throws	SAXException 
	 */
	public static List<Propiedad> evaluar (String ficheroXML) throws IOException, XPathExpressionException {
		Document doc;
		List<Propiedad> list = new ArrayList<>();
		final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setIgnoringElementContentWhitespace(true);
		try {
			final DocumentBuilder builder = domFactory.newDocumentBuilder();
			doc = builder.parse(ficheroXML);
			XPath xpath = XPathFactory.newInstance().newXPath();
			String q = (String) xpath.evaluate("//query", doc, XPathConstants.STRING);
			Double r = (Double) xpath.evaluate("count(//resources/resource)", doc, XPathConstants.NUMBER);
			NodeList lLocation = (NodeList) xpath.evaluate("//eventLocation/text()", doc, XPathConstants.NODESET);
			NodeList lDataset = (NodeList) xpath.evaluate("//*[datasets/dataset/@id=//resources/resource/@id]//dataset", doc, XPathConstants.NODESET);
			NodeList lResource = (NodeList)xpath.evaluate("//resources/resource", doc, XPathConstants.NODESET);
			
			Propiedad query = new Propiedad("query", q.toString());
			Propiedad nResource = new Propiedad("numeroResources", r.toString());
			list.add(query);
			list.add(nResource);
			
			//elements eventlocation
			List <String> auxlLoc = new ArrayList<>();
			for(int i=0; i<lLocation.getLength(); i++) {
				Text location = (Text)lLocation.item(i);
				String l = location.getNodeValue();

				boolean add = true;
				if(!auxlLoc.contains(l)) {
					auxlLoc.add(l);
				}else
					add=false;
				Propiedad pLocation = new Propiedad("ubicaciones", l);
				if((location!=null) && add) {
					list.add(pLocation);
				}
			}
			 
			//elements id iguales
			for(int i=0; i<lDataset.getLength(); i++) {
				Integer nEquals = 0;
				Element Dataset = (Element) lDataset.item(i);
				String idDataset = Dataset.getAttribute("id");
				for(int j=0; j<lResource.getLength(); j++){
					Element resource = (Element) lResource.item(j);
					String idResource = resource.getAttribute("id");
					if(idDataset.equals(idResource)) {
						nEquals ++;
					}
				}
				Propiedad infDatasets = new Propiedad("infDatasets",idDataset.toString());
				Propiedad id = new Propiedad(idDataset.toString(),nEquals.toString());
				list.add(infDatasets);
				list.add(id);
				
			}
			System.out.println(list.toString());
		
		
		}catch(Throwable t) {
			t.printStackTrace();
		}
		return list;
		
		// Realiza las 4 consultas XPath al documento XML de entrada que se indican en el enunciado en el apartado "3.2 Búsqueda de información y generación del documento de resultados."
		// Cada consulta devolverá una información que se añadirá a la colección List <Propiedad>, que es la que devuelve este método
		// Una consulta puede devolver una propiedad o varias



		
	}
	/**
	 * Esta clase interna define una propiedad equivalente a "nombre":"valor" en JSON
	 */
	public static class Propiedad {
		public final String nombre;
		public final String valor;

		public Propiedad (String nombre, String valor){
			this.nombre=nombre;
			this.valor=valor;		
		}

		@Override
		public String toString() {
			return this.nombre+": "+this.valor;

		}

	} //Fin de la clase interna Propiedad
	


} //Fin de la clase XPATH_Evaluador
