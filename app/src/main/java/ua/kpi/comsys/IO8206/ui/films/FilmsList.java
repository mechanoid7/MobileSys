package ua.kpi.comsys.IO8206.ui.films;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ua.kpi.comsys.IO8206.DB.App;
import ua.kpi.comsys.IO8206.DB.AppDatabase;
import ua.kpi.comsys.IO8206.DB.FilmEntities;
import ua.kpi.comsys.IO8206.DB.ImageDao;
import ua.kpi.comsys.IO8206.DB.ImageEntities;
import ua.kpi.comsys.IO8206.Film;
import ua.kpi.comsys.IO8206.DB.FilmDao;
import ua.kpi.comsys.IO8206.JsonHelperFilms;
import ua.kpi.comsys.IO8206.R;
import ua.kpi.comsys.IO8206.ui.FilmDetail;

public class FilmsList extends Fragment {
    private List<Film> films;
    private List<Film> searchedFilms = new ArrayList<>();
    private List<Film> filmsToShow = new ArrayList<>();
    private FilmAdapter adapter;
    ListView listView;
    Boolean elemAddOnStop = false; // добавление элемента и необходимо обновить список
    Boolean searchMode = false; // режим поиска по списку
    Boolean filmsApiGet = false; // фильмы получены мз сайта
//    String userFileMovie =  "movieslistuser.txt"; // стандартное значение, которое заменится
    String freeSpace = (new String(new char[100]).replace("\0", "\t"));
    Film removedElement=null;
    String API_KEY = "ed68b378";
    String REQUEST_FILM_NAME;
    static AppDatabase db = App.getInstance().getDatabase(); // обьект базы данных
    FilmDao filmDao = db.filmDao(); // экземпляр с методами работы с БД
    static ImageDao imageDao = db.imageDao(); // экземпляр с методами работы с БД
    String searchRequest = "";
    JsonHelperFilms jsonHelperFilms;
    Random random;
    String posterUrl = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(elemAddOnStop){ // если до этого был стоп(открыто окно добавления)
            requireActivity().recreate(); // обновить ЛистВью
            elemAddOnStop = false;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_third_tab, container, false);
        jsonHelperFilms = new JsonHelperFilms();
        jsonHelperFilms.setFileUserName("searched_list.txt");
        films = jsonHelperFilms.importFilmListFromJSON(getContext());


        EditText searchRequest = root.findViewById(R.id.filmSearchField); // поле поиска

        Button searchBtn = root.findViewById(R.id.buttonSearch); // кнопка поиска
        FloatingActionButton addFilmBtn = root.findViewById(R.id.imageAddBtn); // кнопка добавления

        listView = root.findViewById(R.id.filmsList);

        if(films != null){
            adapter = new FilmAdapter(getActivity(), R.layout.films_list, films);
//            new Thread(task).start();

            listView.setAdapter(adapter);
            Toast.makeText(getContext(), "Loaded", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getContext(), "Failed to get data", Toast.LENGTH_LONG).show();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // нажатие на элемент списка
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                Toast.makeText(getContext(), filmsToShow.get((int)id).getTitle(),
                        Toast.LENGTH_SHORT).show();

                startActivity(new Intent(getContext(), FilmDetail.class).putExtra("filmImdbId", filmsToShow.get((int)id).getImdbID()));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()  { // долгое нажатие на элемент списка
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View itemClicked, int position,
                                           long id) {
                if(!searchMode) {
                    itemClicked.setBackgroundResource(R.color.light_red); // выделить элемент
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Deleting");
                        builder.setMessage("Do you want to delete this movie?");
                        builder.setCancelable(true);
                        builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // закрыть диалог
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                itemClicked.setBackgroundResource(R.color.white); // вернуть цвет
                            }
                        });

                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { // Кнопка YES
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removedElement = films.remove((int) id); // удалить выбранный элемент
                                adapter.notifyDataSetChanged(); // обновить окно
                                jsonHelperFilms.exportToJSON(getContext(), films);
                                elemAddOnStop = true;
                                dialog.dismiss(); // Отпускает диалоговое окно
                            }

                        });
                        AlertDialog dialog = builder.create();
                        dialog.show(); // показать диалог

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Deleting error", Toast.LENGTH_LONG).show();
                    }
                }
                else Toast.makeText(getContext(), "To delete. you must leave the search mode", Toast.LENGTH_LONG).show();
                return true;
            }
        });

