package piat.opendatasearch;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import piat.opendatasearch.XPATH_Evaluador.Propiedad;

/**
 * @author Jennifer Ruan, X7485985Z
 *
 */

/**
 * Clase principal de la aplicación de extracción de información del 
 * Portal de Datos Abiertos del Ayuntamiento de Madrid
 *
 */
public class P5_JSON {


	public static void main(String[] args) {
		String mensaje;
		// Verificar nº de argumentos correcto
		if (args.length!=5){
			mensaje="ERROR: Argumentos incorrectos.";
			if (args.length>0)
				mensaje+=" He recibido estos argumentos: "+ Arrays.asList(args).toString()+"\n";
			mostrarUso(mensaje);
			System.exit(1);
		}		
	
		/* 
		 * Validar los argumentos recibidos en main() ->
		 * Instanciar un objeto ManejadorXML pasando como parámetro el código de la categoría recibido en el primer argumento de main()
		 * Instanciar un objeto SAXParser e invocar a su método parse() pasando como parámetro un descriptor de fichero, cuyo nombre se recibió en el primer argumento de main(), y la instancia del objeto ManejadorXML 
		 * Invocar al método getConcepts() del objeto ManejadorXML para obtener un List<String> con las uris de los elementos <concept> cuyo elemento <code> contiene el código de la categoría buscado
		 * Invocar al método getDatasets() del objeto ManejadorXML para obtener un mapa con los datasets de la categoría buscada
		 * Crear el mapa donde se almacenarán los concepts de los datasets”. Debe ser Thread Safe porque  
		 * será usado de manera concurrente por varios hilos.
 		 * Invocar al método getDatasetConcepts() de P4_JSON encargado de crear los hilos que ejecutan la clase JSONDatasetParser y metan los valores en el un mapa con los concepts de los datasets
		 * Crear el fichero de salida con el nombre recibido en el cuarto argumento de main()
		 * Volcar al fichero de salida los datos en el formato XML especificado por ResultadosBusquedaP4.xsd
		 * Validar el fichero generado con el esquema recibido en el tercer argumento de main()
		 */
		
		//Validar los argumentos recibidos en main()
		String code = args[0];	//código de la categoría de la que se desea información.
		String rutaXML = args[1];
		String rutaXSD = args[2];
		String rutaSalida = args[3];
		String rutaJSON = args[4];  //ruta al documento JSON con el resultado de las búsquedas realizadas XPath
		
		Pattern pCode = Pattern.compile("[0-9]{3,4}(-\\w{3,8})*");
		Pattern pRutaXML = Pattern.compile(".*.xml");
		Pattern pRutaXSD = Pattern.compile(".*.xsd");
		Pattern pRutaJSON = Pattern.compile(".*.json");
		
		Matcher mCode = pCode.matcher(code);
		Matcher mRutaXML = pRutaXML.matcher(rutaXML);
		Matcher mRutaSalida = pRutaXML.matcher(rutaSalida);
		Matcher mRutaXSD = pRutaXSD.matcher(rutaXSD);
		Matcher mRutaJSON = pRutaJSON.matcher(rutaJSON);
		
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
			if(!mRutaJSON.matches()) {
				mensaje = "ERROR: ruta al documento JSON error.";
				mostrarUso(mensaje);
				System.exit(1);
			}
			//Crear el fichero de salida
			File fXML= new File(rutaXML);
			File fXSD = new File(rutaXSD);
			File fSalida = new File(rutaSalida);
			File fJSON = new File(rutaJSON);
			
			fXML.setReadable(true);
			fXSD.setReadable(true);
			fSalida.setReadable(true);
			fSalida.createNewFile();
			fJSON.createNewFile();
			fSalida.setWritable(true);
			fJSON.setWritable(true);

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
			if(!fJSON.canWrite()) {
				mensaje = "ERROR: fichero" + rutaJSON + " no tiene permiso de escritura";
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
			 <concept> cuyo elemento <code> contiene el código de la categoría buscado
			 Invocar al método getDatasets() del objeto ManejadorXML para obtener un mapa con los datasets de la categoría buscad */
			List<String> listConcepts = manejadorXML.getConcepts();
			Map<String, Map<String, String>> mapDatasets = manejadorXML.getDatasets();
			
			// Crear el mapa donde se almacenarán los concepts de los datasets” con Thread Safe
			Map<String, List<Map<String,String>>> mDatasetsConcepts = getDatasetConcepts(listConcepts,mapDatasets);
			
			//generar xpath y evaluar
			List <Propiedad> lPropiedad = XPATH_Evaluador.evaluar(rutaSalida);
			GenerarJSON.generar(rutaJSON, lPropiedad);
			// Volcar al fichero de salida los datos en el formato XML especificado por ResultadosBusquedaP4.xsd  				
			try (
					PrintWriter writer = new PrintWriter(fSalida)) {
					writer.write(GenerarXML.generar(code,listConcepts,mapDatasets,mDatasetsConcepts));
				}
			//Validar el fichero generado con el esquema recibido en el tercer argumento de main()
			SchemaFactory schemaFactory=SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(fXSD);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(fSalida));
		} 
		catch (SAXException | ParserConfigurationException |IOException e) {
			e.printStackTrace(); 
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
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
	
	
	/**
	 * Devuelve un mapa donde la clave es el id del dataset y los valores son todos los graphs encontrados 
		en el json que son pertinentes
	 * Código a completar:
	 * -Obtener el nº de núcleos del procesador del ordenador
	 * -Crear un pool donde ejecutar hilos del tamaño del nº de núcleos
	 * -Crear tantos hilos como entradas haya en el mapa de Dataset, pasándoles como parámetro la clave que contiene la url del JSON a procesar por el hilo. En el comentario se
	 * 	puede poner la instrucción: Runnable trabajador = new JSONDatasetParser (dataset.getKey(),Concepts,mDatasetConcepts);
	 * Esperar a que terminen todos los hilos
	 * 
	 */
	
	private static Map<String, List<Map<String,String>>> getDatasetConcepts(List<String> lConcepts, Map<String, Map<String, String>> mDatasets){
		Map <String, List<Map<String,String>>> mapDatasetConceps = new ConcurrentHashMap<String, List<Map<String,String>>>();
		int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(cores);	//crear Thread pool 
		for(Map.Entry<String, Map<String,String>> dataset : mDatasets.entrySet()) {
			Runnable jsonParser = new JSONDatasetParser (dataset.getKey(),lConcepts, mapDatasetConceps);
			pool.execute(jsonParser);
			
		}
		//Esperar a que terminen todos los hilos
		pool.shutdown();
		try {
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
			System.out.println("\nTerminado los hilos");
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mapDatasetConceps;
		
	}
}
