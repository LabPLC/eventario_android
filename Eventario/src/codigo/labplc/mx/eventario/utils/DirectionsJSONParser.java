package codigo.labplc.mx.eventario.utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import codigo.labplc.mx.eventario.bean.InfoPointBean;

import com.google.android.gms.maps.model.LatLng;

public class DirectionsJSONParser {
    
    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){
        
        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;    
        
        try {            
            
            jRoutes = jObject.getJSONArray("routes");
            
            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){            
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();
                
                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
                    
                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);
                        
                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);                        
                        }                                
                    }
                    routes.add(path);
                }
            }
            
        } catch (JSONException e) {            
            e.printStackTrace();
        }catch (Exception e){            
        }
        
        
        return routes;
    }    
    
    
    /**
     * Method to decode polyline points 
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java 
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
    
    
    
    
    public ArrayList<InfoPointBean> parsePoints(String strResponse) {
        ArrayList<InfoPointBean> result=new ArrayList<InfoPointBean>();
        try {
            JSONObject obj=new JSONObject(strResponse);
            JSONArray array=obj.getJSONArray("results");
            for(int i=0;i<array.length();i++)
            {
                InfoPointBean point=new InfoPointBean();
                JSONObject item=array.getJSONObject(i);
                ArrayList<HashMap<String, Object>> tblPoints=new ArrayList<HashMap<String,Object>>();
                JSONArray jsonTblPoints=item.getJSONArray("address_components");
                for(int j=0;j<jsonTblPoints.length();j++)
                {
                    JSONObject jsonTblPoint=jsonTblPoints.getJSONObject(j);
                    HashMap<String, Object> tblPoint=new HashMap<String, Object>();
                    @SuppressWarnings("unchecked")
					Iterator<String> keys=jsonTblPoint.keys();
                    while(keys.hasNext())
                    {
                        String key=(String) keys.next();
                        if(tblPoint.get(key) instanceof JSONArray)
                        {
                            tblPoint.put(key, jsonTblPoint.getJSONArray(key));
                        }
                        tblPoint.put(key, jsonTblPoint.getString(key));
                    }
                    tblPoints.add(tblPoint);
                }
                point.setAddressFields(tblPoints);
                point.setStrFormattedAddress(item.getString("formatted_address"));
                JSONObject geoJson=item.getJSONObject("geometry");
                JSONObject locJson=geoJson.getJSONObject("location");
                point.setDblLatitude(Double.parseDouble(locJson.getString("lat")));
                point.setDblLongitude(Double.parseDouble(locJson.getString("lng")));
                result.add(point);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}
