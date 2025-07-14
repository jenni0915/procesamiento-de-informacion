package piat.opendatasearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import piat.opendatasearch.XPATH_Evaluador.Propiedad;

import com.google.gson.stream.JsonWriter;

/**
 * Clase usada para crear un fichero JSON
 * Contiene un método estático, llamado generar() que se encarga de generar un String en formato JSON a partir de la lista de propiedades que se le pasa como parámetro 
 */
public class GenerarJSON {

	/**
	 * Método que se encarga de generar un String en formato JSON a partir de la lista de propiedades que se le pasa como parámetro
	 * @param	ficheroJSONSalida	Nombre del fichero JSON de salida
	 * @param	listaPropiedades	Lista de propiedades
	 */
	public static void generar(String ficheroJSONSalida,List<Propiedad> listaPropiedades) {
	
		try {
			File file = new File(ficheroJSONSalida);
			OutputStream out=new FileOutputStream (file);
			JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
			int position;
			
			writer.setIndent("    ");
			writer.beginObject();
			//write query
			if((position = buscarPropiedad("query",listaPropiedades))>=0) {
				writer.name(listaPropiedades.get(position).nombre).value(listaPropiedades.get(position).valor);
			}
			//write numeroResources
			if((position = buscarPropiedad("numeroResources",listaPropiedades))>=0) {
				writer.name(listaPropiedades.get(position).nombre).value(listaPropiedades.get(position).valor);
			}
			
			//write infDatasets
			writer.name("infDatasets");
			writer.beginArray();

			for(int i = 0; i<listaPropiedades.size(); i++) {
				Propiedad id = listaPropiedades.get(i);
				if(id.nombre.equals("infDatasets")) {
					for(int j = 0; j<listaPropiedades.size(); j++) {
						Propiedad p = listaPropiedades.get(j);
						if(p.nombre.equals(id.valor)) {
							writer.beginObject();
							writer.name("id").value(p.nombre);
							writer.name("num").value(p.valor);
							writer.endObject();
						}

					}
				}
			}
			writer.endArray();

			
			
			
			
			
			if((position = buscarPropiedad("ubicaciones",listaPropiedades))>=0) {
				writer.name("ubicaciones");
				writer.beginArray();
				writer.value(listaPropiedades.get(position).valor);
				position++;
				while(position<listaPropiedades.size() && listaPropiedades.get(position).nombre.equals("ubicaciones")){
					writer.value(listaPropiedades.get(position).valor); 
                    position++;
				}
				writer.endArray();
			}
			writer.endObject();
			writer.close();		
		
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}	
	
    // Método auxiliar para buscar en el ArrayList el primer elemento que contenga el String pasado como parámetro en su nombre. Devuelve -1 si no lo encuentra
	private static int buscarPropiedad(String nombre, List<Propiedad> listaPropiedades){
		for (int i=0;i<listaPropiedades.size();i++)
			if(listaPropiedades.get(i).nombre.equals(nombre)) return i;
		return -1;
	}

}

