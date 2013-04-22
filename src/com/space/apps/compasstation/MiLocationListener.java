package com.space.apps.compasstation;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

public class MiLocationListener implements LocationListener {
	Compass com;
	public double lat=0,lon=0;
	public boolean found=false;
	public MiLocationListener(Compass c) {
		// TODO Auto-generated constructor stub
		com=c;
	}
	
	public void onLocationChanged(Location loc){
		found=true;
		lat=loc.getLatitude();
		lon=loc.getLongitude();
		String coordenadas = "Mis coordenadas son: " + "Latitud = " + loc.getLatitude() + "Longitud = " + loc.getLongitude();
		Toast.makeText( com.getContext(),coordenadas,Toast.LENGTH_LONG).show();
		}
	public void onProviderDisabled(String provider){
		//Toast.makeText( com.getContext(),"Gps Desactivado",Toast.LENGTH_SHORT ).show();
	}
	public void onProviderEnabled(String provider)
	{
		//Toast.makeText( com.getContext(),"Gps Activo",Toast.LENGTH_SHORT ).show();
	}
	public void onStatusChanged(String provider, int status, Bundle extras){}
	}
