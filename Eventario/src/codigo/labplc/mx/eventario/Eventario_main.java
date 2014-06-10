package codigo.labplc.mx.eventario;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;
import codigo.labplc.mx.eventario.bean.InfoPointBean;
import codigo.labplc.mx.eventario.bean.beanEventos;
import codigo.labplc.mx.eventario.configuracion.Configuracion_activity;
import codigo.labplc.mx.eventario.customs.CustomList;
import codigo.labplc.mx.eventario.detalles.Detalle_evento_Activity;
import codigo.labplc.mx.eventario.dialogos.Dialogos;
import codigo.labplc.mx.eventario.servicio.ServicioGeolocalizacion;
import codigo.labplc.mx.eventario.utils.Utils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


@SuppressWarnings("deprecation")
public class Eventario_main extends Activity {

	public final String TAG = this.getClass().getSimpleName();

	
	private GoogleMap map;
	public static double lat=19.0;
	public static double lon=-99.0;
	private MarkerOptions marker;
	private ProgressDialog pDialog;
	private int isLocalizado = 0;
    private ListView list;
	private beanEventos bean;
	private String radio="2";
	private String progreso;
	private String id_ubicacion;
	private String[] id_markers;
	private boolean pause=false;
	private long lastTouched = 0;
	private static final long SCROLL_TIME = 200L;
	 EditText eventario_main_et_direccion ;
	 ArrayList<InfoPointBean> InfoPoint;
	
