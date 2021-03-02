package com.example.spacepictures;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button back, next;
    private TextView textView;
    private String firstImg, secondImg, thirdImg, fourthImg, fifthImg, firstTitle, secondTitle, thirdTitle, fourthTitle, fifthTitle;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onClick();

        AsyncTask.execute(() -> {
            StringBuffer content = new StringBuffer();

            try {
                URL url = new URL("https://api.nasa.gov/planetary/apod?api_key=L4juwY6U70NI1errn1qV0OTkPwWMzwPNj2cVHFw4&count=5&thumbs=false");
                URLConnection urlConnection = url.openConnection();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line + "\n");
                }
                bufferedReader.close();
            } catch (Exception e) {
                System.out.println("Аддрес не найден");
            }

            String JSONString = content.toString();

            try {
                JSONArray jsonArray = new JSONArray(JSONString);
                firstImg = jsonArray.getJSONObject(0).getString("url");
                firstTitle = jsonArray.getJSONObject(0).getString("title");
                secondImg = jsonArray.getJSONObject(1).getString("url");
                secondTitle = jsonArray.getJSONObject(1).getString("title");
                thirdImg = jsonArray.getJSONObject(2).getString("url");
                thirdTitle = jsonArray.getJSONObject(2).getString("title");
                fourthImg = jsonArray.getJSONObject(3).getString("url");
                fourthTitle = jsonArray.getJSONObject(3).getString("title");
                fifthImg = jsonArray.getJSONObject(4).getString("url");
                fifthTitle = jsonArray.getJSONObject(4).getString("title");
            } catch (Exception e) {
                System.out.println("Ошибка");
            }
        });
    }

    public void onClick() {
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textViewTitle);
        back = findViewById(R.id.buttonBack);
        next = findViewById(R.id.buttonNext);

        next.setOnClickListener(v -> {
            switch (count) {
                case 0:
                    Picasso.get().load(firstImg).into(imageView);
                    textView.setText(firstTitle);
                    count++;
                    break;
                case 1:
                    Picasso.get().load(secondImg).into(imageView);
                    textView.setText(secondTitle);
                    count++;
                    break;
                case 2:
                    Picasso.get().load(thirdImg).into(imageView);
                    textView.setText(thirdTitle);
                    count++;
                    break;
                case 3:
                    Picasso.get().load(fourthImg).into(imageView);
                    textView.setText(fourthTitle);
                    count++;
                    break;
                case 4:
                    Picasso.get().load(fifthImg).into(imageView);
                    textView.setText(fifthTitle);
                    count++;
                    break;
            }
        });

        back.setOnClickListener(v -> {
            switch (count) {
                case 2:
                    Picasso.get().load(firstImg).into(imageView);
                    textView.setText(firstTitle);
                    count--;
                    break;
                case 3:
                    Picasso.get().load(secondImg).into(imageView);
                    textView.setText(secondTitle);
                    count--;
                    break;
                case 4:
                    Picasso.get().load(thirdImg).into(imageView);
                    textView.setText(thirdTitle);
                    count--;
                    break;
                case 5:
                    Picasso.get().load(fourthImg).into(imageView);
                    textView.setText(fourthTitle);
                    count--;
                    break;
            }
        });
    }
}