//        addFilmBtn.setOnClickListener(new View.OnClickListener() { // при нажатии на кнопку "добавить"
//            public void onClick(View view) {
//                elemAddOnStop = true;
//                startActivity(new Intent(getContext(), AddFilmActivity.class).putExtra("moviesListId", R.raw.movieslist));
//            }
//        });


        searchBtn.setOnClickListener(new View.OnClickListener() { // при нажатии на кнопку "поиск"
            public void onClick(View view) {

                FilmsList.this.searchRequest = searchRequest.getText().toString().toLowerCase(); // получение текста из поля поиска
//                FilmAdapter adapter2;
                if (films!=null)
                    films.clear();

                if(!FilmsList.this.searchRequest.equals("") & FilmsList.this.searchRequest.length()>=3){ // если поле не пустое и больше трёх
                    searchMode = true;

                    try {
                        REQUEST_FILM_NAME = FilmsList.this.searchRequest;
                        filmsApiGet = false;
                        REQUEST_FILM_NAME = REQUEST_FILM_NAME.replace(" ", "+");

                        while (REQUEST_FILM_NAME.startsWith("+")) REQUEST_FILM_NAME = REQUEST_FILM_NAME.substring(1); // вырезаем пробелы(+) из начала строки
                        while (REQUEST_FILM_NAME.endsWith("+")) REQUEST_FILM_NAME = REQUEST_FILM_NAME.substring(0, REQUEST_FILM_NAME.length()-2); // вырезаем пробелы(+) из конца строки

                        System.out.println("REQUEST: "+ REQUEST_FILM_NAME);

                        new SearchHandler("handle").start();

                    } catch (Exception e) {
                        e.printStackTrace();
//                        adapter2 = new FilmAdapter(getActivity(), R.layout.films_list, new ArrayList<>()); // пустой адаптер
                    }
                }
                else {
                    Toast.makeText(getContext(), "Uncorrected request", Toast.LENGTH_LONG).show();
                    searchMode = false;
//                    adapter2 = new FilmAdapter(getActivity(), R.layout.films_list, films); // адаптер с стандартным списком
                };

            }
        });
        return root;
    }

