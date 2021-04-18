package com.example.spacepictures;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spacepictures.db.DataModel;
import com.example.spacepictures.object.Picture;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class FullscreenImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textViewTitle, textViewDes;
    private Button back, next;
    private int count;
    private ArrayList<Picture> arrayListPicture = new ArrayList<>();
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        imageView = findViewById(R.id.imageViewFull);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewDes = findViewById(R.id.textViewDes);
        back = findViewById(R.id.buttonBack);
        next = findViewById(R.id.buttonNext);
        Button delete = findViewById(R.id.buttonDel);
        Button save = findViewById(R.id.buttonSave);

        Realm.init(this);
        realm = Realm.getInstance(new RealmConfiguration.Builder().name("space_pictures_DB.realm").build());

        Bundle bundle = getIntent().getExtras();

        if (savedInstanceState != null)
            count = savedInstanceState.getInt("save_count");
        else
            count = bundle.getInt("count");

        if (bundle.getString("fav") == null)
            save.setVisibility(View.VISIBLE);
        else
            save.setVisibility(View.INVISIBLE);

        if (bundle.getString("del") == null)
            delete.setVisibility(View.INVISIBLE);
        else
            delete.setVisibility(View.VISIBLE);

        arrayListPicture = bundle.getParcelableArrayList("array");

        changeImg(arrayListPicture.get(count));
    }

    public void nextAction(View view) {
        if (count == 0)
            back.setVisibility(View.VISIBLE);

        if (count == arrayListPicture.size() - 2)
            next.setVisibility(View.INVISIBLE);

        count++;

        changeImg(arrayListPicture.get(count));
    }

    public void backAction(View view) {
        if (count == 1)
            back.setVisibility(View.INVISIBLE);

        if (count == arrayListPicture.size() - 1)
            next.setVisibility(View.VISIBLE);

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
        });
        Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
    }

    public void delete(View view) {
        realm.executeTransactionAsync(realm1 -> {
            DataModel dataModel = realm1.where(DataModel.class).equalTo("title", textViewTitle.getText().toString()).findFirst();
            dataModel.deleteFromRealm();
        }, () -> {
            Toast.makeText(this, "Удалено из избранного", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        });
    }

    private void changeImg(Picture url_title_des) {
        if (count == 0 && arrayListPicture.size() == 1) {
            back.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
        }
        if (count == 0)
            back.setVisibility(View.INVISIBLE);

        if (count == arrayListPicture.size() - 1)
            next.setVisibility(View.INVISIBLE);

        Picasso.get()
                .load(url_title_des.getUrl())
                .error(R.drawable.gif)
                .into(imageView);

        textViewTitle.setText(url_title_des.getTitle());
        textViewDes.setText(getString(R.string.string_des) + " " + url_title_des.getDescription());
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