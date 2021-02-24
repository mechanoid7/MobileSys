package ua.kpi.comsys.IO8206.ui.images;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ua.kpi.comsys.IO8206.R;

import static android.app.Activity.RESULT_OK;

public class ImagesList extends Fragment {
    private List<List<String>>  imagesToShow = new ArrayList<>();
    private ImageAdapter adapter;
    ListView listView;
    List<List<String>> images = new ArrayList<>(); // список фото разбитых в группы по 9 штук
    private final int Pick_image = 1;
    Random random = new Random();
    FloatingActionButton addImageBtn;
    JsonHelperImages jsonHelperImages = new JsonHelperImages();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Toast.makeText(getContext(), "Good luck :)", Toast.LENGTH_LONG).show();
//    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false); // не показывать стрелку "назад"
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false); // не показывать стрелку "назад"

        View root = inflater.inflate(R.layout.fragment_four_tab, container, false);
        addImageBtn = root.findViewById(R.id.imageAddBtn); // кнопка добавления фото

        jsonHelperImages.setFileUserName("images_list.txt");
        images = jsonHelperImages.importStringListFromJSON(getContext()); // при первичной загрузке получить список уже загруженных фото и json
        listView = root.findViewById(R.id.imagesList); // получить лисьВью
        if(images != null){
            adapter = new ImageAdapter(getActivity(), R.layout.images_list, images); // объект кастомного адаптера

            listView.setAdapter(adapter); // установка адаптера
        }
        else{
            Toast.makeText(getContext(), "Failed to get data", Toast.LENGTH_LONG).show();
        }


        addImageBtn.setOnClickListener(new View.OnClickListener() { // при нажатии на кнопку "добавить"
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Pick_image);
            }
        });

        return root;
    }


    private class ImageAdapter extends ArrayAdapter<List<String>>{ // свой адаптер
        ImageAdapter(Context context, int textViewResourceId, List<List<String>> objects) {
            super(context, textViewResourceId, objects);
            imagesToShow = objects; // список изображений
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { // переопределение
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.images_list, parent, false);
            String currentImageName;

            ImageView image1 = (ImageView) row.findViewById(R.id.imageListItem1); // объекты изобажений паттерна
            ImageView image2 = (ImageView) row.findViewById(R.id.imageListItem2);
            ImageView image3 = (ImageView) row.findViewById(R.id.imageListItem3);
            ImageView image4 = (ImageView) row.findViewById(R.id.imageListItem4);
            ImageView image5 = (ImageView) row.findViewById(R.id.imageListItem5);
            ImageView image6 = (ImageView) row.findViewById(R.id.imageListItem6);
            ImageView image7 = (ImageView) row.findViewById(R.id.imageListItem7);
            ImageView image8 = (ImageView) row.findViewById(R.id.imageListItem8);
            ImageView image9 = (ImageView) row.findViewById(R.id.imageListItem9);

            List<ImageView> imageViews = new ArrayList<>(); // список объектов
            imageViews.add(image1);
            imageViews.add(image2);
            imageViews.add(image3);
            imageViews.add(image4);
            imageViews.add(image5);
            imageViews.add(image6);
            imageViews.add(image7);
            imageViews.add(image8);
            imageViews.add(image9);

            for(int i=0; i<imagesToShow.get(position).size(); i++){
                try { // пробуем установить пользовательское изображение
                    currentImageName = imagesToShow.get(position).get(i);
                    File imageFile = new File(getContext().getFilesDir() + "/" + currentImageName); // пользовательское изображение
                    InputStream is = new FileInputStream(imageFile);

                    Bitmap userImage = BitmapFactory.decodeStream(is); // фото в стрим
                    imageViews.get(i).setImageBitmap(userImage); // установка фото
                } catch (Exception e) {e.printStackTrace();} // стандартная картинка
            }
            return row;
        }

        public String handle(String str){ // обработчик строки
            if(str.equals("")) return "None"; // если не задан любой из параметров
            else return str;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { // загрузка фото
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == Pick_image & imageReturnedIntent!=null) {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = imageReturnedIntent.getData(); // получить URI изображения
                    final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri); // получить стрим
                    Bitmap selectedImage1 = BitmapFactory.decodeStream(imageStream); // преобразовать в битмап

                    String newImageName = "image_"+(random.nextInt(99999)+100) + ".png";

                    if (images != null){ //если список изображений не null
                        if (images.size()==0){ // если список только инициализирован и пустой
                            List<String> tempImageList = new ArrayList<>();
                            images.add(tempImageList);
                        }
                        if (images.get(images.size()-1).size()>=9){ // если в последнем элементе списка не меньше девяти элементов
                            List<String> tempImageList = new ArrayList<>();
                            tempImageList.add(newImageName);
                            images.add(tempImageList);
                        }
                        else { // если меньше девяти элементов
                            images.get(images.size()-1).add(newImageName);
                        }
                    }

                    ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
                    selectedImage1.compress(Bitmap.CompressFormat.JPEG, 80, bos2);
                    byte[] bitmapdata = bos2.toByteArray();
                    File imageFile = new File(getContext().getFilesDir(), newImageName);

                    try {
                        FileOutputStream fos = new FileOutputStream(imageFile);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                        requireActivity().recreate(); // обновить ЛистВью, если загрузка успешна

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    jsonHelperImages.exportToJSON(getContext(), images);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

