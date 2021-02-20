package ua.kpi.comsys.IO8206.ui;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ua.kpi.comsys.IO8206.Film;
import ua.kpi.comsys.IO8206.JsonHelper;
import ua.kpi.comsys.IO8206.R;

public class FilmDetail extends AppCompatActivity {
    Film film;
    String imbdId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.film_info);

        Bundle arguments = getIntent().getExtras();
        imbdId = arguments.get("filmImdbId").toString();

        int res = this.getResources().getIdentifier(imbdId, "raw", this.getPackageName()); // поиск ИД по имени

        JsonHelper jsonHelper = new JsonHelper(res);
        jsonHelper.setUserFileEnable(false);


        if(res!=0) {
            film = jsonHelper.importFilmFromJSON(this); // если есть такой ИД
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

            int img;
            try {
                String posterName = film.getPoster().replaceAll(".jpg","").toLowerCase();
                img = getResources().getIdentifier(posterName, "drawable", getPackageName()); // поиск ИД по имени
            } catch (Exception e){img = 0;};


            if(img!=0) poster.setImageResource(img); // если есть такой ИД
            else poster.setImageResource(R.drawable.kpi_logo); // стандартная картинка

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

        }
        else {Toast.makeText(this, "Information not found", Toast.LENGTH_LONG).show(); finish();}
    }

    public void setFilm(Film film) {
        this.film = film;
    }
}
