package com.example.spacepictures;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacepictures.adapter.RecyclerViewAdapter;
import com.example.spacepictures.object.Picture;
import com.example.spacepictures.pojo.Object;
import com.example.spacepictures.retrofit.RetrofitInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnImageSelectedListener {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private TextView textView;
    private ProgressBar progressBar;
    private FloatingActionButton fav, update;
    private final int countPictures = 30;
    private ArrayList<Picture> arrayListPicture = new ArrayList<>(countPictures);
    private List<Object> arrayListObject = new ArrayList<>(countPictures);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textViewMain);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        fav = findViewById(R.id.floatingActionButtonFav);
        update = findViewById(R.id.floatingActionButtonUpdate);

        recyclerViewAdapter = new RecyclerViewAdapter(this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        if (savedInstanceState != null) {
            arrayListPicture = savedInstanceState.getParcelableArrayList("save_array");
            recyclerViewAdapter.setItems(arrayListPicture);
            showRV();
        } else
            getPictures();
    }

    public void updatePictures(View view) {
        arrayListObject.clear();
        arrayListPicture.clear();
        hideRV();
        getPictures();
    }

    public void loadFavorites(View view) {
        Intent intent = new Intent(this, FavoritesActivity.class);
        startActivity(intent);
    }

    private void getPictures() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.nasa.gov/planetary/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        retrofitInterface.someResponse(countPictures).enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                arrayListObject.addAll(response.body());
                for (int i = 0; i < countPictures; i++) {
                    arrayListPicture.add(new Picture(
                            arrayListObject.get(i).getUrl(),
                            arrayListObject.get(i).getTitle(),
                            arrayListObject.get(i).getExplanation()));
                }
                recyclerViewAdapter.setItems(arrayListPicture);
                showRV();
            }
            @Override
            public void onFailure(Call<List<Object>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void hideRV() {
        textView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        fav.setVisibility(View.INVISIBLE);
        update.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void showRV() {
        textView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        fav.setVisibility(View.VISIBLE);
        update.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onImageClick(int position) {
        Intent intent = new Intent(this, FullscreenImageActivity.class);
        intent.putExtra("array", arrayListPicture);
        intent.putExtra("count", position);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("save_array", arrayListPicture);
    }
}