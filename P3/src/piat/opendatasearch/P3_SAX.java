package piat.opendatasearch;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * @author Ponga aquí su nombre, apellidos y DNI
 *
 */

/**
 * Clase principal de la aplicación de extracción de información del 
 * Portal de Datos Abiertos del Ayuntamiento de Madrid
 *
 */
public class P3_SAX {


	public static void main(String[] args) {
		String mensaje;
		// Verificar nº de argumentos correcto
		if (args.length!=4){
			mensaje="ERROR: Argumentos incorrectos.";
			if (args.length>0)
				mensaje+=" He recibido estos argumentos: "+ Arrays.asList(args).toString()+"\n";
			mostrarUso(mensaje);
			System.exit(1);
		}		
		
		// TODO
		/* 
		 * Validar los argumentos recibidos en main() ->
		 * Instanciar un objeto ManejadorXML pasando como parámetro el código de la categoría recibido en el primer argumento de main()
		 * Instanciar un objeto SAXParser e invocar a su método parse() pasando como parámetro un descriptor de fichero, cuyo nombre se recibió en el primer argumento de main(), y la instancia del objeto ManejadorXML 
		 * Invocar al método getConcepts() del objeto ManejadorXML para obtener un List<String> con las uris de los elementos <concept> cuyo elemento <code> contiene el código de la categoría buscado
		 * Invocar al método getDatasets() del objeto ManejadorXML para obtener un mapa con los datasets de la categoría buscada
		 * Crear el fichero de salida con el nombre recibido en el cuarto argumento de main()
		 * Volcar al fichero de salida los datos en el formato XML especificado por ResultadosBusquedaP3.xsd
		 * Validar el fichero generado con el esquema recibido en el tercer argumento de main()
		 */
		
		//Validar los argumentos recibidos en main()
		String code = args[0];
		String rutaXML = args[1];
		String rutaXSD = args[2];
		String rutaSalida = args[3];
		Pattern pCode = Pattern.compile("[0-9]{3,4}(-*\\w{3,8})*");
		Pattern pRutaXML = Pattern.compile(".*.xml");
		Pattern pRutaXSD = Pattern.compile(".*.xsd");
		Matcher mCode = pCode.matcher(code);
		Matcher mRutaXML = pRutaXML.matcher(rutaXML);
		Matcher mRutaSalida = pRutaXML.matcher(rutaSalida);
		Matcher mRutaXSD = pRutaXSD.matcher(rutaXSD);
		try {
		if(! mCode.matches()) {
			mensaje = "ERROR: Código de la categoría incorrecto.";
			mostrarUso(mensaje);
			System.exit(1);
		}
		if(!mRutaXML.matches() || !mRutaSalida.matches()) {
			mensaje = "ERROR: ruta al documento error.";
			mostrarUso(mensaje);
			System.exit(1);
		}
		if(!mRutaXSD.matches()) {
			mensaje = "ERROR: ruta al documento esquema error.";
			mostrarUso(mensaje);
			System.exit(1);
		}
		File fXML= new File(rutaXML);
		File fXSD = new File(rutaXSD);
		File fSalida = new File(rutaSalida);
		
		fXML.setReadable(true);
		fXSD.setReadable(true);
		fSalida.setReadable(true);
		
		if(!fXML.exists()) {
			mensaje = "ERROR: fichero" + rutaXML + " no encuentra";
			mostrarUso(mensaje);
			System.exit(1);
		}
		if(!fXSD.exists()) {
			
				mensaje = "ERROR: fichero" + rutaXSD + " no encuentra";
				mostrarUso(mensaje);
				System.exit(1);
			
		}
		if(!fSalida.canWrite()) {
			mensaje = "ERROR: fichero" + rutaSalida + " no tiene permiso de escritura";
			mostrarUso(mensaje);
			System.exit(1);
			
		}
		
		/*Instanciar un objeto ManejadorXML pasando como parámetro el código de la categoría recibido en el primer argumento de main()
		Instanciar un objeto SAXParser e invocar a su método parse() pasando como parámetro un descriptor de fichero, cuyo 
		nombre se recibió en el primer argumento de main(), y la instancia del objeto ManejadorXML */
		
			ManejadorXML manejadorXML = new ManejadorXML(code);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse( rutaXML, manejadorXML);
			/* Invocar al método getConcepts() del objeto ManejadorXML para obtener un List<String> con las uris de los elementos 
			 * <concept> cuyo elemento <code> contiene el código de la categoría buscado
			 * Invocar al método getDatasets() del objeto ManejadorXML para obtener un mapa con los datasets de la categoría buscada
			 */
			List<String> listConcepts = manejadorXML.getConcepts();
			Map<String, Map<String, String>> mapDatasets = manejadorXML.getDatasets();
			
			try (// Volcar al fichero de salida los datos en el formato XML especificado por ResultadosBusquedaP3.xsd  	
			PrintWriter writer = new PrintWriter(fSalida)) {
				writer.write(GenerarXML.generar(code,listConcepts,mapDatasets));
			}
			//Validar el fichero generado con el esquema recibido en el tercer argumento de main()
			SchemaFactory schemaFactory=SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(fXSD);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(fSalida));
		} 
		catch (SAXException | ParserConfigurationException |IOException e) {
			e.printStackTrace(); 
		}
		
		
		System.exit(0);
	}
	
	
	/**
	 * Muestra mensaje de los argumentos esperados por la aplicación.
	 * Deberá invocase en la fase de validación ante la detección de algún fallo
	 *
	 * @param mensaje  Mensaje adicional informativo (null si no se desea)
	 */
	private static void mostrarUso(String mensaje){
		Class<? extends Object> thisClass = new Object(){}.getClass();
		
		if (mensaje != null)
			System.err.println(mensaje+"\n");
		System.err.println(
				"Uso: " + thisClass.getEnclosingClass().getCanonicalName() + " <códigoCategoría> <ficheroCatalogo> <ficheroXSDsalida> <ficheroXMLSalida>\n" +
				"donde:\n"+
				"\t códigoCategoría:\t código de la categoría de la que se desea obtener datos\n" +
				"\t ficheroCatalogo:\t path al fichero XML con el catálogo de datos\n" +
				"\t ficheroXSDsalida:\t nombre del fichero que contiene el esquema contra el que se tiene que validar el documento XML de salida\n"	+
				"\t ficheroXMLSalida:\t nombre del fichero XML de salida\n"
				);				
	}		

}
