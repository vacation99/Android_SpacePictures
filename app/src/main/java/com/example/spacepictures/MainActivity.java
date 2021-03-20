package com.example.spacepictures;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button back, next, load;
    private TextView textViewTitle, textViewDes;
    private final Integer[] countPhotos = new Integer[] {5, 10, 15, 20, 50, 100};
    private int count = 0, spinnerNumber = countPhotos[0];
    private Spinner spinner;
    private ArrayList<Picture> arrayListPicture = new ArrayList<>();
    private List<Object> arrayListObject = new ArrayList<>();

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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.nasa.gov/planetary/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        retrofitInterface.someResponse(spinnerNumber).enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                arrayListObject.addAll(response.body());
                for (int i = 0; i < spinnerNumber; i++) {
                    Picture picture = new Picture(
                            arrayListObject.get(i).getUrl(),
                            arrayListObject.get(i).getTitle(),
                            arrayListObject.get(i).getExplanation());
                    arrayListPicture.add(picture);
                }
                changeImg(arrayListPicture.get(0));
            }
            @Override
            public void onFailure(Call<List<Object>> call, Throwable t) {
                t.printStackTrace();
            }
        });

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
        changeImg(arrayListPicture.get(count));
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
        changeImg(arrayListPicture.get(count));
    }

    public void changeImg(Picture url_title_des) {
        Picasso.get()
                .load(url_title_des.getUrl())
                .error(R.drawable.gif)
                .into(imageView);
        textViewTitle.setText(url_title_des.getTitle());
        textViewDes.setText("Описание: " + url_title_des.getDescription());
    }
}