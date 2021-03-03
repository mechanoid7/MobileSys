package ua.kpi.comsys.IO8206.ui.images;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ua.kpi.comsys.IO8206.DB.App;
import ua.kpi.comsys.IO8206.DB.AppDatabase;
import ua.kpi.comsys.IO8206.DB.ImageDao;
import ua.kpi.comsys.IO8206.DB.ImageEntities;
//import ua.kpi.comsys.IO8206.DB.PosterEntities;
import ua.kpi.comsys.IO8206.R;

public class ImagesList extends Fragment {
    private List<List<String>>  imagesToShow = new ArrayList<>();
    private ImageAdapter adapter;
    ListView listView;
    List<List<String>> images = new ArrayList<>(); // список ссылок на фото разбитых в группы по 9 штук
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
    static AppDatabase db = App.getInstance().getDatabase(); // обьект базы данных
    static ImageDao imageDao = db.imageDao(); // экземпляр с методами работы с БД



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
            System.out.println("CHECK1");
            for (int i=0; i<9; i++){
                try {
                    if (i<photosNum){
//                        new DownloadImageTask(imageViews.get(i)).execute(imagesToShow.get(position).get(i)); // установить изображение из ссылки
                        ImageHandlerImg handler = new ImageHandlerImg(imageViews.get(i), getActivity(), images.get(position).get(i), position, getContext());
                        Thread thread = new Thread(handler);
                        thread.start();
                    }
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

    class LoadImage extends Thread {
        LoadImage(String name){
            super(name);
        }

        public void run(){
            if (netIsAvailable()) { // есть соединение
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
                    for (String str : S) {
                        if (str.substring(0, 4).equals("http")) {
                            urls.add(str.split("\",\"")[0]);
                        }
                    }
                    try {
                        images.clear();
                    } catch (Exception e) {
                    }

                    for (String currentUrl : urls) {
                        if (images != null) { //если список изображений не null
                            if (images.size() == 0) { // если список только инициализирован и пустой
                                List<String> tempImageList = new ArrayList<>();
                                images.add(tempImageList);
                            }
                            if (images.get(images.size() - 1).size() >= 9) { // если в последнем элементе списка не меньше девяти элементов
                                List<String> tempImageList = new ArrayList<>();
                                tempImageList.add(currentUrl);
                                images.add(tempImageList);
                            } else { // если меньше девяти элементов
                                images.get(images.size() - 1).add(currentUrl);
                            }
                        }
                    }

                    System.out.println("I//SIZE: "+images.size()+"; IMAGES: "+images);

                    in.close();

                    listView = root.findViewById(R.id.imagesList); // получить лисьВью

                    getActivity().runOnUiThread(new Runnable() { // перенос установки адаптера в основной поток
                        @Override
                        public void run() {
                            if (images != null) {
                                adapter = new ImageAdapter(getActivity(), R.layout.images_list, images); // объект кастомного адаптера

                                listView.setAdapter(adapter); // установка адаптера
                            } else {
                                Toast.makeText(getContext(), "Failed to get data", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            else { // нет соединения
                System.out.println("ENTER WITHOUT CONNECTION");
                List<ImageEntities> imageEntities = imageDao.getAll();
                System.out.println("NUM OF ENTITY: "+imageEntities.size());

                for (ImageEntities currentEntity : imageEntities) {
//                    images = new ArrayList<>();
                    if (images != null) { //если список изображений не null
                        if (images.size() == 0) { // если список только инициализирован и пустой
                            List<String> tempImageList = new ArrayList<>();
                            images.add(tempImageList);
                        }
                        if (images.get(images.size() - 1).size() >= 9) { // если в последнем элементе списка не меньше девяти элементов
                            List<String> tempImageList = new ArrayList<>();
                            tempImageList.add(currentEntity.getUrl());
                            images.add(tempImageList);
                        } else { // если меньше девяти элементов
                            images.get(images.size() - 1).add(currentEntity.getUrl());
                        }
                    }
                }

                System.out.println("NI//SIZE: "+images.size()+"; IMAGES: "+images);



                getActivity().runOnUiThread(new Runnable() { // перенос установки адаптера в основной поток
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();

                        if (images != null) {
                            listView = root.findViewById(R.id.imagesList); // получить лисьВью

                            adapter = new ImageAdapter(getActivity(), R.layout.images_list, images); // объект кастомного адаптера

                            listView.setAdapter(adapter); // установка адаптера
                        } else {
                            Toast.makeText(getContext(), "Failed to get data", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

//    static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> { // получить изображение из ссылки
//        ImageView bmImage;
//
//        public DownloadImageTask(ImageView bmImage) {
//            this.bmImage = bmImage;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//        }
//    }


    public static class ImageHandlerImg implements Runnable {
        protected ImageView imageView;
        protected Activity uiActivity;
        protected String imageUrl;
        protected Context context;
        protected int position;

        public ImageHandlerImg(ImageView imageView, Activity uiActivity, String imageUrl, int position, Context context) {
            this.imageView = imageView;
            this.uiActivity = uiActivity;
            this.imageUrl = imageUrl;
            this.position = position;
            this.context = context;
        }

        public void run() {
            ImageEntities currentImage = new ImageEntities();
            System.out.println("Pos:"+position+"; URL:"+ imageUrl);
            String fileName;

            if (imageUrl.startsWith("http")) { // если URL начинается с http
                List<ImageEntities> daoByUrl = imageDao.getByUrl(imageUrl);
                String cacheDir = context.getCacheDir() + "";

                boolean imageExist = false;
                if (daoByUrl.size() != 0) { // файл есть в БД, проверяем наличие в Кеше
                    String imageCachePath = cacheDir + "/" + daoByUrl.get(0).getFileName();
                    imageExist = new File(imageCachePath).exists(); // bool есть в кеше
                    System.out.println("FILE:"+daoByUrl.get(0).getFileName()+"; Exist:"+imageExist);
                }

                if (daoByUrl.size() == 0 | !imageExist) { // если нет изображений с таким url
                    // или отсутствует файл изображения на устройстве, но есть в БД
                    if (!imageExist & daoByUrl.size()>0) // если изображение есть в БД, но нет в кеше - установка имени из БД
                        fileName = daoByUrl.get(0).getFileName();
                    else { // изображения нет в бд - генерация нового имени
                        int rndInt = new Random().nextInt(9999);
                        fileName = "image_" + position+ "_" + rndInt+".png";
                        while (true){ // установка уникального имени,
                            // ежели сгенерировано которое уже сущестует
                            if (!(new File(cacheDir + "/" + fileName).exists())) break;
                            System.out.println("WHILE lifecycle(");
                            fileName = "image_" + position+ "_" + new Random().nextInt(9999)+".png";
                        }
                    }

                    URL urlDownload;
                    try {
                        urlDownload = new URL(imageUrl);
                        InputStream input = urlDownload.openStream();
                        try {
                            OutputStream output = new FileOutputStream(cacheDir + "/" + fileName);
                            try {
                                byte[] buffer = new byte[2048];
                                int bytesRead = 0;
                                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                                    output.write(buffer, 0, bytesRead);
                                }
                            } finally {
                                output.close();
                            }
                        } finally {
                            input.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    currentImage.url = imageUrl;
                    currentImage.fileName = fileName;
                    imageDao.insert(currentImage);
                }

                try { // пробуем установить кешированое изображение
                    String imageNameDB = imageDao.getByUrl(imageUrl).get(0).getFileName();

                    File imageFile = new File(context.getCacheDir() + "/" + imageNameDB); // кешированое изображение
                    InputStream is = new FileInputStream(imageFile);

                    Bitmap userImage = BitmapFactory.decodeStream(is); // фото в стрим

                    uiActivity.runOnUiThread(new Runnable() { // этот код выполнится в основном потоке
                        @Override
                        public void run() {
                            imageView.setImageBitmap(userImage); // установка фото
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } // стандартная картинка

            } else if (imageView != null) { // если адресс постера начинается не с http
                uiActivity.runOnUiThread(new Runnable() { // этот код выполнится в основном потоке
                    @Override
                    public void run() {
                        imageView.setImageResource(R.drawable.ic_image_not_found); // установка изображения("Отсутствует")
                    }
                });
            }
        }
    }

    private static boolean netIsAvailable() {
        try {
            final URL url = new URL("http://www.google.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            System.out.println("INTERNET CONNECTION SUCCESS");
            return true;
        } catch (Exception e) {}
        System.out.println("INTERNET CONNECTION FAILED");
        return false;
    }
}
