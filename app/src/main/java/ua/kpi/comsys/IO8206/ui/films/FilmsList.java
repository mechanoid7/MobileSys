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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import ua.kpi.comsys.IO8206.AddFilmActivity;
import ua.kpi.comsys.IO8206.Film;
import ua.kpi.comsys.IO8206.JsonHelperFilms;
import ua.kpi.comsys.IO8206.R;
import ua.kpi.comsys.IO8206.ui.FilmDetail;
import ua.kpi.comsys.IO8206.ui.images.JsonHelperImages;

public class FilmsList extends Fragment {
    private List<Film> films;
    private List<Film> searchedFilms = new ArrayList<>();
    private List<Film> filmsToShow = new ArrayList<>();
    private FilmAdapter adapter;
    ListView listView;
    Boolean elemAddOnStop = false; // добавление элемента и необходимо обновить список
    Boolean searchMode = false; // режим поиска по списку
    Boolean filmsApiGet = false; // фильмы получены мз сайта
    String userFileMovie =  "movieslistuser.txt"; // стандартное значение, которое заменится
    String freeSpace = (new String(new char[100]).replace("\0", "\t"));
    Film removedElement=null;
    String API_KEY = "ed68b378";
    String REQUEST_FILM_NAME;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
//        adapter.notifyDataSetChanged();
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
        if(elemAddOnStop){ // если до этого был стоп(открыто окно добавления)
            requireActivity().recreate(); // обновить ЛистВью
            elemAddOnStop = false;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_third_tab, container, false);
        JsonHelperFilms jsonHelperFilms = new JsonHelperFilms();
        jsonHelperFilms.setFileUserName("searched_list.txt");
        films = jsonHelperFilms.importFilmListFromJSON(getContext());

        EditText searchRequest = root.findViewById(R.id.filmSearchField); // поле поиска

        Button searchBtn = root.findViewById(R.id.buttonSearch); // кнопка поиска
        FloatingActionButton addFilmBtn = root.findViewById(R.id.imageAddBtn); // кнопка добавления

        listView = root.findViewById(R.id.filmsList);

        if(films != null){
            adapter = new FilmAdapter(getActivity(), R.layout.films_list, films);

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

        addFilmBtn.setOnClickListener(new View.OnClickListener() { // при нажатии на кнопку "добавить"
            public void onClick(View view) {
                elemAddOnStop = true;
                startActivity(new Intent(getContext(), AddFilmActivity.class).putExtra("moviesListId", R.raw.movieslist));
            }
        });


        searchBtn.setOnClickListener(new View.OnClickListener() { // при нажатии на кнопку "поиск"
            public void onClick(View view) {

                String fieldText = searchRequest.getText().toString().toLowerCase(); // получение текста из поля поиска
                FilmAdapter adapter2;
//                searchedFilms.clear();
                films.clear();

//                if (fieldText.equals("!reset")){ // сброс пользовательский изменений
//                    File userFile = new File(view.getContext().getFilesDir() + "/" + userFileMovie);
//
//                    JsonHelperImages jsonHelperImages = new JsonHelperImages(); // экземпляр класса фотографий для сброса
//                    jsonHelperImages.setFileUserName("images_list.txt");
//                    List<List<String>> images= jsonHelperImages.importStringListFromJSON(getContext());
//                    for (int i = 0; i < images.size(); i++) {
//                        for (int j = 0; j < images.get(i).size(); j++) {
//                            try {
//                                File toDelete = new File(getContext().getFilesDir() + "/"+images.get(i).get(j)); //удаление загруженных фото
//                                toDelete.delete();
//                            } catch (Exception e){}
//
//                        }
//                    }
//
//                    jsonHelperImages.exportToJSON(getContext(), new ArrayList<>()); // обнуление списка фотографий
//
//
//                    try(FileWriter writer = new FileWriter(userFile)){
//                        jsonHelperFilms.setUserFileEnable(false);
//                        writer.write(jsonHelperFilms.getStringFromRawFile(getContext())); // запись в файл юзерспейса JSON`а
//                        writer.flush();
//                    }
//                    catch(IOException ex){
//                        ex.printStackTrace();
//                    }
//                    getActivity().recreate();
//                    Toast.makeText(getContext(), "User lists has been reset", Toast.LENGTH_LONG).show();
//                    adapter2 = new FilmAdapter(getActivity(), R.layout.films_list, films); // адаптер с стандартным списком
//                }

                if(!fieldText.equals("") & fieldText.length()>=3){ // если поле не пустое и больше трёх
                    searchMode = true;

                    try {
                        REQUEST_FILM_NAME = fieldText;
                        filmsApiGet = false;

                        Ion.with(getContext()).load("http://www.omdbapi.com/?apikey="+API_KEY+"&s="+REQUEST_FILM_NAME+"&page=1").asString().setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                System.out.println(result);
                                searchedFilms = jsonHelperFilms.importFilmListFromString(result);
                                System.out.println("FILMS SIZE1: "+searchedFilms.size());

                                jsonHelperFilms.exportToJSON(getContext(), searchedFilms);
//                                jsonHelperFilms.exportStringToJSON(getContext(), result);

                                FilmAdapter adapter3 = new FilmAdapter(getActivity(), R.layout.films_list, searchedFilms); // адаптер с новыми фильмами
                                listView.setAdapter(adapter3);
                                filmsApiGet = true;


                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        adapter2 = new FilmAdapter(getActivity(), R.layout.films_list, new ArrayList<>()); // пустой адаптер
                    }
                }
                else {

                    searchMode = false;
                    adapter2 = new FilmAdapter(getActivity(), R.layout.films_list, films); // адаптер с стандартным списком
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
//                Log.e("Error", e.getMessage());
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
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { // переопределение
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.films_list, parent, false);

            TextView title = (TextView) row.findViewById(R.id.filmTitle); // связь данных и ИД слоя
            TextView year = (TextView) row.findViewById(R.id.filmReleasedDetail);
            TextView type = (TextView) row.findViewById(R.id.filmType);

            title.setText(handle(filmsToShow.get(position).getTitle())); // запись всех параметров
            year.setText("Year: " + handle(filmsToShow.get(position).getYear()));
            type.setText("Type: " + handle(filmsToShow.get(position).getType())+" "+freeSpace);

            ImageView iconImageView = (ImageView) row.findViewById(R.id.poster);

            String posterUrl = filmsToShow.get(position).getPoster();

            try {
                new DownloadImageTask(iconImageView).execute(posterUrl); // устанавливаем изображение
            } catch (Exception e){}
            return row;
        }

        public String handle(String str){ // обработчик строки
            if(str.equals("")) return "None"; // если не задан любой из параметров
            else return str;

        }
    }
}