//    class UpdateAdapter extends Thread {
//        Activity activityGetted;
//        UpdateAdapter(Activity activity){
//            activityGetted = activity;
//        }
//
//        public void run(){
//            while (!filmsApiGet){
//                try{Thread.sleep(250);}
//                catch(InterruptedException e){}
//            }
//            activityGetted.recreate();
//        }
//    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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

    private class FilmAdapter extends ArrayAdapter<Film>{ // свой адаптер
        FilmAdapter(Context context, int textViewResourceId, List<Film> objects) {
            super(context, textViewResourceId, objects);
            filmsToShow = objects; // список найденых фильмов
            System.out.println(">>>>>CALL ADAPTER");

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { // переопределение
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.films_list, parent, false);
            TextView title = (TextView) row.findViewById(R.id.filmTitle); // связь данных и ИД слоя
            TextView year = (TextView) row.findViewById(R.id.filmReleasedDetail);
            TextView type = (TextView) row.findViewById(R.id.filmType);
            
            title.setText(handle(filmsToShow.get(position).getTitle())); // установка параметров
            year.setText("Year: " + handle(filmsToShow.get(position).getYear()));
            type.setText("Type: " + handle(filmsToShow.get(position).getType())+" "+freeSpace);


            try {
                ImageView iconImageView = (ImageView) row.findViewById(R.id.poster);
                posterUrl = filmsToShow.get(position).getPoster(); // адрес изображения

                ImageHandler handler = new ImageHandler(iconImageView, getActivity(), posterUrl, position, getContext());
                Thread thread = new Thread(handler);
                thread.start();
//                thread.join();

                System.out.println(">>>CHECK handler IN");
            } catch (Exception e){
                e.printStackTrace();
            }



//            Runnable task = () -> {
//
//
//
//
//
//            };

//            new Thread(task).start();
            return row;


//            ImageEntities currentImage = new ImageEntities();
//            String fileName = "";
//
//            fileName += "poster_" + imageDao.getDataCount() + "_"+ random.nextInt(9999);
//            currentImage.url = posterUrl;
//            currentImage.fileName = fileName;
//            ss
//
//            imageDao.insert(currentImage);
//
//
//            if (posterUrl.startsWith("http")){
//                try {
//                    new DownloadImageTask(iconImageView).execute(posterUrl); // устанавливаем изображение
//                } catch (Exception e){}}
//            else if (iconImageView != null) {
//                iconImageView.setImageResource(R.drawable.ic_image_not_found); // если у обьекта нет постера
//            }

        }

        public String handle(String str){ // обработчик строки
            if(str.equals("")) return "None"; // если не задан любой из параметров
            else return str;

        }
    }

    public boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) { return false; }
    }

    class SaveFilmsToDB extends Thread { // поток для сохранения фильмов в БД
        SaveFilmsToDB(String name){
            super(name);
        }

        public void run(){
            if (filmDao.getByRequest(REQUEST_FILM_NAME).size()==0){
                FilmEntities filmEntity;
                if (searchedFilms!=null)
                    for (Film currentFilm : searchedFilms){
                        filmEntity = new FilmEntities();
                        filmEntity.Title = currentFilm.getTitle();
                        filmEntity.Year = currentFilm.getYear();
                        filmEntity.Type = currentFilm.getType();
                        filmEntity.Poster = currentFilm.getPoster();
                        filmEntity.imdbID = currentFilm.getImdbID();
                        filmEntity.SearchRequest = REQUEST_FILM_NAME;
                        filmDao.insert(filmEntity);
                    }
            }
            else System.out.println("Request '"+searchRequest+"' already in DB :)");

            getActivity().runOnUiThread(new Runnable() { // этот код выполнится в основном потоке
                @Override
                public void run() {

                }
            });
        }
    }

    class SearchHandler extends Thread { // обработчик поиска
        SearchHandler(String name){
            super(name);
        }

        public void run(){
            List<FilmEntities> entityByRequest = filmDao.getByRequest(REQUEST_FILM_NAME);
            String requestUrl = "http://www.omdbapi.com/?apikey="+API_KEY+"&s="+REQUEST_FILM_NAME+"&page=1";

            if(entityByRequest.size()>0){
                System.out.println("LIST: GET FROM DB");
                searchedFilms = Film.listFilmEntitiesToListFilms(entityByRequest); // entityByRequest в List<Film>
                jsonHelperFilms.exportToJSON(getContext(), searchedFilms);
                FilmAdapter adapter3 = new FilmAdapter(getActivity(), R.layout.films_list, searchedFilms); // адаптер с новыми фильмами
                try {
                    listView.setAdapter(adapter3);
//                    adapter3.notifyDataSetChanged();
                } catch (Exception e2){}
                filmsApiGet = true;
            }
            else {
                Ion.with(getContext()).load(requestUrl).asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        System.out.println(result);
                        searchedFilms = jsonHelperFilms.importFilmListFromString(result);

                        jsonHelperFilms.exportToJSON(getContext(), searchedFilms);

                        new SaveFilmsToDB("SaveFilms").start();

                        FilmAdapter adapter3 = new FilmAdapter(getActivity(), R.layout.films_list, searchedFilms); // адаптер с новыми фильмами
                        try {
                            listView.setAdapter(adapter3);
                        } catch (Exception e2) {}
                        filmsApiGet = true;
                    }
                });
            }

            getActivity().runOnUiThread(new Runnable() { // этот код выполнится в основном потоке
                @Override
                public void run() {
                }
            });
        }
    }

