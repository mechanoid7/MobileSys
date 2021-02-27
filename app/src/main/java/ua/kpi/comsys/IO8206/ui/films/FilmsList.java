package ua.kpi.comsys.IO8206.ui.films;

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

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import ua.kpi.comsys.IO8206.Film;
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

//        addFilmBtn.setOnClickListener(new View.OnClickListener() { // при нажатии на кнопку "добавить"
//            public void onClick(View view) {
//                elemAddOnStop = true;
//                startActivity(new Intent(getContext(), AddFilmActivity.class).putExtra("moviesListId", R.raw.movieslist));
//            }
//        });


        searchBtn.setOnClickListener(new View.OnClickListener() { // при нажатии на кнопку "поиск"
            public void onClick(View view) {

                String fieldText = searchRequest.getText().toString().toLowerCase(); // получение текста из поля поиска
                FilmAdapter adapter2;
                if (films!=null)
                    films.clear();

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

                                jsonHelperFilms.exportToJSON(getContext(), searchedFilms);

                                FilmAdapter adapter3 = new FilmAdapter(getActivity(), R.layout.films_list, searchedFilms); // адаптер с новыми фильмами
                                try {
                                    listView.setAdapter(adapter3);
                                } catch (Exception e2){}
                                filmsApiGet = true;
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        adapter2 = new FilmAdapter(getActivity(), R.layout.films_list, new ArrayList<>()); // пустой адаптер
                    }
                }
                else {
                    Toast.makeText(getContext(), "Uncorrected request", Toast.LENGTH_LONG).show();
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

            ImageView iconImageView = null;
            String posterUrl = "";
            try {
                title.setText(handle(filmsToShow.get(position).getTitle())); // запись всех параметров
                year.setText("Year: " + handle(filmsToShow.get(position).getYear()));
                type.setText("Type: " + handle(filmsToShow.get(position).getType())+" "+freeSpace);

                iconImageView = (ImageView) row.findViewById(R.id.poster);

                posterUrl = filmsToShow.get(position).getPoster();
            } catch (Exception e){}

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
}