package com.space.apps.compasstation;


import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;

public class Compass extends View implements SensorEventListener{
	
	public Grafico flecha,norte,satelite;
	public SensorManager mSensorManager;
	private ThreadJuego thread = new ThreadJuego();
	public int giro;
	public boolean ISS=false,ISS2=false;
    private URLConnection con;
    private InputStream is;
    private StringBuffer sb;
    private URL urlMuestra;
    private String fr;
    private double latISS,lonISS,latISS2,lonISS2,mylat,mylon;
    MiLocationListener donde;
    LocationManager milocManager;
    LocationListener milocListener;
	
	public Compass(Context context, AttributeSet attrs){
		super(context, attrs);
		
		flecha= new Grafico(this,context.getResources().getDrawable(
	      		  R.drawable.flecha));
		norte= new Grafico(this,context.getResources().getDrawable(
	      		  R.drawable.norte));
		satelite= new Grafico(this,context.getResources().getDrawable(
	      		  R.drawable.satelite));
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listSensors = mSensorManager.getSensorList( 
                      Sensor.TYPE_ORIENTATION);
        if (!listSensors.isEmpty()) {
           Sensor orientationSensor = listSensors.get(0);
           mSensorManager.registerListener(this, orientationSensor,
                                      SensorManager.SENSOR_DELAY_GAME);
           new Conectarm().execute("hola");
           donde=new MiLocationListener(this);
           milocManager = (LocationManager)this.getContext().getSystemService(Context.LOCATION_SERVICE);
           milocListener = donde;
           milocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, milocListener);
           }
		
	}
	
	
	@Override 
    protected void onSizeChanged(int ancho, int alto,
                                                         int ancho_anter, int alto_anter) {

          super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
          flecha.setPosX(ancho/2-flecha.getAncho()/2);
          flecha.setPosY(alto/2-flecha.getAlto()/2);
          flecha.setAngulo(90);
          norte.setPosX(ancho/2-norte.getAncho()/2);
          norte.setPosY(alto/2-norte.getAlto()/2);
          satelite.setPosX(ancho/2-satelite.getAncho()/2);
          satelite.setPosY(alto/2-satelite.getAlto()/2);
          
          thread.start();
    }
	@Override 
    synchronized protected void onDraw(Canvas canvas) {

          super.onDraw(canvas);

          norte.dibujaGrafico(canvas);
          flecha.dibujaGrafico(canvas);
          Paint p=new Paint();
    	  p.setColor(Color.WHITE);
    	  p.setAntiAlias(true);
    	  p.setTextSize(18);
    	  canvas.drawText("hola: "+String.valueOf(norte.getAngulo()), 25, 15, p);
          if(donde.found&&ISS){
        	  mylat=donde.lat;
        	  mylon=donde.lon;
        	  satelite.setAngulo(flecha.getAngle(mylat, mylon, latISS, lonISS)-giro);
        	  satelite.dibujaGrafico(canvas);
        	  if(ISS2){
        		  ISS2=false;
            	  new Conectarm().execute("hola");
        	  }
        	  
          }

    }
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		giro=(int)event.values[0];
		
	}
	
	synchronized protected void actualizaFisica() {
		norte.setAngulo(-giro);
		satelite.setAngulo(Math.abs(satelite.getAngulo()-giro));
    }
	
	
	class ThreadJuego extends Thread {
		
		   private boolean pausa,corriendo;
		   
		   public synchronized void pausar() {
		          pausa = true;
		   }
		 
		   public synchronized void reanudar() {
		          pausa = false;
		          notify();
		   }
		 
		   public void detener() {
		          corriendo = false;
		          if (pausa) reanudar();
		   }
		  
		   @Override    public void run() {
		          corriendo = true;
		          while (corriendo) {
		                 actualizaFisica();
		                 synchronized (this) {
		                       while (pausa) {
		                              try {
		                                     wait();
		                              } catch (Exception e) {
		                              }
		                       }
		                 }
		          }
		   }
		}

	public ThreadJuego getThread() {
		return thread;
	}
	
	class Conectarm extends AsyncTask<String, Void,String>{
        protected String doInBackground(String... tipom){
                try{
                    urlMuestra = new URL("http://api.open-notify.org/iss-now/");
                    con = urlMuestra.openConnection();
                    con.setDoOutput(true);
                    con.setRequestProperty( "Content-Type","application/x-www-form-urlencoded" );
                    is = con.getInputStream();
                    int ch;
                    sb = new StringBuffer();
                    while( (ch=is.read()) != -1 ){
                        sb.append( (char)ch );
                    }
                    fr=sb.toString();
                    latISS2=Double.parseDouble(fr.substring(fr.indexOf("latitude")+11,fr.indexOf(", \"longitude")-1));
                    lonISS2=Double.parseDouble(fr.substring(fr.indexOf("longitude")+12,fr.indexOf("}}")-1));
                }
                catch(Exception e){
                    fr=e.toString();
                }
            return fr+tipom;
        }
        @Override
        protected void onPostExecute(String bytes) {
        	ISS=true;
            ISS2=true;
            latISS=latISS2;
            lonISS=lonISS2;
            //Toast.makeText(Compass.this.getContext(), fr+"   "+fr.indexOf(", \"longitude"), Toast.LENGTH_LONG).show();
            //Toast.makeText(Compass.this.getContext(), "lat: "+String.valueOf(flecha.getAngle(0,0,0.5,0.5))+"\nlong: "+String.valueOf(lonISS),Toast.LENGTH_LONG).show();
          }
    }

}
	