	private LocationManager mLocationManager_eventos;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eventario_main);
		mLocationManager_eventos = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!mLocationManager_eventos.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			new Dialogos().showDialogGPS(Eventario_main.this).show();		
		}else{
			init();
		}
			
	}
	
	public void init(){
		ServicioGeolocalizacion.taxiActivity = Eventario_main.this;
		startService(new Intent(Eventario_main.this,ServicioGeolocalizacion.class));

		 SharedPreferences prefs = getSharedPreferences("MisPreferenciasEventario",Context.MODE_PRIVATE);
		 progreso = prefs.getString("progreso", null);
		 
		 if(progreso!=null){
			 radio=progreso;
		 }
		
	     if(lat==19.0){
	    	anillo();
	     }
	    	
	     
	      eventario_main_et_direccion = (EditText)findViewById(R.id.eventario_main_et_direccion);
	     
		final ImageView handle= (ImageView)findViewById(R.id.handle);
		SlidingDrawer drawer = (SlidingDrawer)findViewById(R.id.drawer);
		drawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			
			@Override
			public void onDrawerOpened() {
				handle.setImageResource(R.drawable.ic_launcher_flechas);
				
			}
		});
		drawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			
			@Override
			public void onDrawerClosed() {
				handle.setImageResource(R.drawable.ic_launcher_mas);
				
			}
		});
		
		
		
		ImageView eventario_main_iv_gps =(ImageView)findViewById(R.id.eventario_main_iv_gps);
		eventario_main_iv_gps.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			//	cargarMapa(lat,lon);
				
				CameraPosition cameraPosition;
				cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lon)).zoom(map.getCameraPosition().zoom).build();
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				cargarMapa(lat,lon);
				
			}
		});
		
		
		ImageView eventario_main_iv_config =(ImageView)findViewById(R.id.eventario_main_iv_config);
		eventario_main_iv_config.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Eventario_main.this,Configuracion_activity.class);
				startActivity(intent);
				finish();
				
			}
		});
		
		ImageView eventario_main_iv_lupa =(ImageView)findViewById(R.id.eventario_main_iv_lupa);
		eventario_main_iv_lupa.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String direccion_busqueda=   eventario_main_et_direccion.getText().toString(); 
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	        	imm.hideSoftInputFromWindow(eventario_main_et_direccion.getWindowToken(), 0);
	        	  
				if(!direccion_busqueda.equals("")){
					InfoPoint = null;
					InfoPoint =Utils.busquedaDireccion(direccion_busqueda);
					if(InfoPoint!=null){
						
						CameraPosition cameraPosition;
						cameraPosition = new CameraPosition.Builder().target(new LatLng(InfoPoint.get(0).getDblLatitude(), InfoPoint.get(0).getDblLongitude())).zoom(map.getCameraPosition().zoom).build();
						map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
						cargarMapa(InfoPoint.get(0).getDblLatitude(), InfoPoint.get(0).getDblLongitude());
					}
				}
				
			}
		});
		
		
		
		setUpMapIfNeeded();
		
	}
	
	public boolean cargarEventos(){
		try{
			        CustomList adapter = new CustomList(Eventario_main.this, bean.getNombre(), bean.getHora_inicio(),bean.getHora_fin(),bean.getDistancia(),bean.getImagen());
			        list=(ListView)findViewById(R.id.list);
			        list.setAdapter(adapter);
			        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			            @Override
			            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			       
			            	abrirDetalles(bean.getId_marker()[position]);
			            }
			        });
			    
			return true;
		}catch(Exception e){
			e.printStackTrace();
		return false;
		}
		
	}
	
	public void anillo(){
		pDialog = new ProgressDialog(Eventario_main.this);
 		pDialog.setCanceledOnTouchOutside(false);
 		pDialog.setMessage(getResources().getString(R.string.mapa_texto_significado_el_viaje_inicio));
 		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
 		pDialog.setCancelable(false);
 		pDialog.show();
	}
	
	
	
	/**
	 * inicializa el mapa y muestra la ubicacion inicial
	 */
	private void setUpMapIfNeeded() {
		if (map == null) {
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapa)).getMap();
			if (map != null) {
				if(setUpMap()) {
					initMap();
				}
			}
		}
	}
	
	/**
	 * Revisa que el mapa esta listo
	 */
	public boolean setUpMap() {
		if (!checkReady()) {
            return false;
        } else {
        	return true;
        }
	}
    
	/**
	 * revisa que el mapa no sea nulo
	 */
	private boolean checkReady() {
        if (map == null) {
            return false;
        }
        return true;
    }

	/**
	 * inicializa el mapa y coloca todos sus atributos
	 */
	public void initMap() {
		map.setMyLocationEnabled(false);//quitar circulo azul;
		map.setBuildingsEnabled(true);
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.getUiSettings().setZoomControlsEnabled(true); //ZOOM
		map.getUiSettings().setCompassEnabled(true); //COMPASS
		map.getUiSettings().setZoomGesturesEnabled(true); //GESTURES ZOOM
		map.getUiSettings().setRotateGesturesEnabled(true); //ROTATE GESTURES
		map.getUiSettings().setScrollGesturesEnabled(true); //SCROLL GESTURES
		map.getUiSettings().setTiltGesturesEnabled(true); //TILT GESTURES
		map.getUiSettings().setZoomControlsEnabled(false);
		
		// create marker
		marker = new MarkerOptions();
		marker.position(new LatLng(lat, lon));
		marker.title(getResources().getString(R.string.mapa_inicio_de_viaje));
		marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_chinche_llena));
		
		CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lon)).zoom(21).build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		// adding marker
		Marker m = map.addMarker(marker);	
		id_ubicacion=m.getId();
	}
	
	
	/**
	 * manejo de transmiciones
	 */
	private BroadcastReceiver onBroadcast = new BroadcastReceiver() {

		@SuppressLint("SimpleDateFormat")
		@Override
		public void onReceive(Context ctxt, Intent t) {
			
			lat = t.getDoubleExtra("latitud", 19.0f);
			lon = t.getDoubleExtra("longitud",-99.0f);
			
			if(isLocalizado==0){
				cargarMapa(lat,lon);
			}
			
			CameraPosition cameraPosition;
			cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lon)).zoom(14).build();
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));			
			
			map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(Marker marker) {
					if(!marker.getId().toString().equals(id_ubicacion)){
						abrirDetalles(marker.getId().toString());
					}
					
				}

				
			});
	
			
			map.setInfoWindowAdapter(new InfoWindowAdapter() {
	            @Override
	            public View getInfoWindow(Marker marker) {              
	                return null;
	            }           
	            @Override
	            public View getInfoContents(Marker marker) {
	            	if(!marker.getId().toString().equals(id_ubicacion)){
			            	View v = getLayoutInflater().inflate(R.layout.windowlayout, null);
			                String s[] = marker.getTitle().split("@@");
			                TextView   pupop_nombre = (TextView) v.findViewById(R.id.pupop_nombre);
			                pupop_nombre.setText(s[0] );
			                TextView pupop_lugar = (TextView) v.findViewById(R.id.pupop_lugar);
			                pupop_lugar.setText(s[1]);
			                return v;
		            	
	            	}else{
	            		View v = getLayoutInflater().inflate(R.layout.windowlayout_simple, null);
						 TextView   pupop_nombre = (TextView) v.findViewById(R.id.pupop_simple_nombre);
			              pupop_nombre.setText(getResources().getString(R.string.mapa_inicio_de_viaje));
						 return v;
	            	}
	            	 
	                
	              

	            }
	        });
			if(isLocalizado>=1){
		 		if(pDialog!=null){
			 		pDialog.dismiss();
			 	}
		 		stopService(new Intent(Eventario_main.this, ServicioGeolocalizacion.class));
		 	}else{
		 		isLocalizado+=1;
		 	}
			
		}
	};
	
	
	@SuppressLint("SimpleDateFormat")
	public void cargarMapa(double lat_, double lon_){
		
		map.clear();
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String horaInicio = sdf.format(c.getTime());
		if(Utils.isNetworkConnectionOk(getApplicationContext())){
			bean = Utils.llenarEventos(lat_+"",lon_+"",radio,horaInicio);
		}else{
			Toast.makeText(getApplicationContext(), "No tienes Internet", Toast.LENGTH_SHORT).show();
		}
		if(bean!=null){
			cargarEventos();	
		}else{
			Toast.makeText(getApplicationContext(), "No hay eventos cerca de ti", Toast.LENGTH_SHORT).show();
		}
		marker.position(new LatLng(lat_,lon_));
		Marker m=map.addMarker(marker);
		id_ubicacion=m.getId();
	   	
	   if(bean!=null){
		id_markers = new String[bean.getLatitud().length];

		for(int i=0;i<bean.getLatitud().length;i++){
			MarkerOptions markerte= new MarkerOptions();
			markerte.position(new LatLng(Double.parseDouble(bean.getLatitud()[i]), Double.parseDouble(bean.getLongitud()[i])));
			markerte.title(bean.getNombre()[i]+"@@"+bean.getLugar()[i]);
			markerte.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_pin));
			Marker ma =map.addMarker(markerte);
			id_markers[i] = ma.getId();
		}
	   	bean.setId_marker(id_markers);
	   }
	  
	   if(pDialog!=null){
		   pDialog.dismiss();
	 	}
		
	}
	
		@Override
		protected void onDestroy() {
		if(pDialog!=null){
	    	pDialog.dismiss();
	    }
		
			isLocalizado=0;
			super.onDestroy();
		}

		@Override
		protected void onPause() {
			pause= true;
			try{
				unregisterReceiver(onBroadcast);
				if(pDialog!=null){
			 		pDialog.dismiss();	
			 	}
				
		 		stopService(new Intent(Eventario_main.this, ServicioGeolocalizacion.class));
			}catch(Exception e){
				
			}
			super.onPause();
		}

		@Override
		protected void onResume() {
			if(Dialogos.customDialog!=null){
				Dialogos.customDialog.dismiss();
			}
			if (!mLocationManager_eventos.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				new Dialogos().showDialogGPS(Eventario_main.this).show();		
			}else{
				if(pause){
					 init();
					 pause=false;
				}
			}
			try{
					 registerReceiver(onBroadcast, new IntentFilter("key"));
			}catch(Exception e){}
			
			super.onResume();
		}

		
		public void abrirDetalles(String id) {
			for(int i=0;i<bean.getId_marker().length;i++){
				if(bean.getId_marker()[i].toString().equals(id)){
						Intent intent = new Intent(Eventario_main.this,Detalle_evento_Activity.class);
						intent.putExtra("nombre", bean.getNombre()[i]);
						intent.putExtra("lugar", bean.getLugar()[i]);
						intent.putExtra("hora_inicio", bean.getHora_inicio()[i]);
						intent.putExtra("hora_fin", bean.getHora_fin()[i]);
						intent.putExtra("imagen", bean.getImagen()[i]);
						intent.putExtra("descripcion", bean.getDescripcion()[i]);
						intent.putExtra("precio", bean.getPrecio()[i]);
						intent.putExtra("direccion", bean.getDireccion()[i]);
						intent.putExtra("fuente", bean.getFuente()[i]);
						intent.putExtra("fecha_inicio", bean.getFecha_inicio()[i]);
						intent.putExtra("fecha_fin", bean.getFecha_fin()[i]);
						intent.putExtra("categoria", bean.getCategoria()[i]);
						intent.putExtra("contacto", bean.getContacto()[i]);
						intent.putExtra("pagina", bean.getPagina()[i]);
						intent.putExtra("latitud", bean.getLatitud()[i]);
						intent.putExtra("longitud", bean.getLongitud()[i]);
						intent.putExtra("distancia", bean.getDistancia()[i]);
						intent.putExtra("url", bean.getUrl()[i]);
						intent.putExtra("id_marker", bean.getId_marker()[i]);
						intent.putExtra("mi_latitud", lat);
						intent.putExtra("mi_longitud", lon);
						startActivity(intent);
						break;
				}
			}

			
		}

		@Override
		public void onBackPressed() {
			if(Dialogos.customDialog!=null){
				Dialogos.customDialog.dismiss();
				finish();
			}else{
				super.onBackPressed();	
			}
		}
		
		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				lastTouched = SystemClock.uptimeMillis();
				break;
			case MotionEvent.ACTION_UP:
				final long now = SystemClock.uptimeMillis();
				if ((now - lastTouched > SCROLL_TIME)&&
						Utils.getDistanceMeters(lat, lon,map.getCameraPosition().target.latitude, map.getCameraPosition().target.longitude)>=1000) {
						anillo();
						cargarMapa(map.getCameraPosition().target.latitude,map.getCameraPosition().target.longitude);
				}
				break;
			}
			return super.dispatchTouchEvent(ev);
		}	

}
