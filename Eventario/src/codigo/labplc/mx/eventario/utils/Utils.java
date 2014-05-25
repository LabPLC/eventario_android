package codigo.labplc.mx.eventario.utils;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import codigo.labplc.mx.eventario.bean.beanEventos;

public class Utils {
	public static boolean isNetworkConnectionOk(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}

	/**
	 * metodo que hace la conexion al servidor con una url especifica
	 * 
	 * @param url
	 *            (String) ruta del web service
	 * @return (String) resultado del service
	 */
	public static String doHttpConnection(String url) {
		HttpClient Client = new DefaultHttpClient();
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
			HttpGet httpget = new HttpGet(url);
			HttpResponse hhrpResponse = Client.execute(httpget);
			HttpEntity httpentiti = hhrpResponse.getEntity();
			// Log.d("RETURN HTTPCLIENT", EntityUtils.toString(httpentiti));
			return EntityUtils.toString(httpentiti);
		} catch (ParseException e) {
			e.getStackTrace();
			return null;
		} catch (IOException e) {
			e.getStackTrace();
			return null;
		}
	}
	
/**
 * llena un arreglo de eventos dado por un json
 * 
 * @param lat (String) latitud de la ubicacion
 * @param lon (String) longitud de la ubicacion
 * @param radio (String) radio de busqueda default: 2km
 * 
 * @return (BeanEventos) beasn que contiene todos los datos de la busqueda
 */
	public  static beanEventos llenarEventos(String lat,String lon,String radio){

			try{
				  String Sjson=  Utils.doHttpConnection("http://dev.codigo.labplc.mx/EventarioWeb/eventos.json?lat="+lat+"&lon="+lon+"&dist="+radio);

				  JSONArray jsonarray = new JSONArray(Sjson);
				  	beanEventos bean = new    beanEventos();
				
				  	String[] nombre = new String[jsonarray.length()];
				  	String[] lugar = new String[jsonarray.length()];;
				  	String[] hora_inicio = new String[jsonarray.length()];
				  	String[] hora_fin = new String[jsonarray.length()];
				  	String[] imagen = new String[jsonarray.length()];
				  	String[] descripcion = new String[jsonarray.length()];
				  	String[] precio = new String[jsonarray.length()];
				  	String[] direccion = new String[jsonarray.length()];
				  	String[] fuente = new String[jsonarray.length()];
				  	String[] fecha_inicio = new String[jsonarray.length()];
				  	String[] fecha_fin = new String[jsonarray.length()];
				  	String[] categoria = new String[jsonarray.length()];
				  	String[] contacto = new String[jsonarray.length()];
				  	String[] pagina = new String[jsonarray.length()];
				  	String[] latitud = new String[jsonarray.length()];
				  	String[] longitud = new String[jsonarray.length()];
				  	String[] distancia = new String[jsonarray.length()];
				  	String[] url = new String[jsonarray.length()];
					String[] id = new String[jsonarray.length()];
					
				  for (int i = 0; i < jsonarray.length(); i++) {
					  	JSONObject oneObject = jsonarray.getJSONObject(i);
							nombre[i]=((String) oneObject.getString("nombre"));
							lugar[i]=((String) oneObject.getString("lugar"));
							hora_inicio[i]=((String) oneObject.getString("hora_inicio"));
							hora_fin[i]=((String) oneObject.getString("hora_fin"));
							imagen[i]=((String) oneObject.getString("imagen"));
							descripcion[i]=((String) oneObject.getString("descripcion"));
							precio[i]=((String) oneObject.getString("precio"));
							direccion[i]=((String) oneObject.getString("direccion"));
							fuente[i]=((String) oneObject.getString("fuente"));
							fecha_inicio[i]=((String) oneObject.getString("fecha_inicio"));
							fecha_fin[i]=((String) oneObject.getString("fecha_fin"));
							categoria[i]=((String) oneObject.getString("categoria"));
							contacto[i]=((String) oneObject.getString("contacto"));
							pagina[i]=((String) oneObject.getString("pagina"));
							latitud[i]=((String) oneObject.getString("latitud"));
							longitud[i]=((String) oneObject.getString("longitud"));
							distancia[i]=((String) oneObject.getString("distancia"));
							url[i]=((String) oneObject.getString("url"));
		
				      }
				  
					bean.setNombre(nombre);
					bean.setLugar(lugar);
					bean.setHora_inicio(hora_inicio);
					bean.setHora_fin(hora_fin);
					bean.setImagen(imagen);
					bean.setDescripcion(descripcion);
					bean.setPrecio(precio);
					bean.setDireccion(direccion);
					bean.setFuente(fuente);
					bean.setFecha_inicio(fecha_inicio);
					bean.setFecha_fin(fecha_fin);
					bean.setCategoria(categoria);
					bean.setContacto(contacto);
					bean.setPagina(pagina);
					bean.setLatitud(latitud);
					bean.setLongitud(longitud);
					bean.setUrl(url);
					bean.setDistancia(distancia);
					
				  return bean;
				}catch(JSONException e){
					e.getStackTrace();
					return null;
				}
		}
	
	
	
	
}
