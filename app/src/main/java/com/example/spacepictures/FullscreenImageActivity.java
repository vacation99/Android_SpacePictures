package com.example.spacepictures;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spacepictures.db.DataModel;
import com.example.spacepictures.object.Picture;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class FullscreenImageActivity extends AppCompatActivity {

    private PhotoView photoView;
    private TextView textViewTitle, textViewDes;
    private ImageButton imBack, imNext, imSave, imDelete;
    private int count;
    private boolean visibility, favAct;
    private ArrayList<Picture> arrayListPicture = new ArrayList<>();
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        photoView = findViewById(R.id.photo_view);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewDes = findViewById(R.id.textViewDes);
        imBack = findViewById(R.id.imageButtonBack);
        imNext = findViewById(R.id.imageButtonNext);
        imDelete = findViewById(R.id.imageButtonUnSave);
        imSave = findViewById(R.id.imageButtonSave);

        Realm.init(this);
        realm = Realm.getInstance(new RealmConfiguration.Builder().name("space_pictures_DB.realm").build());

        Bundle bundle = getIntent().getExtras();

        if (savedInstanceState != null)
            count = savedInstanceState.getInt("save_count");
        else
            count = bundle.getInt("count");

        favAct = bundle.getString("fav") == null;

        arrayListPicture = bundle.getParcelableArrayList("array");

        changeImg(arrayListPicture.get(count));
    }

    public void nextAction(View view) {
        if (count == 0)
            imBack.setVisibility(View.VISIBLE);

        if (count == arrayListPicture.size() - 2)
            imNext.setVisibility(View.INVISIBLE);

        count++;

        changeImg(arrayListPicture.get(count));
    }

    public void backAction(View view) {
        if (count == 1)
            imBack.setVisibility(View.INVISIBLE);

        if (count == arrayListPicture.size() - 1)
            imNext.setVisibility(View.VISIBLE);

        count--;

        changeImg(arrayListPicture.get(count));
    }

    public void save(View view) {
        realm.executeTransactionAsync(realm1 -> {
            DataModel dataModel = new DataModel();

            dataModel.setDes(arrayListPicture.get(count).getDescription());
            dataModel.setUrl(arrayListPicture.get(count).getUrl());
            dataModel.setTitle(arrayListPicture.get(count).getTitle());

            realm1.copyToRealm(dataModel);
        }, () -> {
            imSave.setVisibility(View.INVISIBLE);
            checkImgInDB();
            Toast.makeText(this, "Сохранено в избранное", Toast.LENGTH_SHORT).show();
        });
    }

    public void delete(View view) {
        realm.executeTransactionAsync(realm1 -> {
            DataModel dataModel = realm1.where(DataModel.class).equalTo("title", textViewTitle.getText().toString()).findFirst();
            dataModel.deleteFromRealm();
        }, () -> {
            if (!favAct) {
                Toast.makeText(this, "Удалено из избранного", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                checkImgInDB();
                Toast.makeText(this, "Удалено из избранного", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeImg(Picture url_title_des) {
        checkImgInDB();

        if (count == 0 && arrayListPicture.size() == 1) {
            imBack.setVisibility(View.INVISIBLE);
            imNext.setVisibility(View.INVISIBLE);
        }
        if (count == 0)
            imBack.setVisibility(View.INVISIBLE);

        if (count == arrayListPicture.size() - 1)
            imNext.setVisibility(View.INVISIBLE);

        Picasso.get()
                .load(url_title_des.getUrl())
                .error(R.drawable.gif)
                .into(photoView);

        textViewTitle.setText(url_title_des.getTitle());
        textViewDes.setText(getString(R.string.string_des) + " " + url_title_des.getDescription());
    }

    private void checkImgInDB() {
        realm.executeTransactionAsync(realm1 -> {
            DataModel dataModel = realm1.where(DataModel.class).equalTo("title", textViewTitle.getText().toString()).findFirst();
            visibility = dataModel == null;
        }, () -> {
            if (visibility) {
                imSave.setVisibility(View.VISIBLE);
                imDelete.setVisibility(View.INVISIBLE);
            } else {
                imSave.setVisibility(View.INVISIBLE);
                imDelete.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("save_count", count);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}