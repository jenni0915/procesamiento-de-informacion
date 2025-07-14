package piat.opendatasearch;


import java.util.List;
import java.util.Map;

/**
 * @author Jennifer Ruan, X7485985Z
 *
 */


/**
 * Clase estática para crear un String que contenga el documento xml a partir de la información almacenadas en las colecciones 
 *
 */	
public class GenerarXML {
	
	private static final String conceptPattern= "\n\t\t\t<concept> \"#ID#\"</concept>" ;
	private static final String dataSetPattern= "\n\t\t\t<dataset id=\"#ID#\">";
	/**  
	 * Método que deberá ser invocado desde el programa principal
	 * 
	 * @param Colecciones con la información obtenida del documento XML de entrada
	 * @return String con el documento XML de salida
	 */	
	public static String generar (String code, List <String>lConcepts,  Map <String,Map<String,String>>hDatasets){
		StringBuilder salidaXML= new StringBuilder();
		salidaXML.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); 
		salidaXML.append("<searchResults xmlns=\"http://piat.dte.upm.es/practica3\"\n"
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
				+ "xsi:schemaLocation=\"http://piat.dte.upm.es/practica3 ResultadosBusquedaP3.xsd\">" );
		salidaXML.append("\n\t<summary>");
		salidaXML.append("\n\t\t<query>" + code+ "</query>");
		salidaXML.append("\n\t\t<numConcepts>" + lConcepts.size()+ "</numConcepts>");
		salidaXML.append("\n\t\t<numDatasets>" + hDatasets.size()+ "</numDatasets>");
		salidaXML.append("\n\t</summary>");
		
		salidaXML.append("\n\t<results>");
		salidaXML.append("\n\t\t<concepts>");
		for (String unConcepto : lConcepts){
			salidaXML.append (conceptPattern.replace("#ID#", unConcepto).replace("\"", ""));
		}
		
		salidaXML.append("\n\t\t</concepts>");
		salidaXML.append("\n\t\t<datasets>");
		for(String dkey: hDatasets.keySet()) {
			salidaXML.append(dataSetPattern.replace("#ID#", dkey));
			Map<String,String> map=hDatasets.get(dkey);
			salidaXML.append("\n\t\t\t\t<title>"+ map.get("title")+ "</title>");
			salidaXML.append("\n\t\t\t\t<description>"+ map.get("description")+ "</description>");
			salidaXML.append("\n\t\t\t\t<theme>"+ map.get("theme")+ "</theme>");
			salidaXML.append("\n\t\t\t</dataset>");

		}
		salidaXML.append("\n\t\t</datasets>");
		salidaXML.append("\n\t</results>");
		salidaXML.append("\n</searchResults>");

		return salidaXML.toString();

	}

	
}
