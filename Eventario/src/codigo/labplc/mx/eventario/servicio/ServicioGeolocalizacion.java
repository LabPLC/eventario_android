package codigo.labplc.mx.eventario.servicio;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import codigo.labplc.mx.eventario.Eventario_main;
import codigo.labplc.mx.eventario.R;

/**
 * 
 * @author mikesaurio
 * 
 */
public class ServicioGeolocalizacion extends Service implements Runnable {
	/**
	 * Declaraci—n de variables
	 */
	
	public final String TAG = this.getClass().getSimpleName();
	public static Eventario_main taxiActivity;
	private LocationManager mLocationManager;
	private MyLocationListener mLocationListener;
	public static double latitud_inicial = 19.0f;
	public static double longitud_inicial = -99.0f;
	public static double latitud =0;
	public static double longitud=0;
	private Location currentLocation = null;
	private boolean isFirstLocation = true;
	private Thread thread;
	Double pointsLat ;
	Double pointsLon;
	private boolean isFirstTime = true;
	public static boolean serviceIsIniciado = false;
	public static boolean countTimer = true;
	public static  boolean panicoActivado = false;
	public boolean isSendMesagge= false;
    private int intervaloLocation =5000;
   
    

	@Override
	public void onCreate() {
		super.onCreate();		   
		 //escucha para la location 
		mLocationListener = new MyLocationListener();
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Toast.makeText(taxiActivity, "Servicio creado ", Toast.LENGTH_SHORT).show();
	}
	
	
	
	

	@Override
	public void onStart(Intent intent, int startId) {
		Toast.makeText(taxiActivity, "Servicio creado ", Toast.LENGTH_SHORT).show();
		if(isFirstTime){
			obtenerSenalGPS();
			isFirstTime=false;
		}
				
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		if (mLocationManager != null)
			if (mLocationListener != null)
				mLocationManager.removeUpdates(mLocationListener);

		Toast.makeText(this, "Servicio detenido ", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}
	
	

	@Override
	public IBinder onBind(Intent intencion) {
		return null;
	}


	/**
	 * handler
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// mLocationManager.removeUpdates(mLocationListener);
			updateLocation(currentLocation);
		}
	};

	/**
	 * metodo para actualizar la localizaci—n
	 * 
	 * @param currentLocation
	 * @return void
	 */
	public void updateLocation(Location currentLocation) {
		if (currentLocation != null) {
			latitud = Double.parseDouble(currentLocation.getLatitude() + "");
			longitud = Double.parseDouble(currentLocation.getLongitude() + "");
			Toast.makeText(this, "latitud "+latitud+" longitud "+longitud, Toast.LENGTH_SHORT).show();

			if (isFirstLocation) {
				latitud_inicial = latitud;
				longitud_inicial = longitud;
				isFirstLocation = false;
			} 
		//	pointsLat.add(latitud + "");
		//	pointsLon.add(longitud + "");
			
			Intent intent = new Intent("key");
			intent.putExtra("latitud", latitud);
			intent.putExtra("longitud", longitud);
			getApplicationContext().sendBroadcast(intent);


		}
	}

	
	/**
	 * Hilo de la aplicacion para cargar las cordenadas del usuario
	 */
	public void run() {
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Looper.prepare();
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, intervaloLocation, 0, mLocationListener);
			Looper.loop();
			Looper.myLooper().quit();
		} else {
			taxiActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(taxiActivity,getResources().getString(R.string.mapa_GPS_OFF), Toast.LENGTH_LONG).show();
					
				}
			});
		}
	}

	
	/**
	 * Metodo para Obtener la se–al del GPS
	 */
	private void obtenerSenalGPS() {
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * Metodo para asignar las cordenadas del usuario
	 * */
	private void setCurrentLocation(Location loc) {
		currentLocation = loc;
	}

	/**
	 * Metodo para obtener las cordenadas del GPS
	 */
	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
			// Log.d("finura",loc.getAccuracy()+"");
			if (loc != null) {
				setCurrentLocation(loc);
				handler.sendEmptyMessage(0);
			}
		}

		/**
		 * metodo que revisa si el GPS esta apagado
		 */
		public void onProviderDisabled(String provider) {
			taxiActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(taxiActivity,
							getResources().getString(R.string.mapa_GPS_OFF), Toast.LENGTH_LONG).show();
				}
			});
		}

		// @Override
		public void onProviderEnabled(String provider) {
		}

		// @Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
     
  
    
}