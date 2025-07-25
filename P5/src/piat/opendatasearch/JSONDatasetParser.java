package piat.opendatasearch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.stream.JsonReader;


/* En esta clase se comportará como un hilo */

public class JSONDatasetParser implements Runnable {
	private String fichero;
	private List<String> lConcepts;
	private Map<String, List<Map<String,String>>> mDatasetConcepts;
	private String nombreHilo;
	
	public JSONDatasetParser (String fichero, List<String> lConcepts, Map<String, List<Map<String,String>>> mDatasetConcepts) { 
		this.fichero=fichero;
		this.lConcepts=lConcepts;
		this.mDatasetConcepts=mDatasetConcepts;
	}

	
	@Override
	public void run (){
		List<Map<String,String>> graphs=new ArrayList<Map<String,String>>();	// Aquí se almacenarán todos los graphs de un dataset cuyo objeto de nombre @type se corresponda con uno de los valores pasados en el la lista lConcepts
		boolean finProcesar=false;	// Para detener el parser si se han agregado a la lista graphs 5 graph
	
		Thread.currentThread().setName("JSON " + fichero);
		nombreHilo="["+Thread.currentThread().getName()+"] ";
	    System.out.println(nombreHilo+"Empezar a descargar de internet el JSON");
	    try {
	    	InputStreamReader inputStream = new InputStreamReader(new URL(fichero).openStream(), "UTF-8"); 
			//TODO:
			//	- Crear objeto JsonReader a partir de inputStream
			//  - Consumir el primer "{" del fichero
			//  - Procesar los elementos del fichero JSON, hasta el final de fichero o hasta que finProcesar=true
			//		Si se encuentra el objeto @graph, invocar a procesar_graph()
			//		Descartar el resto de objetos
			//	- Si se ha llegado al fin del fichero, consumir el último "}" del fichero
			//  - Cerrar el objeto JsonReader
	    	JsonReader jsonR = new JsonReader (inputStream);
	    	jsonR.beginObject();;
	    	
	    	while(jsonR.hasNext() ) {
	    		String t = jsonR.nextName();
	    		if("@graph".equals(t)  ) {
	    			finProcesar = procesar_graph(jsonR,graphs,lConcepts);
	    		}else {
	    			jsonR.skipValue();
	    		}
	    		
	    	}
	    	
	    	jsonR.endObject();
	    	jsonR.close();
			inputStream.close();
			
		} catch (FileNotFoundException e) {
			System.out.println(nombreHilo+"El fichero no existe. Ignorándolo");
		} catch (IOException e) {
			System.out.println(nombreHilo+"Hubo un problema al abrir el fichero. Ignorándolo" + e);
		}
	    mDatasetConcepts.put(fichero, graphs); 	// Se añaden al Mapa de concepts de los Datasets
	    
	}

	/* 	procesar_graph()
	 * 	Procesa el array @graph
	 *  Devuelve true si ya se han añadido 5 objetos a la lista graphs
	 */
	private boolean procesar_graph(JsonReader jsonReader, List<Map<String, String>> graphs, List<String> lConcepts) throws IOException {
		boolean finProcesar=false;
		// TODO:
		//	- Consumir el primer "[" del array @graph
		//  - Procesar todos los objetos del array, hasta el final de fichero o hasta que finProcesar=true
		//  	- Consumir el primer "{" del objeto
		//  	- Procesar un objeto del array invocando al método procesar_un_graph()
		//  	- Consumir el último "}" del objeto
		// 		- Ver si se han añadido 5 graph a la lista, para en ese caso poner la variable finProcesar a true
		//	- Si se ha llegado al fin del array, consumir el último "]" del array
    	jsonReader.beginArray();
		while(jsonReader.hasNext()) {
			if(graphs.size()<5) {
				jsonReader.beginObject();
				procesar_un_graph(jsonReader, graphs, lConcepts);
				jsonReader.endObject();
			}else {
				finProcesar = true;
				jsonReader.skipValue();

			}
		}
	
		System.out.println("Fin de procesar "+Thread.currentThread().getName());
    	jsonReader.endArray();

	    return finProcesar;
		
	}

	/*	procesar_un_graph()
	 * 	Procesa un objeto del array @graph y lo añade a la lista graphs si en el objeto de nombre @type hay un valor que se corresponde con uno de la lista lConcepts
	 */
	
	private void procesar_un_graph(JsonReader jsonReader, List<Map<String, String>> graphs, List<String> lConcepts) throws IOException {
		// TODO:
		//	- Procesar todas las propiedades de un objeto del array @graph, guardándolas en variables temporales
		//	- Una vez procesadas todas las propiedades, ver si la clave @type tiene un valor igual a alguno de los concept de la lista lConcepts. Si es así
		//	  guardar en un mapa Map<String,String> todos los valores de las variables temporales recogidas en el paso anterior y añadir este mapa al mapa graphs
		String link = null, title = null, evenLocation = null, area = null, locality = null, street = null, start = null, end = null, description = null;
		String atype = null;
		Double latitude = null, longitude = null;
		Map <String,String> map = new HashMap<>();
		while(jsonReader.hasNext()) {
			String t = jsonReader.nextName();
			if("@type".equals(t)) {
				atype = jsonReader.nextString();
			}else if("title".equals(t)) {
				title = jsonReader.nextString();
			}else if("description".equals(t)) {
				description = jsonReader.nextString();
			}else if("dtstart".equals(t)) {
				start = jsonReader.nextString();
			}else if("dtend".equals(t)) {
				end = jsonReader.nextString();
			}else if("link".equals(t)) {
				link = jsonReader.nextString();
			}else if("event-location".equals(t)) {
				evenLocation = jsonReader.nextString();
			}else if("address".equals(t)) {
				jsonReader.beginObject();
				while(jsonReader.hasNext()) {
					String y = jsonReader.nextName();
					if("area".equals(y)) {
						jsonReader.beginObject();
						while(jsonReader.hasNext()) {
							String a = jsonReader.nextName();
							if("locality".equals(a)) {
								locality = jsonReader.nextString();
							}else if("street-address".equals(a)) {
								street = jsonReader.nextString();
							}else if("@id".equals(a)) {
								area = jsonReader.nextString();
							}
							else {
								jsonReader.skipValue();
							}
						}
						jsonReader.endObject();
					}else {
						jsonReader.skipValue();
					}
				}
				jsonReader.endObject();

			}else if("location".equals(t)) {
				jsonReader.beginObject();
				while(jsonReader.hasNext()) {
					String a = jsonReader.nextName();
					if("latitude".equals(a)) {
						latitude = jsonReader.nextDouble();
					}else if("longitude".equals(a)) {
						longitude = jsonReader.nextDouble();
					}
				}
				jsonReader.endObject();

			}else {
				jsonReader.skipValue();
			}
		}
		Integer a = 0;
		if(atype != null) {
			for(String s: lConcepts) {
				if(atype.equals(s)) {
					map.put("id",atype);
					map.put("link",link);
					map.put("title",title);
					map.put("EventLocation",evenLocation);
					map.put("area",area);
					map.put("locality",locality);
					map.put("street",street);
					map.put("start",start);
					map.put("end",end);
					if(latitude!=null) {
						map.put("latitude",latitude.toString());
					}else 
						map.put("latitude",a.toString());
					if(longitude!=null) {
						map.put("longitude",longitude.toString());
					}else
						map.put("longitude",a.toString());

					map.put("description",description);
					//System.out.println(map.toString());
					graphs.add(map);
				}
			}
		}
		
	}

	
	
}
