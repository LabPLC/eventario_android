package codigo.labplc.mx.eventario.dialogos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import codigo.labplc.mx.eventario.R;

public class Dialogos {

	public static AlertDialog customDialog= null;	//Creamos el dialogo generico

	
	/**
	 * Dialogo para asegurar que quieres salir de la app
	 *
	 * @param Activity (actividad que llama al di‡logo)
	 * @return Dialog (regresa el dialogo creado)
	 **/
	
	public Dialog showDialogGPS(final Activity activity)
    {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    View view = activity.getLayoutInflater().inflate(R.layout.dialogo_gps, null);
	    builder.setView(view);
	    builder.setCancelable(true);
        //tipografias

	  //escucha del boton aceptar
        ((Button) view.findViewById(R.id.dialogo_salir_btnAceptar)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
            	Dialogos.customDialog.dismiss(); 
            	activity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });

        ((Button) view.findViewById(R.id.dialogo_salir_btnCancelar)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
            	Dialogos.customDialog.dismiss(); 
                activity.finish();
            }
        });
        return (customDialog=builder.create());// return customDialog;//regresamos el di‡logo
    }   
	
	/**
	 * Dialogo para mostrar adicionales
	 *
	 * @param Activity (actividad que llama al di‡logo)
	 * @return Dialog (regresa el dialogo creado)
	 **/
	
	public Dialog showDialogExtras(final Activity activity,String titulo,String contenido)
    {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    View view = activity.getLayoutInflater().inflate(R.layout.dialogo_datos, null);
	    builder.setView(view);
	    builder.setCancelable(true);
        //tipografias

	    ((TextView) view.findViewById(R.id.dialogo_datos_tv_titulo)).setText(titulo);
	    ((TextView) view.findViewById(R.id.dialogo_datos_tv_contenido)).setText(contenido);
	  //escucha del boton aceptar
        ((Button) view.findViewById(R.id.dialogo_datos_btn_aceptar)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
            	Dialogos.customDialog.dismiss(); 

            }
        });

        return (customDialog=builder.create());// return customDialog;//regresamos el di‡logo
    }  
	
	
}
