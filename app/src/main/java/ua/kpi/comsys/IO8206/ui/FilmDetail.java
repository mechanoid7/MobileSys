package ua.kpi.comsys.IO8206.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import ua.kpi.comsys.IO8206.Film;
import ua.kpi.comsys.IO8206.JsonHelperFilms;
import ua.kpi.comsys.IO8206.R;
import ua.kpi.comsys.IO8206.ui.films.FilmsList;

public class FilmDetail extends AppCompatActivity {
    Film film;
    String imbdId;
    String API_KEY = "ed68b378";
    Context context = FilmDetail.this;
    URL url;
    JsonHelperFilms jsonHelperFilms = new JsonHelperFilms();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.film_info);

        Bundle arguments = getIntent().getExtras();
        imbdId = arguments.getString("filmImdbId");

        //        jsonHelperFilms.setUserFileEnable(false);

        System.out.println("IMDBID: "+imbdId);

        try {
            url = new URL("http://www.omdbapi.com/?apikey="+API_KEY+"&i="+imbdId);
//            Ion.with(this).load(url).asString().setCallback(new FutureCallback<String>() {
//                @Override
//                public void onCompleted(Exception e, String result) {
//                    System.out.println(result);
//                    film = jsonHelperFilms.importFilmFromString(result);
//                    System.out.println("FILM: "+film.toString());
//
////                    jsonHelperFilms.exportToJSON(getContext(), searchedFilms);
////                                jsonHelperFilms.exportStringToJSON(getContext(), result);
//
////                    FilmsList.FilmAdapter adapter3 = new FilmsList.FilmAdapter(getActivity(), R.layout.films_list, searchedFilms); // адаптер с новыми фильмами
////                    listView.setAdapter(adapter3);
//                }
//            });

//            URL oracle = new URL("http://www.oracle.com/");
            new LoadStr("LoadStr").start();


        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println(film);
//
//        if(film!=null) {
//            film = jsonHelperFilms.importFilmFromJSON(this); // если есть такой ИД
//            TextView title = findViewById(R.id.filmTitleDetail);
//            TextView releasedDate = findViewById(R.id.filmReleasedDetail);
//            TextView rated = findViewById(R.id.filmRatedDetail);
//            TextView runtime = findViewById(R.id.filmRuntimeDetail);
//            TextView genre = findViewById(R.id.filmGenreDetail);
//            TextView imdbRating = findViewById(R.id.filmImdbRatingDetail);
//            TextView imdbVotes = findViewById(R.id.filmImdbVotesDetail);
//            TextView production = findViewById(R.id.filmProductionDetail);
//            TextView type = findViewById(R.id.filmTypeDetail);
//            TextView director = findViewById(R.id.filmDirectorDetail);
//            TextView writer = findViewById(R.id.filmWriterDetail);
//            TextView language = findViewById(R.id.filmLanguageDetail);
//            TextView country = findViewById(R.id.filmCountryDetail);
//            TextView awards = findViewById(R.id.filmAwardsDetail);
//            TextView actors = findViewById(R.id.filmActorsDetail);
//            TextView plot = findViewById(R.id.filmPlotDetail);
//
//            ImageView poster = (ImageView) findViewById(R.id.filmPosterDetail);
//
////            int img;
////            try {
////                String posterName = film.getPoster().replaceAll(".jpg","").toLowerCase();
////                img = getResources().getIdentifier(posterName, "drawable", getPackageName()); // поиск ИД по имени
////            } catch (Exception e){img = 0;};
//
//
////            if(img!=0) poster.setImageResource(img); // если есть такой ИД
////            else poster.setImageResource(R.drawable.kpi_logo); // стандартная картинка
//
//            title.setText(film.getTitle());
//            releasedDate.setText("Released: "+film.getReleased());
//            rated.setText("Rated: "+film.getRated());
//            runtime.setText("Runtime: "+film.getRuntime());
//            genre.setText("Genre: "+film.getGenre());
//            imdbRating.setText("IMDB: "+film.getImdbRating());
//            imdbVotes.setText("("+film.getImdbVotes()+")");
//            production.setText("Production: "+film.getProduction());
//            type.setText("Type: "+film.getType());
//            director.setText("Director: "+film.getDirector());
//            writer.setText("Writer: "+film.getWriter());
//            language.setText("Language: "+film.getLanguage());
//            country.setText("Country: "+film.getCountry());
//            awards.setText("Awards: "+film.getAwards());
//            actors.setText("Actors: "+film.getActors());
//            plot.setText(film.getPlot());
//
//            String posterUrl = film.getPoster();
//
//            try {
//                new DownloadPosterTask(poster).execute(posterUrl); // устанавливаем изображение
//            } catch (Exception e){}
//
//        }
//        else {Toast.makeText(this, "Information not found", Toast.LENGTH_LONG).show(); finish();}
    }

