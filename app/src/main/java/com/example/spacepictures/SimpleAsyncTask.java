package com.example.spacepictures;

import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

public class SimpleAsyncTask extends AsyncTask<Integer, Void, Void> {
    private WeakReference<ImageView> imageView;
    private WeakReference<TextView> textViewTitle, textViewDes;

    SimpleAsyncTask(ImageView imageView, TextView textViewTitle, TextView textViewDes) {
        this.imageView = new WeakReference<>(imageView);
        this.textViewTitle = new WeakReference<>(textViewTitle);
        this.textViewDes = new WeakReference<>(textViewDes);
    }

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
            for (int i = 0; i < integers[0]; i++) {
                Picture picture = new Picture(
                        jsonArray.getJSONObject(i).getString("url"),
                        jsonArray.getJSONObject(i).getString("title"),
                        jsonArray.getJSONObject(i).getString("explanation"));
                MainActivity.arrayList.add(picture);
            }
        } catch (Exception e) {
            System.out.println("Ошибка");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        Picasso.get()
                .load(MainActivity.arrayList.get(0).getUrl())
                .error(R.drawable.gif)
                .into(imageView.get());
        textViewTitle.get().setText(MainActivity.arrayList.get(0).getTitle());
        textViewDes.get().setText("Описание: " + MainActivity.arrayList.get(0).getDescription());
    }
}