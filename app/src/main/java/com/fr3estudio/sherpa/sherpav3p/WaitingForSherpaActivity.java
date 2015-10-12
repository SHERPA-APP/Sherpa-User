package com.fr3estudio.sherpa.sherpav3p;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.fr3estudio.sherpa.sherpav3p.model.Response;
import com.fr3estudio.sherpa.sherpav3p.utils.CONSTANTS;
import com.fr3estudio.sherpa.sherpav3p.utils.Destination;
import com.fr3estudio.sherpa.sherpav3p.utils.ObjectSerializer;
import com.fr3estudio.sherpa.sherpav3p.utils.OverlayActivity;
import com.fr3estudio.sherpa.sherpav3p.utils.SherpaDialog;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import retrofit.Callback;
import retrofit.RetrofitError;

public class WaitingForSherpaActivity extends AppCompatActivity   implements DialogInterface.OnClickListener {

    public static final String TAG = WaitingForSherpaActivity.class.getSimpleName();
    boolean internet_access;
    private int is_overlay_shown = 0;
    private Intent overlayIntent;
    private boolean service_canceled;
    private boolean insert_service;
    private String current_token;

    private ArrayList<Destination> destinations;

    private int service_id = 0;
    private int id_cliente = 0;
    private String sherpa_id = "";

    private Handler h;
    private Runnable r;

