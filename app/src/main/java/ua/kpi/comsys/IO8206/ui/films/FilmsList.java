package ua.kpi.comsys.IO8206.ui.films;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.List;

import ua.kpi.comsys.IO8206.Film;
import ua.kpi.comsys.IO8206.JsonHelper;
import ua.kpi.comsys.IO8206.R;

public class FilmsList extends Fragment {
    private List<Film> films;
    private FilmAdapter adapter;
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_third_tab, container, false);

        listView = root.findViewById(R.id.filmsList);
        films = JsonHelper.importFromJSON(getContext()); // берём фильмы из файла

        if(films != null){
            adapter = new FilmAdapter(getActivity(), R.layout.activity_list, films);

            listView.setAdapter(adapter);
            Toast.makeText(getContext(), "Загружено", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getContext(), "Не удалось открыть данные", Toast.LENGTH_LONG).show();
        }
        return root;
    }

    private class FilmAdapter extends ArrayAdapter<Film>{ // свой адаптер
        FilmAdapter(Context context, int textViewResourceId, List<Film> objects) {
            super(context, textViewResourceId, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { // переопределение
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.activity_list, parent, false);
            TextView title = (TextView) row.findViewById(R.id.filmTitle); // связь данных и ИД слоя
            TextView year = (TextView) row.findViewById(R.id.filmYear);
            TextView type = (TextView) row.findViewById(R.id.filmType);
            TextView rating = (TextView) row.findViewById(R.id.filmRating);

            title.setText(handle(films.get(position).getTitle())); // запись всех параметров
            year.setText("Year: " + handle(films.get(position).getYear()));
            type.setText("Type: " + handle(films.get(position).getType()));
            rating.setText("IMBD: in dev, IMBD ID: " + handle(films.get(position).getImdbID()));

            ImageView iconImageView = (ImageView) row.findViewById(R.id.poster);

            String posterName = films.get(position).getPoster();
            int res = getContext().getResources().getIdentifier(posterName.replaceAll(".jpg",
                    ""), "drawable", getContext().getPackageName()); // поиск ИД по имени

            if(res!=0) iconImageView.setImageResource(res); // если нет такого ИД
            else iconImageView.setImageResource(R.drawable.kpi_logo); // стандартная картинка
            return row;
        }

        public String handle(String str){ // обработчик строки
            if(str.equals("")) return "None"; // если не задан любой из параметров
            else return str;

        }
    }
}