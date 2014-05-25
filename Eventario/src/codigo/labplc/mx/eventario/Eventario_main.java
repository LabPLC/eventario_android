package codigo.labplc.mx.eventario;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;
import codigo.labplc.mx.eventario.bean.beanEventos;
import codigo.labplc.mx.eventario.configuracion.Configuracion_activity;
import codigo.labplc.mx.eventario.customs.CustomList;
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


public class Eventario_main extends Activity {

	public final String TAG = this.getClass().getSimpleName();

	
	private GoogleMap map;
	private double lat=0.0;
	private double lon=0.0;
	private MarkerOptions marker;
	private Handler updateBarHandler;
	private ProgressDialog pDialog;
	private int isLocalizado = 0;
    private ListView list;
	private beanEventos bean;
	private String radio="2";
	private String progreso;
	private int id_evento=-1;
	
	
	
	@SuppressWarnings({ "deprecation" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eventario_main);
		
		
		
		ServicioGeolocalizacion.taxiActivity = Eventario_main.this;
		startService(new Intent(Eventario_main.this,ServicioGeolocalizacion.class));
		 
		
		 SharedPreferences prefs = getSharedPreferences("MisPreferenciasEventario",Context.MODE_PRIVATE);
		 progreso = prefs.getString("progreso", null);
		 if(progreso!=null){
			 radio=progreso;
		 }
		
	     if(lat==0.0){
	    	 updateBarHandler = new Handler();
	    	 launchRingDialog();
	     }
	    		
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
				CameraPosition cameraPosition;
					 cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lon)).zoom(16).build();
				
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				
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
			            	
			               	Intent intent = new Intent(Eventario_main.this,Detalle_evento_Activity.class);
			            	intent.putExtra("id_evento", bean.getId()[position]);
			            	startActivity(intent);
			               Toast.makeText(Eventario_main.this, "You Clicked at " +bean.getId()[position], Toast.LENGTH_SHORT).show();
			            }
			        });
			    
			return true;
		}catch(Exception e){
			e.printStackTrace();
		return false;
		}
		
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
		map.addMarker(marker);	
	
		
	}
	
	
	
	/**
	 * crea el dialogo de espera al cargar el mapa
	 * 
	 */
	public void launchRingDialog() {

		pDialog = new ProgressDialog(Eventario_main.this);
		pDialog.setCanceledOnTouchOutside(false);
		pDialog.setMessage(getResources().getString(R.string.mapa_texto_significado_el_viaje_inicio));
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setCancelable(true);
		pDialog.show();
	}	
	
	
	
	/**
	 * manejo de transmiciones
	 */
	private BroadcastReceiver onBroadcast = new BroadcastReceiver() {

		@Override
		public void onReceive(Context ctxt, Intent t) {
			
			lat = t.getDoubleExtra("latitud", 19.0f);
			lon = t.getDoubleExtra("longitud",-99.0f);
			
			if(isLocalizado==0){
				bean = Utils.llenarEventos(lat+"",lon+"",radio);
				if(bean!=null){
					cargarEventos();	
				}
			}
			
		
			CameraPosition cameraPosition;
			cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lon)).zoom(14).build();
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			map.clear();
			
			map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				
				@Override
				public void onInfoWindowClick(Marker marker) {

			           
				   	Intent intent = new Intent(Eventario_main.this,Detalle_evento_Activity.class);
	            	startActivity(intent);
					
				}
			});
			map.setInfoWindowAdapter(new InfoWindowAdapter() {
	            @Override
	            public View getInfoWindow(Marker marker) {              
	                return null;
	            }           
	            @Override
	            public View getInfoContents(Marker marker) {
	            	 try{
	            	
		            	View v = getLayoutInflater().inflate(R.layout.windowlayout, null);
		                String s[] = marker.getTitle().split("@@");
		                TextView   pupop_nombre = (TextView) v.findViewById(R.id.pupop_nombre);
		                pupop_nombre.setText(s[0] );
		                TextView pupop_lugar = (TextView) v.findViewById(R.id.pupop_lugar);
		                pupop_lugar.setText(s[1]);
		                return v;
					}catch(Exception e){
						 View v = getLayoutInflater().inflate(R.layout.windowlayout_simple, null);
						 TextView   pupop_nombre = (TextView) v.findViewById(R.id.pupop_simple_nombre);
			              pupop_nombre.setText(getResources().getString(R.string.mapa_inicio_de_viaje));
			              id_evento=-1;
						 return v;
					}
	            	
	                
	              

	            }
	        });
			marker.position(new LatLng(lat,lon));
		   	map.addMarker(marker);
		   	
		   
			for(int i=0;i<bean.getLatitud().length;i++){
				MarkerOptions markerte= new MarkerOptions();
				markerte.position(new LatLng(Double.parseDouble(bean.getLatitud()[i]), Double.parseDouble(bean.getLongitud()[i])));
				markerte.title(bean.getNombre()[i]+"@@"+bean.getLugar()[i]);
				markerte.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_pin));
				map.addMarker(markerte);	
				
			}
		   	
		 
		 	//
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
	
		@Override
		protected void onDestroy() {
		if(pDialog!=null){
	    	pDialog.dismiss();
	    	}
			super.onDestroy();
		}

		@Override
		protected void onPause() {
			unregisterReceiver(onBroadcast);
			super.onPause();
		}

		@Override
		protected void onResume() {
			registerReceiver(onBroadcast, new IntentFilter("key"));
			
			super.onResume();
		}

		
	

}