    AnimationDrawable loadingAnimation;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONSTANTS.CANCEL_CONFIRMATION_OVARLAY) {
            if (data.getExtras() != null
                    && data.getExtras().containsKey("confirm")
                    && data.getExtras().getBoolean("confirm")) {
                service_canceled = true;
                cancelUpdates();

                RestClient.get().executeCommand("17", "[" + service_id + "]", new Callback<Response>() {
                    @Override
                    public void success(Response response, retrofit.client.Response response2) {

                        Log.i(TAG, "ServiceCanceled!!!");
                        goBackMainMenu();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideOverlay();
                        showDialog(
                                getResources().getString(
                                        R.string.msg_serverError_title),
                                getResources().getString(
                                        R.string.msg_serverError), false, false);
                    }
                });

            }
        } else if (requestCode == CONSTANTS.MENU_OPTION) {
            if (data != null && data.getExtras() != null
                    && data.getExtras().containsKey("menu_option")) {
                String menu = data.getStringExtra("menu_option");
                if (menu.equals("about") || menu.equals("terms")) {
                    showInfo(menu);
                } else if (menu.equals("profile")) {
                    getInfoToShow(menu);
                }

            }
        } else if (requestCode == CONSTANTS.CLOSE_SESSION) {
            if (data != null && data.getExtras() != null
                    && data.getExtras().containsKey(CONSTANTS.CLOSE_SESSION_ST)
                    && data.getExtras().getBoolean(CONSTANTS.CLOSE_SESSION_ST)) {
                onCloseSession();
                return;
            }
        }else if (requestCode == CONSTANTS.MENU_OVARLAY ){
            if (data != null
                    && data.getExtras() != null
                    && data.getExtras().containsKey("update")
                    && data.getExtras().getBoolean("update")) {
                System.out.println("menuOvarlay");
                SharedPreferences settings = this.getSharedPreferences(
                        "com.fr3estudio.sherpaV3P.UsersData", Context.MODE_PRIVATE);

                // id_cliente = settings.getInt("id_cliente", 0);
                String nombre = settings.getString("nombre", "");
                String apellido = settings.getString("apellido", "");
                String email = settings.getString("email", "");
                String telefono = settings.getString("telefono", "");
                String passwd = settings.getString("passwd", "");

                ArrayList<String> param = new ArrayList<>();

                param.add(nombre);
                param.add(apellido);
                param.add(email);
                param.add(telefono);
                param.add(id_cliente + "");

                RestClient.get().executeCommand("6", param.toString(), new Callback<Response>() {
                    @Override
                    public void success(Response response, retrofit.client.Response response2) {

                        if (response.getStatus() == 0) {
                            hideOverlay();
                            showDialog(
                                    getResources().getString(
                                            R.string.msg_title_success),
                                    getResources().getString(
                                            R.string.msg_detail_infoSaved), false,
                                    false);

                        } else {
                            hideOverlay();
                            showDialog(
                                    getResources().getString(R.string.msg_serverError_title),
                                    getResources()
                                            .getString(R.string.msg_detail_info_not_Saved),
                                    false, false);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println("Error [" + error + "]");

                        hideOverlay();
                        showDialog(
                                getResources().getString(
                                        R.string.msg_serverError_title),
                                getResources().getString(
                                        R.string.msg_serverError), false, false);
                    }
                });

                if (passwd != null && !passwd.isEmpty()){
                    param = new ArrayList<>();
                    param.add(passwd);
                    param.add(id_cliente + "");
                    RestClient.get().executeCommand("18", param.toString(), new Callback<Response>() {
                        @Override
                        public void success(Response response, retrofit.client.Response response2) {

                            if (response.getStatus() != 0){
                                hideOverlay();
                                showDialog(
                                        getResources().getString(R.string.msg_serverError_title),
                                        getResources()
                                                .getString(R.string.msg_detail_info_not_Saved),
                                        false, false);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            System.out.println("Error [" + error + "]");

                            hideOverlay();
                            showDialog(
                                    getResources().getString(
                                            R.string.msg_serverError_title),
                                    getResources().getString(
                                            R.string.msg_serverError), false, false);
                        }
                    });
                }

            }
            if (data != null && data.getExtras() != null
                    && data.getExtras().containsKey(CONSTANTS.CLOSE_SESSION_ST)
                    && data.getExtras().getBoolean(CONSTANTS.CLOSE_SESSION_ST)) {

                onBackPressed();
                return;
            }
        }else if (requestCode == CONSTANTS.CONFIRMATION_OVARLAY){
            if (data != null && data.getExtras() != null
                    && data.getExtras().containsKey("confirm")
                    && data.getExtras().getBoolean("confirm")) {
                onCloseSession();
            }
        }
    }

    public void getInfoToShow(String type) {


        RestClient.get().executeCommand("5", "[" + id_cliente + "]", new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {
                if (response != null && response.getClientes() != null && response.getClientes().size() > 0) {
                    String nombre = response.getClientes().get(0).getNombre_cliente();
                    String apellido = response.getClientes().get(0).getApellido_cliente();
                    String email = response.getClientes().get(0).getEmail_cliente();
                    String telefono = response.getClientes().get(0).getTelefono_cliente();

                    SharedPreferences settings = WaitingForSherpaActivity.this
                            .getSharedPreferences(
                                    "com.fr3estudio.sherpaV3P.UsersData",
                                    Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    // editor.putInt("id_cliente", id_cliente);
                    editor.putString("nombre", nombre);
                    editor.putString("apellido", apellido);
                    editor.putString("email", email);
                    editor.putString("telefono", telefono);
                    System.out.println("metiendo " + nombre
                            + apellido + email + telefono);
                    editor.commit();

                    hideOverlay();
                    showInfo("profile");
                }

            }

            @Override
            public void failure(RetrofitError error) {
                hideOverlay();
                showDialog(
                        getResources().getString(
                                R.string.msg_serverError_title),
                        getResources().getString(
                                R.string.msg_serverError), false, false);
            }
        });

    }

    public void showInfo(String type) {

        System.out.println("show info");
        Intent i = new Intent(this, MenuActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("code", type);

        startActivityForResult(i, CONSTANTS.MENU_OVARLAY);
    }

    private void goBackMainMenu() {
        Intent result = new Intent();
        result.putExtra(CONSTANTS.CLOSE_SESSION_ST, false);
        setResult(CONSTANTS.CLOSE_SESSION, result);
        WaitingForSherpaActivity.this.finish();
    }
    public void onCloseSession() {
        Log.i(TAG, "ClossingSession");
        cancelUpdates();
        SharedPreferences settings = this.getSharedPreferences(
                "com.fr3estudio.sherpaV3P.UsersData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(CONSTANTS.ID_SHERPA, "");
        editor.putInt(CONSTANTS.ID_CLIENT, 0);
        editor.commit();

        Intent result = new Intent();
        result.putExtra(CONSTANTS.CLOSE_SESSION_ST, true);
        setResult(CONSTANTS.CLOSE_SESSION, result);
        WaitingForSherpaActivity.this.finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_sherpa);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustumView = mInflater.inflate(R.layout.menu_action_bar, null);
        ((Button) mCustumView.findViewById(R.id.show_menu_ma))
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        System.out.println("mostrar el creado desde el main.");
                        is_overlay_shown++;
                        overlayIntent = new Intent(v.getContext(),
                                OverlayActivity.class);
                        overlayIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        overlayIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        overlayIntent.putExtra("code",
                                OverlayActivity.SHOW_MENU);


                        startActivityForResult(overlayIntent,
                                CONSTANTS.MENU_OPTION);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);

                    }
                });
        mActionBar.setCustomView(mCustumView);
        mActionBar.setDisplayShowCustomEnabled(true);

        h = new Handler();
        r = new Runnable() {

            @Override
            public void run() {
                isServiceAttended();
            }
        };

        ImageView loading = (ImageView) findViewById(R.id.imageView1);
        loading.setBackgroundResource(R.drawable.loading_animation);
        loadingAnimation = (AnimationDrawable) loading.getBackground();
        loadingAnimation.start();

        //destinations = this.getIntent().getParcelableExtra(CONSTANTS.DESTINATIONS_NAME);

        SharedPreferences settings = this.getSharedPreferences(
                "com.fr3estudio.sherpaV3P.UsersData", Context.MODE_PRIVATE);

        String data = settings.getString(CONSTANTS.DESTINATIONS_NAME,"");


        try {
            destinations = (ArrayList<Destination>) ObjectSerializer.deserialize(data);
        } catch (IOException e) {
            e.printStackTrace();
            showDialog(getResources().getString(
                            R.string.msg_savingData_title),
                    getResources().getString(
                            R.string.msg_detail_internal), false, false);
            destinations = new ArrayList<>();
        }
        Log.i("Waiting", "se cargaron: " + destinations.size() + " destinos");

        insert_service = this.getIntent().getBooleanExtra(CONSTANTS.INSERT_SERVICE, false);
        id_cliente = this.getIntent().getIntExtra(CONSTANTS.ID_CLIENT, 0);
        if (insert_service) {
            insertServiceRequest();
        } else {
            service_id = this.getIntent().getIntExtra(CONSTANTS.ID_SERVICE, 0);
            scheduleAttentionQuery(0);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        System.out.println("[WaitingForSherpa]STOPPED!!!");

            loadingAnimation.stop();
            cancelUpdates();

        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
            // System.out.println("is in service ? "+inService);

            loadingAnimation.start();
            // --restart requesting
            scheduleAttentionQuery(0);

    }

    private void cancelUpdates() {

        if (h != null) {
            h.removeCallbacksAndMessages(null);
        }
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void insertServiceRequest() {
        // send data to Web Service.
        Random rand = new Random();
        service_id = rand.nextInt(Integer.MAX_VALUE);
        int tokenSeed = rand.nextInt(Integer.MAX_VALUE);
        current_token = md5(tokenSeed + "");

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String fecha = sdf.format(cal.getTime());

        int deliver_state = 1;

        int costo = (int) Math.round(destinations.get(0).total_price);

        ArrayList<String> param = new ArrayList<>();
        param.add(service_id + "");
        param.add(current_token);
        param.add(fecha);
        param.add(deliver_state + "");
        param.add(id_cliente + "");
        param.add(costo + "");
        param.add("0");
        RestClient.get().executeCommand("8", param.toString(), new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {

                insertRoutes();
            }

            @Override
            public void failure(RetrofitError error) {
                hideOverlay();
                showDialog(
                        getResources().getString(
                                R.string.msg_serverError_title),
                        getResources().getString(
                                R.string.msg_serverError), false, false);
            }
        });


    }

    public void insertRoutes() {

        int queriesToInsert = destinations.size();
        ArrayList<String> param = new ArrayList<>();

        for (int i = 0; i < destinations.size() ; i++) {
            param.add("pto" + i);
            param.add(new String(destinations.get(i).latitude+"").replace(',', ' '));
            param.add(new String(destinations.get(i).longitude + "").replace(',', ' '));
            param.add(new String(destinations.get(i).service_detail).replace(',', ' '));
            param.add("no");
            param.add(new String(destinations.get(i).address).replace(',', ' '));
            param.add(new String(service_id + "").replace(',', ' '));
            param.add("),");
        }
        Log.i("Waiting","====>"+param.toString());

        RestClient.get().executeCommand("9", param.toString(), new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {
                scheduleAttentionQuery(CONSTANTS.TIME_BETWEEN_SERVICEA_CALLS);

            }

            @Override
            public void failure(RetrofitError error) {
                hideOverlay();
                showDialog(
                        getResources().getString(
                                R.string.msg_serverError_title),
                        getResources().getString(
                                R.string.msg_serverError), false, false);
            }
        });

    }

    public void cancelService(View v) {
        showConfirmation(
                getResources().getString(R.string.st_title_cancelServiceQ),
                getResources().getString(R.string.st_subtitle_cancelServiceQ));
    }

    private void showConfirmation(String title, String subTitle) {
        // System.out.println("Showing confirmation.");
        Intent overlayIntent = new Intent(this, OverlayActivity.class);
        overlayIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overlayIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        overlayIntent.putExtra("code", OverlayActivity.CANCEL_CONFIRMATION);
        overlayIntent.putExtra("title", title);
        overlayIntent.putExtra("subtitle", subTitle);
        startActivityForResult(overlayIntent,
                CONSTANTS.CANCEL_CONFIRMATION_OVARLAY);
    }

    public void scheduleAttentionQuery(int interval) {
        System.out.println("SCHEDULING AT ... " + interval);
        h.removeCallbacksAndMessages(null);
        h.postDelayed(r, interval);
    }


    public void isServiceAttended() {


        RestClient.get().executeCommand("10", "[" + service_id + "]", new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {

                if (response != null && response.getDiligencias() != null && response.getDiligencias().size() > 0 &&
                        !response.getDiligencias().get(0).getToken_actualizaciones().isEmpty()) {
                    String token = response.getDiligencias().get(0).getToken_actualizaciones();
                    if (current_token.equals(token) && !service_canceled) {
                        scheduleAttentionQuery(CONSTANTS.TIME_BETWEEN_SERVICEA_CALLS);
                    } else if (!current_token.equals(token)) {
                        sherpa_id = response.getDiligencias().get(0).getId_sherpa();

                        updateServiceId();
                    }
                } else {
                    scheduleAttentionQuery(CONSTANTS.TIME_BETWEEN_SERVICEA_CALLS);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideOverlay();
                showDialog(
                        getResources().getString(
                                R.string.msg_serverError_title),
                        getResources().getString(
                                R.string.msg_serverError), false, false);
            }
        });


    }

    public void updateServiceId() {

        RestClient.get().executeCommand("11", "[" + service_id + "]", new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {
                showServiceInAction();
            }

            @Override
            public void failure(RetrofitError error) {
                hideOverlay();
                showDialog(
                        getResources().getString(
                                R.string.msg_serverError_title),
                        getResources().getString(
                                R.string.msg_serverError), false, false);
            }
        });

    }

    public void showServiceInAction() {
        Intent i = new Intent(this, ServiceInActionActivity.class);
        i.putExtra(CONSTANTS.ID_CLIENT, id_cliente);
        i.putExtra(CONSTANTS.ID_SERVICE, service_id);
        i.putExtra(CONSTANTS.ID_SHERPA, sherpa_id);
       // i.putExtra(CONSTANTS.DESTINATIONS_NAME, destinations);
        String data = "";
        try {
            data = ObjectSerializer.serialize(destinations);
        } catch
                (IOException e) {
            e.printStackTrace();
        }
        //i.putExtra(CONSTANTS.DESTINATIONS_NAME, destinations);
        SharedPreferences settings = this.getSharedPreferences("com.fr3estudio.sherpaV3P.UsersData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(CONSTANTS.DESTINATIONS_NAME, data);
        editor.commit();
        startActivity(i);

        this.finish();

    }

    public void showDialog(String title, String msg, boolean cancel,
                           boolean listen) {
        if (!listen) {
            SherpaDialog.showDialog(title, msg, cancel, this, getResources()
                    .getString(R.string.st_accept));
        } else {
            SherpaDialog.showDialogRes(title, msg, cancel, this, this,
                    getResources().getString(R.string.st_accept));
        }
    }

    public void hideOverlay() {
        if (is_overlay_shown > 0) {
            overlayIntent = new Intent(this, OverlayActivity.class);
            overlayIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            overlayIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overlayIntent.putExtra("code", OverlayActivity.EXIT);
            startActivity(overlayIntent);
            is_overlay_shown--;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void showConfirmation() {
        // System.out.println("Showing confirmation.");
        Intent oiIntent = new Intent(this, OverlayActivity.class);
        oiIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        oiIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        oiIntent.putExtra("code", OverlayActivity.CONFIRMATION);
        oiIntent.putExtra("title",
                getResources().getString(R.string.st_title_closeSessionQ));
        oiIntent.putExtra("subtitle",
                getResources().getString(R.string.st_subtitle_closeSessionQ));
        startActivityForResult(oiIntent, CONSTANTS.CONFIRMATION_OVARLAY);
    }

    @Override
    public void onBackPressed() {
        showConfirmation();
    }
}