//    public void setFilm(Film film) {
//        this.film = film;
//    }

    private class DownloadPosterTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadPosterTask(ImageView bmImage) {
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

        class LoadStr extends Thread {
        LoadStr(String name){
            super(name);
        }

        public void run(){
            BufferedReader in = null;
            try{
                in = new BufferedReader(new InputStreamReader(url.openStream()));
                String inputLine;
                String json = "";
                while (true) {
                        if ((inputLine = in.readLine()) == null) break;
//                    System.out.println(inputLine);
                    json+=inputLine;
                }

                film = jsonHelperFilms.importFilmFromString(json);

                System.out.println("FILM: "+ film);

                in.close();

                if(film!=null) {
//                    film = jsonHelperFilms.importFilmFromJSON(context); // если есть такой ИД
                    System.out.println("PLOT: "+film.getPlot());

                    TextView title = findViewById(R.id.filmTitleDetail);
                    TextView releasedDate = findViewById(R.id.filmReleasedDetail);
                    TextView rated = findViewById(R.id.filmRatedDetail);
                    TextView runtime = findViewById(R.id.filmRuntimeDetail);
                    TextView genre = findViewById(R.id.filmGenreDetail);
                    TextView imdbRating = findViewById(R.id.filmImdbRatingDetail);
                    TextView imdbVotes = findViewById(R.id.filmImdbVotesDetail);
                    TextView production = findViewById(R.id.filmProductionDetail);
                    TextView type = findViewById(R.id.filmTypeDetail);
                    TextView director = findViewById(R.id.filmDirectorDetail);
                    TextView writer = findViewById(R.id.filmWriterDetail);
                    TextView language = findViewById(R.id.filmLanguageDetail);
                    TextView country = findViewById(R.id.filmCountryDetail);
                    TextView awards = findViewById(R.id.filmAwardsDetail);
                    TextView actors = findViewById(R.id.filmActorsDetail);
                    TextView plot = findViewById(R.id.filmPlotDetail);

                    ImageView poster = (ImageView) findViewById(R.id.filmPosterDetail);

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            title.setText(film.getTitle());
                            releasedDate.setText("Released: "+film.getReleased());
                            rated.setText("Rated: "+film.getRated());
                            runtime.setText("Runtime: "+film.getRuntime());
                            genre.setText("Genre: "+film.getGenre());
                            imdbRating.setText("IMDB: "+film.getImdbRating());
                            imdbVotes.setText("("+film.getImdbVotes()+")");
                            production.setText("Production: "+film.getProduction());
                            type.setText("Type: "+film.getType());
                            director.setText("Director: "+film.getDirector());
                            writer.setText("Writer: "+film.getWriter());
                            language.setText("Language: "+film.getLanguage());
                            country.setText("Country: "+film.getCountry());
                            awards.setText("Awards: "+film.getAwards());
                            actors.setText("Actors: "+film.getActors());
                            plot.setText(film.getPlot());

                            String posterUrl = film.getPoster();

                            try {
                                new DownloadPosterTask(poster).execute(posterUrl); // устанавливаем изображение
                            } catch (Exception e){}
                        }
                    });
                }
                else {Toast.makeText(context, "Information not found", Toast.LENGTH_LONG).show(); finish();}
            } catch (Exception e){e.printStackTrace();}
        }
    }
}
