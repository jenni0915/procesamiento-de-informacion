package piat.opendatasearch;

import java.util.Map;
import java.io.InputStream;
import java.util.Collection;

import javax.xml.parsers.SAXParser;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;

/**
 * @author Ponga aquí su nombre y apellidos y su correo UPM completo
 */

/**
 * Clase que implementa el analizador XML usando un SAXParser.
 * Extiende la clase DefaultHandler para que pueda ser usada por un SAXParser,
 * por lo que tiene que sobreescribir sus métodos públicos.
 * Implementa la interfaz ParserCatálogo para que pueda ser usada por la
 * aplicación para obtener la información de concepts y datasets pertinentes.
 */
public final class AnalizadorXML
extends DefaultHandler
implements ParserCatálogo
{
	// Analizador SAX que se usará para analizar el catálogo
	private final SAXParser saxParser;
	// Categoría que se usará para seleccionar la información pertinente
	private final String cat;
	// Stream para leer el catálogo
	private final InputStream doc;
	// Colección en la que anotar los concepts pertinentes a devolver
	private final Collection<String> concepts;
	// Mapa en el que registrar los datasets pertinentes a devolver
	private final Map<String,Map<String,String>> datasets;

	// TODO: añadir los campos que se necesiten

	/**
	 * Constructor de la clase.
	 * @param cat la categoría que se usará para seleccionar la
	 * información pertinente.
	 * @param doc el documento XML que contiene el catálogo a analizar.
	 */
	public AnalizadorXML ( String cat, InputStream doc )
	throws ParserConfigurationException, SAXException
	{
		// TODO: codificar el constructor
	}

	/**
	 * Analiza el contenido del catálogo y obtiene la información
	 * pertinente. Debe ser invocado una sola vez.
	 */
	public void analizar ()
	throws SAXException, IOException
	{
		// TODO: código adicional si se considera necesario
		saxParser.parse ( doc, this );
	}

	//===========================================================
	// Métodos a implementar de la interfaz ParserCatálogo
	//===========================================================

	@Override
	public Collection<String> getConcepts ()
	{
		// TODO: código adicional si se considera necesario
		return concepts;
	}

	@Override
	public Map<String,Map<String,String>> getDatasets ()
	{
		// TODO: código adicional si se considera necesario
		return datasets;
	}

	//===========================================================
	// Métodos a implementar de SAX DocumentHandler
	//===========================================================

	@Override
	public final void startDocument() throws SAXException
	{
		super.startDocument();
		// TODO: código adicional si se considera necesario
	}
	
	@Override
	public final void endDocument() throws SAXException
	{
		super.endDocument();
		// TODO: código adicional si se considera necesario
	}

	@Override
	public final void startElement (
		String nsURI,
		String localName,
		String qName,
		Attributes atts )
		throws SAXException
	{
		super.startElement ( nsURI, localName, qName, atts );
		// TODO: código adicional si se considera necesario
	}

	@Override
	public final void endElement (
		String uri,
		String localName,
		String qName )
		throws SAXException
	{
		super.endElement ( uri, localName, qName );
		// TODO: código adicional si se considera necesario
	}

	@Override
	public final void characters (
		char chars[],
		int start,
		int length )
		throws SAXException
	{
		super.characters ( chars, start, length );
		// TODO: código adicional si se considera necesario
	}
}
