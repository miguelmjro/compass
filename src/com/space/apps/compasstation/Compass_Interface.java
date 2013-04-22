package com.space.apps.compasstation;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Compass_Interface extends Activity {
	Compass com;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compass);
        com=(Compass)findViewById(R.id.compass);
    }
    @Override    protected void onPause() {
        super.onPause();
        // Desactivamos notificaciones para ahorrar batería
        com.milocManager.removeUpdates(com.milocListener);
  }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compass__interface, menu);
        return true;
    }
}
