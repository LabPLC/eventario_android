package codigo.labplc.mx.eventario;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class Detalle_evento_Activity extends Activity {

	private String id_evento;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalle_evento);
		
		//propiedades del action bar
		 final ActionBar ab = getActionBar();
	     ab.setDisplayShowHomeEnabled(false);
	     ab.setDisplayShowTitleEnabled(false);     

	     //instancias
	     final LayoutInflater inflater = (LayoutInflater)getSystemService("layout_inflater");
	     View view = inflater.inflate(R.layout.abs_layout,null);   
	     //instancias en  
	     ab.setDisplayShowCustomEnabled(true);
	     ImageView abs_layout_iv_menu = (ImageView) view.findViewById(R.id.abs_layout_iv_menu);
	     ab.setCustomView(view,new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
	     ab.setCustomView(view);
	     
	     
	   //obtenemos los adicionales 
			Bundle bundle = getIntent().getExtras();
			if(bundle!=null){
				id_evento = bundle.getString("id_evento");	
				
				Log.d("**********", id_evento+"");

			}
			

	}
}
