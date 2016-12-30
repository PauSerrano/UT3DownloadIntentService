package es.schooleando.downloadintent;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Interpolator;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */


//+++++++ NO OLVIDAR DEFINIR EL SERVICIO EN EL MANIFEST ++++++++++

public class DownloadIntentService extends IntentService {
    // definimos tipos de mensajes que utilizaremos en ResultReceiver
    public static final int PROGRESS = 0;
    public static final int FINSISHED = 1;
    public static final int ERROR = 2;


    private static final String TAG = "DownloadIntentService";

    private Bitmap imagenBmpDescargada;

    //++++ Realizaremos el constructor de la clase por defecto
    public DownloadIntentService() {
        super("DownloadIntentService");
    }

    //++++ Sobrescribimos este metodo donde se ejecutará la tarea
    @Override
    protected void onHandleIntent(Intent intent) {

        ResultReceiver resultado;

        // Ejemplo de como logear
        Log.d(TAG, "Servicio arrancado!");

        if (intent != null) {
            //++ Creamos un receptor de resultados recogido del intent que a dado paso al inicio del servicio ++
            resultado = intent.getParcelableExtra("receiverTag");


            //Obtenemos la url del intent
            String urlString = intent.getStringExtra("urlTag");
            Log.d(TAG, "obtnida la url: " + urlString);
            // Aquí deberás descargar el archivo y notificar a la Activity mediante un ResultReceiver que recibirás en el intent.

            try {
                URL url = new URL(urlString);
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                //Realizamos la peticion de la descarga de la imagen
                conexion.setRequestMethod("GET");
                conexion.connect();
                String tipo = conexion.getContentType();
                int tamañoRecurso = conexion.getContentLength();

                if (tipo.startsWith("image/")) {
                    InputStream is = conexion.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    byte[] buffer = new byte[1024];
                    int n = 0;
                    int total = 0;
                    //Mientras el resultado de lectura del buffer sea distinto a -1
                    Log.d(TAG, "Empieza la descarga de la imagen");
                    while ((n = is.read(buffer)) != -1) {
                        //Escribimos los bytes
                        bos.write(buffer, 0, n);

                        //Estado de la descarga en progreso

                        total += n;
                        if (tamañoRecurso != -1) {

                            Integer porc = (total * 100) / tamañoRecurso;
                            SystemClock.sleep(50);
                            Bundle b = new Bundle();
                            b.putString("ServicioDescarga", String.valueOf(porc));
                            resultado.send(this.PROGRESS, b);

                        } else {
                            Integer porc = total;
                            SystemClock.sleep(500);
                            Bundle b = new Bundle();
                            b.putString("ServicioDescarga", String.valueOf(porc));
                            resultado.send(this.PROGRESS, b);

                        }

                    }

                    //cerramos los Streams
                    bos.close();
                    is.close();

                    byte[] arrayImagen = bos.toByteArray();
                    imagenBmpDescargada = BitmapFactory.decodeByteArray(arrayImagen, 0, arrayImagen.length);

                    Log.d(TAG, "FINALIZA la descarga de la imagen");
                    Bundle b = new Bundle();
                    b.putString("ServicioDescarga", "Finalizada la descarga");
                    b.putParcelable("bitmapImagen", imagenBmpDescargada);
                    resultado.send(this.FINSISHED, b);
                }


            } catch (MalformedURLException e) {
                Bundle b = new Bundle();
                b.putString("ServicioDescarga", "Error en los datos para la descarga");
                resultado.send(this.ERROR, b);
                e.printStackTrace();
            } catch (IOException e) {
                Bundle b = new Bundle();
                b.putString("ServicioDescarga", "Error en los datos para la descarga");
                resultado.send(this.ERROR, b);
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Servicio NO Correcto! Intent = null");
        }


        // Deberamos obtener el ResultReceiver del intent
        // intent.getParcelableExtra("receiver");

        // Es importante que definas el contenido de los Intent.

        // Por ejemplo:
        //  - que enviarás al IntentService como parámetro inicial (url a descargar)
        //         intent.getgetStringExtra("url")
        //  - que enviarás a ResultReceiver para notificarle incrementos en el porcentaje de descarga (número de 0 a 100)
        //         receiver.send(PROGRESS, Bundle);
        //  - que enviarás a ResultReceiver cuando se haya finalizado la descarga (nombre del archivo temporal)
        //         receiver.send(FINISHED, Bundle);


    }
}
