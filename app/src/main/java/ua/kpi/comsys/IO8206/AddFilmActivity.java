package ua.kpi.comsys.IO8206;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;

public class AddFilmActivity extends AppCompatActivity {
    int resFilmList;
    private ImageView imageView;
    private final int Pick_image = 1;
    Button choosePoster;
    Bitmap selectedImage;
    private List<Film> films;
    OutputStream os;
    ByteArrayOutputStream bos;
    Random random = new Random();
    Boolean uploadedImage = false;
    String userFileMovie =  "movieslistuser.txt"; // стандартное значение, которое заменится


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        try {
//            userFileMovie = getResources().getString(R.string.user_films_list); // берём имя файла из ресурсов
//        } catch (Exception e){}

        setContentView(R.layout.activity_add_film);

        imageView = (ImageView) findViewById(R.id.imageView_filmAdd);
        choosePoster = (Button) findViewById(R.id.buttonChoosePoster_filmAdd);
        bos = new ByteArrayOutputStream();

        Bundle arguments = getIntent().getExtras();
        resFilmList = (int)arguments.get("moviesListId");
    }

    public void choosePoster(View view) { // кнопка выбрать изображение
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*"); // получаемый тип
        startActivityForResult(photoPickerIntent, Pick_image); // ожидание выбора фото
        uploadedImage = true;
    }

    public void addBtn(View view) { // кнопка добавить
        String filmTitle = ((EditText)findViewById(R.id.editTextFilmTitle_filmAdd)).getText().toString();
        String filmYear = ((EditText)findViewById(R.id.editTextFilmYear_filmAdd)).getText().toString();
        String filmType = ((EditText)findViewById(R.id.editTextFilmType_filmAdd)).getText().toString();

        String newPosterName;

        if(filmTitle.length()<1){ // некорректный заголовок
            Toast.makeText(view.getContext(), "Uncorrected title", Toast.LENGTH_LONG).show();
        }
        else {
            JsonHelperFilms jsonHelperFilms = new JsonHelperFilms(resFilmList);
            jsonHelperFilms.setFileUserName(userFileMovie);
            films = jsonHelperFilms.importFilmListFromJSON(view.getContext()); // берём фильмы из файла

            if(uploadedImage){ // если выбрано изображение
                newPosterName = "poster_"+(random.nextInt(99999)+100) + ".png";

                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, bos);

                byte[] bitmapdata = bos.toByteArray();


//                File filesDir = view.getContext().getFilesDir();
                File imageFile = new File(view.getContext().getFilesDir(), newPosterName);

                try {
                    FileOutputStream fos = new FileOutputStream(imageFile);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            else newPosterName = "";

            films.add(new Film(filmTitle, filmYear, filmType, "", newPosterName, "",
                    "", "", "", "", "", "",
                    "", "", "", "", "", "", "" ));

            jsonHelperFilms.exportToJSON(view.getContext(), films);

            finish();
            Toast.makeText(view.getContext(), "Added successfully", Toast.LENGTH_LONG).show();
        }
    }

    public void returnBtn(View view) { // кнопка возврата
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { // загрузка фото
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        System.out.println("ENTER SECTION");

        if (requestCode == Pick_image) {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = imageReturnedIntent.getData(); // получить URI изображения
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri); // получить стрим
                    selectedImage = BitmapFactory.decodeStream(imageStream); // преобразовать в битмап
                    imageView.setImageBitmap(selectedImage); // показать в imageView
                    choosePoster.setBackgroundColor(getResources().getColor(R.color.posterChooseSuccess)); // фон кнопки

                } catch (FileNotFoundException e) {
                    choosePoster.setBackgroundColor(getResources().getColor(R.color.posterChooseFailure));
                    e.printStackTrace();
                }
            }
        }
    }
}