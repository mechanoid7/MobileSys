package ua.kpi.comsys.IO8206.ui.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ua.kpi.comsys.IO8206.R;

public class ImagesList extends Fragment {
    private List<List<String>>  imagesToShow = new ArrayList<>();
    private ImageAdapter adapter;
    ListView listView;
    List<List<String>> images = new ArrayList<>(); // список фото разбитых в группы по 9 штук
    private final int Pick_image = 1;
    Random random = new Random();
//    FloatingActionButton addImageBtn;
    JsonHelperImages jsonHelperImages = new JsonHelperImages();
    String API_KEY = "19193969-87191e5db266905fe8936d565"; // ключ апи
    int COUNT = 27; // кол-во выводимых изображений
    String REQUEST = "\"night+city\""; // поисковый запрос
    URL url;
    View root;
//    String imageUrlTarget="\"largeImageURL\":\""; // large img, long time load
//    String imageUrlTarget="\"webformatURL\":\""; // middle img, short time load
    String imageUrlTarget="\"previewURL\":\""; // small img, fast load


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

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

        root = inflater.inflate(R.layout.fragment_four_tab, container, false);

//        addImageBtn = root.findViewById(R.id.imageAddBtn); // кнопка добавления фото

        try {
            url = new URL("https://pixabay.com/api/?key="+API_KEY+"&q="+REQUEST+"&image_type=photo&per_page="+COUNT); // ссылка АПИ
            new LoadImage("LoadImage").start(); // загрузка страницы в отдельном потоке
        } catch (Exception e) {
            e.printStackTrace();
        }

//        addImageBtn.setOnClickListener(new View.OnClickListener() { // при нажатии на кнопку "добавить"
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, Pick_image);
//            }
//        });

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

            ImageView image1 = row.findViewById(R.id.imageListItem1); // объекты изобажений паттерна
            ImageView image2 = row.findViewById(R.id.imageListItem2);
            ImageView image3 = row.findViewById(R.id.imageListItem3);
            ImageView image4 = row.findViewById(R.id.imageListItem4);
            ImageView image5 = row.findViewById(R.id.imageListItem5);
            ImageView image6 = row.findViewById(R.id.imageListItem6);
            ImageView image7 = row.findViewById(R.id.imageListItem7);
            ImageView image8 = row.findViewById(R.id.imageListItem8);
            ImageView image9 = row.findViewById(R.id.imageListItem9);

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

            int photosNum = imagesToShow.get(position).size();
            for (int i=0; i<9; i++){
                try {
                    if (i<photosNum)
                        new DownloadImageTask(imageViews.get(i)).execute(imagesToShow.get(position).get(i)); // установить изображение из ссылки
                    else imageViews.get(i).setImageResource(R.drawable.white_background); // если кол-во элементов не кратно 9 - заполнить белым
                } catch (Exception e){}
            }

            return row;
        }

        public String handle(String str){ // обработчик строки
            if(str.equals("")) return "None"; // если не задан любой из параметров
            else return str;
        }
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { // загрузка пользовательского фото
//        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
//
//        if (requestCode == Pick_image & imageReturnedIntent!=null) {
//            if (resultCode == RESULT_OK) {
//                try {
//                    final Uri imageUri = imageReturnedIntent.getData(); // получить URI изображения
//                    final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri); // получить стрим
//                    Bitmap selectedImage1 = BitmapFactory.decodeStream(imageStream); // преобразовать в битмап
//
//                    String newImageName = "image_"+(random.nextInt(99999)+100) + ".png";
//
//                    if (images != null){ //если список изображений не null
//                        if (images.size()==0){ // если список только инициализирован и пустой
//                            List<String> tempImageList = new ArrayList<>();
//                            images.add(tempImageList);
//                        }
//                        if (images.get(images.size()-1).size()>=9){ // если в последнем элементе списка не меньше девяти элементов
//                            List<String> tempImageList = new ArrayList<>();
//                            tempImageList.add(newImageName);
//                            images.add(tempImageList);
//                        }
//                        else { // если меньше девяти элементов
//                            images.get(images.size()-1).add(newImageName);
//                        }
//                    }
//
//                    ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
//                    selectedImage1.compress(Bitmap.CompressFormat.JPEG, 80, bos2);
//                    byte[] bitmapdata = bos2.toByteArray();
//                    File imageFile = new File(getContext().getFilesDir(), newImageName);
//
//                    try {
//                        FileOutputStream fos = new FileOutputStream(imageFile);
//                        fos.write(bitmapdata);
//                        fos.flush();
//                        fos.close();
//                        requireActivity().recreate(); // обновить ЛистВью, если загрузка успешна
//
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    jsonHelperImages.exportToJSON(getContext(), images);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    class LoadImage extends Thread {
        LoadImage(String name){
            super(name);
        }

        public void run(){
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(url.openStream())); // получаем текст АПИ(json text)
                String inputLine;
                String json = "";
                while (true) {
                    if ((inputLine = in.readLine()) == null) break;
                    json += inputLine;
                }

                List<String> urls = new ArrayList<>();

                String S[] = json.split(imageUrlTarget);
                for (String str : S){
                    if (str.substring(0, 4).equals("http")){
                        urls.add(str.split("\",\"")[0]);
                    }
                }
                try {
                    images.clear();
                }catch (Exception e){}

                for (String currentUrl : urls){
                    if (images != null){ //если список изображений не null
                        if (images.size()==0){ // если список только инициализирован и пустой
                            List<String> tempImageList = new ArrayList<>();
                            images.add(tempImageList);
                        }
                        if (images.get(images.size()-1).size()>=9){ // если в последнем элементе списка не меньше девяти элементов
                            List<String> tempImageList = new ArrayList<>();
                            tempImageList.add(currentUrl);
                            images.add(tempImageList);
                        }
                        else { // если меньше девяти элементов
                            images.get(images.size()-1).add(currentUrl);
                        }
                    }
                }

                in.close();

                listView = root.findViewById(R.id.imagesList); // получить лисьВью

                getActivity().runOnUiThread(new Runnable() { // перенос установки адаптера в основной поток
                    @Override
                    public void run() {
                        if(images != null){
                            adapter = new ImageAdapter(getActivity(), R.layout.images_list, images); // объект кастомного адаптера

                            listView.setAdapter(adapter); // установка адаптера
                        }
                        else{
                            Toast.makeText(getContext(), "Failed to get data", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> { // получить изображение из ссылки
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
