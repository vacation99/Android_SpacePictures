package com.example.spacepictures;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button back, next, load;
    private TextView textViewTitle, textViewDes;
    private final Integer[] countPhotos = new Integer[] {5, 10, 15, 20, 50, 100};
    private int count = 0, spinnerNumber = countPhotos[0];
    private Spinner spinner;
    private ArrayList<Picture> arrayList = new ArrayList<>(countPhotos[0]);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewDes = findViewById(R.id.textViewDes);
        spinner = findViewById(R.id.spinner);
        back = findViewById(R.id.buttonBack);
        next = findViewById(R.id.buttonNext);
        load = findViewById(R.id.buttonLoad);

        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countPhotos);
        spinner.setAdapter(arrayAdapter);
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerNumber = (Integer) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);
    }

    public void loadAction(View view) {
        new SimpleAsyncTask().execute(spinnerNumber);

        load.setVisibility(View.INVISIBLE);
        next.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.INVISIBLE);
    }

    public void nextAction(View view) {
        if (count == 0) {
            count++;
            back.setVisibility(View.VISIBLE);
        }
        else if (count == spinnerNumber - 2) {
            count++;
            next.setVisibility(View.INVISIBLE);
        }
        else
            count++;
        changeImg(arrayList.get(count));
    }

    public void backAction(View view) {
        if (count == 1) {
            count--;
            back.setVisibility(View.INVISIBLE);
        }
        else if (count == spinnerNumber - 1) {
            count--;
            next.setVisibility(View.VISIBLE);
        }
        else
            count--;
        changeImg(arrayList.get(count));
    }

    public void changeImg(Picture url_title_des) {
        Picasso.get()
                .load(url_title_des.getUrl())
                .error(R.drawable.gif)
                .into(imageView);
        textViewTitle.setText(url_title_des.getTitle());
        textViewDes.setText("Описание: " + url_title_des.getDescription());
    }

    class SimpleAsyncTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            StringBuffer content = new StringBuffer();
            try {
                URL url = new URL("https://api.nasa.gov/planetary/apod?api_key=L4juwY6U70NI1errn1qV0OTkPwWMzwPNj2cVHFw4&count=" + integers[0] + "&thumbs=false");
                URLConnection urlConnection = url.openConnection();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                bufferedReader.close();
            } catch (Exception e) {
                System.out.println("Аддрес не найден");
            }
            String JSONString = content.toString();
            try {
                JSONArray jsonArray = new JSONArray(JSONString);
                for (int i = 0; i < spinnerNumber; i++) {
                    Picture picture = new Picture(
                            jsonArray.getJSONObject(i).getString("url"),
                            jsonArray.getJSONObject(i).getString("title"),
                            jsonArray.getJSONObject(i).getString("explanation"));
                    arrayList.add(picture);
                }
            } catch (Exception e) {
                System.out.println("Ошибка");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void v) {
            changeImg(arrayList.get(count));
        }
    }
}