//    class ImageHandler extends Thread { // обработчик изображений
//        ImageHandler(String name){
//            super(name);
//        }
//
//        public void run(){
//            ImageEntities currentImage = new ImageEntities();
//            String fileName = "";
//
//            System.out.println("URL:"+posterUrl);
//
//            fileName += "poster_" + imageDao.getDataCount() + "_"+ random.nextInt(9999);
//
//            if (posterUrl.startsWith("http")) { // если URL начинается с http
//                Boolean imageExist = false;
//                if (imageDao.getByUrl(posterUrl).size() != 0){ // файл есть в БД, проверяем наличие в Кеше
//                    String imageCachePath = getContext().getCacheDir() + "/" + imageDao.getByUrl(posterUrl).get(0).getFileName();
//                    File f = new File(imageCachePath);
//                    imageExist = f.exists();
//                }
//
//                if (imageDao.getByUrl(posterUrl).size() == 0 | !imageExist) { // если нет изображений с таким url
//                    // или отсутствует файл изображения на устройстве, но есть в БД
//                    URL urlDownload;
//                    try {
//                        urlDownload = new URL(posterUrl);
//                        InputStream input = urlDownload.openStream();
//                        try {
//                            OutputStream output = new FileOutputStream(getContext().getCacheDir() + "/" + fileName);
//                            try {
//                                byte[] buffer = new byte[2048];
//                                int bytesRead = 0;
//                                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
//                                    output.write(buffer, 0, bytesRead);
//                                }
//                            } finally {
//                                output.close();
//                            }
//                        } finally {
//                            input.close();
//                        }
//                    } catch (Exception e) { e.printStackTrace();
//                    }
//
//                    currentImage.url = posterUrl;
//                    currentImage.fileName = fileName;
//                    imageDao.insert(currentImage);
//                }
//
//                try { // пробуем установить кешированое изображение
//                    String imageNameDB = imageDao.getByUrl(posterUrl).get(0).getFileName();
//
//                    File imageFile = new File(getContext().getCacheDir() + "/" + imageNameDB); // кешированое изображение
//                    InputStream is = new FileInputStream(imageFile);
//
//                    Bitmap userImage = BitmapFactory.decodeStream(is); // фото в стрим
//                    iconImageView.setImageBitmap(userImage); // установка фото
//                } catch (Exception e) {e.printStackTrace();} // стандартная картинка
//
//            }
//            else if (iconImageView != null) { // если адресс постера начинается не с http
//                iconImageView.setImageResource(R.drawable.ic_image_not_found); // установка изображения("Отсутствует")
//            }
//
////            if (posterUrl.startsWith("http")){
////                try {
////                    new DownloadImageTask(iconImageView).execute(posterUrl); // устанавливаем изображение
////                } catch (Exception e){}}
////            else if (iconImageView != null) {
////                iconImageView.setImageResource(R.drawable.ic_image_not_found); // если у обьекта нет постера
////            }
//
//            getActivity().runOnUiThread(new Runnable() { // этот код выполнится в основном потоке
//                @Override
//                public void run() {
//                }
//            });
//        }
//    }

    public static class ImageHandler implements Runnable {
        protected ImageView imageView;
        protected Activity uiActivity;
        protected String posterUrl;
        protected Context context;
        protected int position;

        public ImageHandler(ImageView imageView, Activity uiActivity, String posterUrl, int position, Context context) {
            this.imageView = imageView;
            this.uiActivity = uiActivity;
            this.posterUrl = posterUrl;
            this.position = position;
            this.context = context;
        }

        public void run() {
            System.out.println("ENTER IN HANDLER");
            ImageEntities currentImage = new ImageEntities();

            System.out.println("Pos:"+position+"; URL:"+posterUrl);

            String fileName = "";


            if (posterUrl.startsWith("http")) { // если URL начинается с http
                int rndInt = new Random().nextInt(9999);
                fileName += "poster_" + position+ "_" + rndInt+".png";

                List<ImageEntities> daoByUrl = imageDao.getByUrl(posterUrl);

                Boolean imageExist = false;
                if (daoByUrl.size() != 0) { // файл есть в БД, проверяем наличие в Кеше
                    String imageCachePath = context.getCacheDir() + "/" + daoByUrl.get(0).getFileName();
                    imageExist = new File(imageCachePath).exists();
//                    imageExist = f.exists();
                }

                if (daoByUrl.size() == 0 | !imageExist) { // если нет изображений с таким url
                    // или отсутствует файл изображения на устройстве, но есть в БД
                    URL urlDownload;
                    try {
                        urlDownload = new URL(posterUrl);
                        InputStream input = urlDownload.openStream();
                        try {
                            OutputStream output = new FileOutputStream(context.getCacheDir() + "/" + fileName);
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

                    currentImage.url = posterUrl;
                    currentImage.fileName = fileName;
                    imageDao.insert(currentImage);
                }

                try { // пробуем установить кешированое изображение
                    String imageNameDB = imageDao.getByUrl(posterUrl).get(0).getFileName();

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
}