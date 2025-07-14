package piat.opendatasearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Jennifer Ruan, X7485985Z
 *
 */

/**
 * Clase estática, que debe implementar la interfaz ParserCatalogo 
 * Hereda de DefaultHandler por lo que se deben sobrescribir sus métodos para procesar documentos XML 
 *
 */
public class ManejadorXML extends DefaultHandler implements ParserCatalogo {
	//private String sNombreCategoria;	// Nombre de la categoría
	private List<String> lConcepts; 	// Lista con los uris de los elementos <concept> que pertenecen a la categoría
	private Map <String, Map<String,String>> hDatasets;	// Mapa con información de los dataset que pertenecen a la categoría
	private String sCodigoConcepto;
	private StringBuilder contenidoElemento;
	private boolean inConcept, inCode, isCodigo, inDataset, isURL, inTitle, inDescrip, inTheme;
	private String idConcept, idDataset;
	private int nivel;
	private int n ;
	private String title, description, theme; 

	/**  
	 * @param sCodigoConcepto código de la categoría a procesar
	 * @throws SAXException, ParserConfigurationException 
	 */
	public ManejadorXML (String sCodigoConcepto) throws SAXException, ParserConfigurationException {
		this.sCodigoConcepto=sCodigoConcepto;
		lConcepts = new ArrayList<>();
		hDatasets = new HashMap<>();
		inConcept = false;
		inCode = false;
		isCodigo = false;
		isURL = false;
		inDescrip = false;
		inTheme = false;
		inTitle = false;
		idDataset = null;
		nivel = 0;
		n=-1;
		idConcept = null;
		contenidoElemento=new StringBuilder();
		title = null;
		description = null;
		theme = null;
	}

	 //===========================================================
	 // Métodos a implementar de la interfaz ParserCatalogo
	 //===========================================================

	/**
	 * <code><b>getConcepts</b></code>
	 *	Devuelve una lista con información de los <code><b>concepts</b></code> resultantes de la búsqueda. 
	 * <br> Cada uno de los elementos de la lista contiene la <code><em>URI</em></code> del <code>concept</code>
	 * 
	 * <br>Se considerarán pertinentes el <code><b>concept</b></code> cuyo código
	 *  sea igual al criterio de búsqueda y todos sus <code>concept</code> descendientes.
	 *  
	 * @return
	 * - List  con la <em>URI</em> de los concepts pertinentes.
	 * <br>
	 * - null  si no hay concepts pertinentes.
	 * 
	 */
	@Override	
	public List<String> getConcepts() {
		return lConcepts;
	}

	/**
	 * <code><b>getDatasets</b></code>
	 * 
	 * @return Mapa con información de los <code>dataset</code> resultantes de la búsqueda.
	 * <br> Si no se ha realizado ninguna  búsqueda o no hay dataset pertinentes devolverá el valor <code>null</code>
	 * <br> Estructura de cada elemento del map:
	 * 		<br> . <b>key</b>: valor del atributo ID del elemento <code>dataset</code>con la cadena de la <code><em>URI</em></code>  
	 * 		<br> . <b>value</b>: Mapa con la información a extraer del <code>dataset</code>. Cada <code>key</code> tomará los valores <em>title</em>, <em>description</em> o <em>theme</em>, y <code>value</code> sus correspondientes valores.

	 * @return
	 *  - Map con información de los <code>dataset</code> resultantes de la búsqueda.
	 *  <br>
	 *  - null si no hay datasets pertinentes.  
	 */	
	@Override
	public Map<String, Map<String, String>> getDatasets() {
	
		return hDatasets;
	}
	

	 //===========================================================
	 // Métodos a implementar de SAX DocumentHandler
	 //===========================================================
	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		System.out.println("Comienza el documento.");
		
	}

	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		System.out.println("Finaliza el documento\n");
			
	}


	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		contenidoElemento.delete(0, contenidoElemento.length());
		//guardar idConcept temporal
		if(localName.equalsIgnoreCase("concept")){
			inConcept = true;
			nivel++;
			idConcept = attributes.getValue("id").trim();
		}
		//buscar idConcept de dataset en la lista
		if(localName.equalsIgnoreCase("concept") && inDataset) {
			for(int j= 0; j<attributes.getLength(); j++) {
				for(int i = 0; i<lConcepts.size(); i++) {
					if(idConcept.equals(lConcepts.get(i))) {
						isURL = true;
						idConcept = attributes.getValue(j);
					}
				}		
			}
		}
		if(localName.equalsIgnoreCase("code"))
			inCode = true;
		
		if(localName.equalsIgnoreCase("dataset")) {
			inDataset = true;
			idDataset = attributes.getValue("id").trim();
		}
		if(localName.equalsIgnoreCase("title"))
			inTitle = true;
		
		if(localName.equalsIgnoreCase("description"))
			inDescrip = true;
		
		if(localName.equalsIgnoreCase("theme"))
			inTheme = true;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		// TODO 
		//String title = null, description = null, theme = null; 
		if(localName.equalsIgnoreCase("code")) {
			inCode = false;
			if(inConcept && isCodigo) {
				lConcepts.add(idConcept);

			}
		}
		
		if(localName.equalsIgnoreCase("concept")) {
			nivel--;
			//si sale concept de concepts 
			
		}
		if(localName.equalsIgnoreCase("concepts")) {
			//si sale concept de concepts 
			if((isCodigo && n == 0)) {
				inConcept = false;
				isCodigo = false;
			}else if(isCodigo && nivel==n) {
				inConcept = false;
				isCodigo = false;
			}
		}
		
		if(localName.equalsIgnoreCase("title") && inDataset ) {
			inTitle = false;
			title=contenidoElemento.toString();
		}
		if(localName.equalsIgnoreCase("description") && inDataset ){
			inDescrip = false;
			description = contenidoElemento.toString();
		}
		if(localName.equalsIgnoreCase("theme") && inDataset ) {
			inTheme = false;
			theme = contenidoElemento.toString();
		}
		
		if(localName.equalsIgnoreCase("dataSet")) {
			inConcept=false;
			inDataset = false;
			Map<String, String> map = new HashMap<String,String>();
			//si ha encontrado idDataset poner en la mapa
			if(isURL) {
				isURL = false;
				map.put("title", title);
				map.put("description", description);
				map.put("theme", theme);
				hDatasets.put(idDataset, map);
			}

		}
		contenidoElemento.delete(0, contenidoElemento.length());	
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		//comprobar el codigo con sCodigoConcepto
		if(inCode) {
			String cad = new String(ch, start, length);
			if(cad.equalsIgnoreCase(sCodigoConcepto)) {
				isCodigo = true;
				n=nivel;
			}
		}
		//almacenar datos temporalmente
		if(inTitle || inDescrip || inTheme) {
			String cad = new String(ch, start, length);
			contenidoElemento.append(cad);
		}
	}
		

}
