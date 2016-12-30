package es.schooleando.downloadintent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity  implements MiResultReceiver.Receiver {

    //Primero damos ****** PERMISOS ******* A LA APP para acceder a la red en el Manifest

    private static final String LOGTAG = "UD3_Act2_MainActivity";

    Button botEmpezar;
    TextView tvActividad;
    TextView tvIndicador;
    ImageView ivImagen;

    //** Creamos un objeto MiResultReceiver **
    MiResultReceiver miResultado;


    String urlString;
    Intent urlIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Añade en el interfaz un botón y un TextView, como mínimo.

        botEmpezar = (Button) findViewById(R.id.butEmpezar);
        tvActividad = (TextView) findViewById(R.id.tvActividad);
        tvIndicador = (TextView) findViewById(R.id.tvIndicador);
        ivImagen = (ImageView) findViewById(R.id.imageView);

        //** Creamos un objeto MiResultReceiver **
        miResultado = new MiResultReceiver(new Handler());
        miResultado.setReceiver(this);

        //urlString = "http://www.fpmislata.com/joomla/images/cipfpmislata/logo.jpg";
        urlString = "http://lorempixel.com/200/300/";



        botEmpezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvIndicador.setText("De donde viene los datos del tv siguiente");
                tvActividad.setText("Situacion en el Proceso de descarga");


                //Cremos un intent al presionar el boton donde pasaremos la url
                urlIntent = new Intent(MainActivity.this, DownloadIntentService.class);
                //Rellenamos el bundle con la url a través del intent
                urlIntent.putExtra("urlTag", urlString);
                urlIntent.putExtra("receiverTag", miResultado);
                //Ejecutamos el servicio
                startService(urlIntent);
            }
        });



        // cuando pulsemos el botón deberemos crear un Intent que contendrá un Bundle con:
        // una clave "url" con la dirección de descarga asociada.
        // una clave "receiver" con un objeto ResultReceiver.
        //
        // El objeto ResultReceiver contendrá el callback que utilizaremos para recibir
        // mensajes del IntentService.

        // después deberás llamar al servicio con el intent.
    }

    @Override
    public void onReceiverResult(int resultCode, Bundle resultData) {

       Log.d(LOGTAG, "Estamos en ReceiverResult");

        switch (resultCode){

            //Caso: codigo igual a 0
            case DownloadIntentService.PROGRESS:
                tvIndicador.setText("Del Bundle creado en la descarga");
                tvActividad.setText(resultData.getString("ServicioDescarga"));
                break;
            //Caso: codigo igual a 1
            case DownloadIntentService.FINSISHED:
                tvIndicador.setText("Del Bundle creado al finalizar descarga");
                tvActividad.setText(resultData.getString("ServicioDescarga"));
                ivImagen.setImageBitmap((Bitmap) resultData.get("bitmapImagen"));
                break;
            //Caso: codigo igual a 2
            case 2:
                tvIndicador.setText("Del Bundle creado en el error de la descarga");
                tvActividad.setText(resultData.getString("ServicioDescarga"));
                break;

        }

    }
}
