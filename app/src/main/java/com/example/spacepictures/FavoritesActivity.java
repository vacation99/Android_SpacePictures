package com.example.spacepictures;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacepictures.adapter.RecyclerViewAdapter;
import com.example.spacepictures.db.DataModel;
import com.example.spacepictures.object.Picture;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class FavoritesActivity extends AppCompatActivity implements RecyclerViewAdapter.OnImageSelectedListener {

    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Picture> arrayListPicture = new ArrayList<>();
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewFav);
        recyclerViewAdapter = new RecyclerViewAdapter(this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        Realm.init(this);
        realm = Realm.getInstance(new RealmConfiguration.Builder().name("space_pictures_DB.realm").build());

        if (savedInstanceState != null)
            arrayListPicture = savedInstanceState.getParcelableArrayList("save_array_fav");
        else
            loadFromRealm();

        recyclerViewAdapter.setItems(arrayListPicture);
    }

    public void clearFavorites(View view) {
        if (!arrayListPicture.isEmpty()) {
            realm.executeTransactionAsync(
                    realm1 -> realm1.deleteAll(),
                    () -> {
                        arrayListPicture.clear();
                        recyclerViewAdapter.setItems(arrayListPicture);
                    });
        } else {
            Toast.makeText(this, "Избранное пустое", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFromRealm() {
        arrayListPicture.clear();
        RealmResults<DataModel> realmResults = realm.where(DataModel.class).findAll();

        for (DataModel dataModel : realmResults) {
            arrayListPicture.add(new Picture(dataModel.getUrl(), dataModel.getTitle(), dataModel.getDes()));
        }
    }

    @Override
    public void onImageClick(int position) {
        Intent intent = new Intent(this, FullscreenImageActivity.class);
        intent.putExtra("array", arrayListPicture);
        intent.putExtra("count", position);
        intent.putExtra("fav", "fav");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                loadFromRealm();
                recyclerViewAdapter.setItems(arrayListPicture);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("save_array_fav", arrayListPicture);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}