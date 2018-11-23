package com.project.PotHole.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.PotHole.R;
import com.project.PotHole.async.AsyncHandler;
import com.project.PotHole.async.AsyncHttpPost;
import com.project.PotHole.async.DownloadImageTask;
import com.project.PotHole.local.LoginDataBaseAdapter;
import com.project.PotHole.sign.LoginActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends Activity {

    private ImageButton logOut, imageButtonUpload;
    private ImageView imageView;
    private String encoded_string, imageName;
    private Bitmap bitmap;
    private File file;
    private Uri fileUri;
    LoginDataBaseAdapter loginDataBaseAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.home);

        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();

        String str[] = loginDataBaseAdapter.getUsers();

        Toast.makeText(getApplicationContext(), str[0], Toast.LENGTH_LONG).show();

        logOut = (ImageButton) findViewById(R.id.imageButtonProfile);
        imageButtonUpload = (ImageButton) findViewById(R.id.imageButtonUpload);
        imageView = (ImageView) findViewById(R.id.imageView);
        /*
            This is where code downloads a picture
            new DownloadImageTask(ImageView)
            .execute( !!! in here is gonna be our feedback from database POTHOLES path column !!! );
         */
        //new DownloadImageTask(imageView).execute("http://lamp.ms.wits.ac.za/~s815108/images/test.jpg");
        AsyncHttpPost asyncHttpPost = new AsyncHttpPost(new AsyncHandler() {
            @Override
            public void handleResponse(String response) {
                processQuestions(response);
            }
        });
        asyncHttpPost.execute("http://lamp.ms.wits.ac.za/~s815108/" +
                "cars.php");


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                /*
                new AlertDialog.Builder(HomeActivity.this
                )
                        .setTitle("Sign Out Pot.Hole ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // nothing
                            }
                        })
                        .show();
                        */
            }
        });


        imageButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                getFileUri();
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(i, 10);
            }
        });
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void doRequest(View view) {

        AsyncHttpPost asyncHttpPost = new AsyncHttpPost(new AsyncHandler() {
            @Override
            public void handleResponse(String response) {
                processQuestions(response);
            }
        });
        asyncHttpPost.execute("http://lamp.ms.wits.ac.za/~s815108/" +
                "cars.php");
    }

    public void processQuestions(String output) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.postLayout);
        try {
            JSONArray myArray = new JSONArray(output);
            for (int i = 0; i < myArray.length(); i++) {
                RelativeLayout rl = (RelativeLayout) getLayoutInflater().
                        inflate(R.layout.post, null);
                JSONObject jo = (JSONObject) myArray.get(i);
                final int id = jo.getInt("id");
                String text = jo.getString("name");
                String path = jo.getString("path");
                String[] str;
                str = path.split("/");
                int last = str.length;
                System.out.println("http://lamp.ms.wits.ac.za/~s815108/images/" + str[last - 1]);
                /*
                Button t;
                t = (Button) rl.findViewById(R.id.username);
                t.setText(""+id);
                t = (Button) rl.findViewById(R.id.uploadtime);
                t.setText(text);
                */
                ImageView image = (ImageView) rl.findViewById(R.id.imageViewPost);
                ImageButton imageButton = (ImageButton) rl.findViewById(R.id.imageButtonVote);


                new DownloadImageTask(image).execute("http://lamp.ms.wits.ac.za/~s815108/images/" + str[last - 1]);
                //t = (Button) rl.findViewById(R.id.flag);
                //t.setText(path);//http://lamp.ms.wits.ac.za/~s815108/images/test.jpg
                rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println(id);
                    }
                });



                ll.addView(rl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getFileUri() {
        imageName = "testing123.jpg";
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + imageName);

        fileUri = Uri.fromFile(file);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == RESULT_OK) {
            new EncodeImage().execute();
        }
    }

    private class EncodeImage extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            bitmap = BitmapFactory.decodeFile(fileUri.getPath());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, 0);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            makeRequest();
        }
    }

    private void makeRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST,
                "http://lamp.ms.wits.ac.za/~s815108/images/uploadimage.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("encoded_string", encoded_string);
                map.put("image_name", imageName);

                return map;
            }
        };
        requestQueue.add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginDataBaseAdapter.close();
    }
}