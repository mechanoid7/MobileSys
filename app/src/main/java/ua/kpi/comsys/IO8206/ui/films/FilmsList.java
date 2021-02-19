package ua.kpi.comsys.IO8206.ui.films;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.ls.LSOutput;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ua.kpi.comsys.IO8206.AddFilmActivity;
import ua.kpi.comsys.IO8206.Film;
import ua.kpi.comsys.IO8206.JsonHelper;
import ua.kpi.comsys.IO8206.R;
import ua.kpi.comsys.IO8206.ui.FilmDetail;

public class FilmsList extends Fragment {
    private List<Film> films;
    private List<Film> searchedFilms = new ArrayList<>();
    private List<Film> filmsToShow = new ArrayList<>();
    private FilmAdapter adapter;
    ListView listView;
    Boolean elemAddOnStop = false;

    Film removedElement=null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
//        adapter.notifyDataSetChanged();
        elemAddOnStop = true;
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
        JsonHelper jsonHelper = new JsonHelper(R.raw.movieslist);
        jsonHelper.setFileUserName("movieslist.txt");
        EditText searchRequest = root.findViewById(R.id.filmSearchField); // поле поиска

        Button searchBtn = root.findViewById(R.id.buttonSearch);
        FloatingActionButton addFilmBtn = root.findViewById(R.id.filmAddBtn);

        listView = root.findViewById(R.id.filmsList);
        films = jsonHelper.importFilmListFromJSON(getContext()); // берём фильмы из файла

        if(films != null){
            adapter = new FilmAdapter(getActivity(), R.layout.activity_list, films);

            listView.setAdapter(adapter);
            Toast.makeText(getContext(), "Загружено", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getContext(), "Не удалось открыть данные", Toast.LENGTH_LONG).show();
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

                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { // Кнопка ОК
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removedElement = films.remove((int)id); // удалить выбранный элемент
                            adapter.notifyDataSetChanged(); // обновить окно
                            dialog.dismiss(); // Отпускает диалоговое окно
                        }

                    });
                    AlertDialog dialog = builder.create();
                    dialog.show(); // показать диалог

                }
                catch (Exception e){
                    Toast.makeText(getContext(), "Deleting error", Toast.LENGTH_LONG).show();
                }
                jsonHelper.exportToJSON(getContext(), films);

                return true;
            }
        });

        addFilmBtn.setOnClickListener(new View.OnClickListener() { // при нажатии на кнопку "добавить"
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddFilmActivity.class).putExtra("moviesListId", R.raw.movieslist));
//                System.out.println("AFTER ACTIVITY");
            }
        });


        searchBtn.setOnClickListener(new View.OnClickListener() { // при нажатии на кнопку "поиск"
            public void onClick(View view) {
                String fieldText = searchRequest.getText().toString().toLowerCase();
                FilmAdapter adapter2;
                searchedFilms.clear();

                if(!fieldText.equals("")){
                    for (int i = 0; i < films.size(); i++) {
                        if(films.get(i).getTitle().toLowerCase().contains(fieldText)){ // ищем совпадения в заголовках и добавляем в
                            // второй список фильмов, если есть совпадения
                            searchedFilms.add(films.get(i));
                        }
                    }

                    if(searchedFilms.isEmpty()){
                        Toast.makeText(getContext(), "Ничего не найдено :(", Toast.LENGTH_LONG).show();
                    }
                    else Toast.makeText(getContext(), "Загружено", Toast.LENGTH_LONG).show();
                    adapter2 = new FilmAdapter(getActivity(), R.layout.activity_list, new ArrayList<>(searchedFilms)); // адаптер с новыми фильмами
                }
                else {
                    adapter2 = new FilmAdapter(getActivity(), R.layout.activity_list, films); // адаптер с стандартным списком
                };
                listView.setAdapter(adapter2);
            }
        });
        return root;
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
            View row = inflater.inflate(R.layout.activity_list, parent, false);

            TextView title = (TextView) row.findViewById(R.id.filmTitle); // связь данных и ИД слоя
            TextView year = (TextView) row.findViewById(R.id.filmReleasedDetail);
            TextView type = (TextView) row.findViewById(R.id.filmType);

            title.setText(handle(filmsToShow.get(position).getTitle())); // запись всех параметров
            year.setText("Year: " + handle(filmsToShow.get(position).getYear()));
            type.setText("Type: " + handle(filmsToShow.get(position).getType()));

            ImageView iconImageView = (ImageView) row.findViewById(R.id.poster);

            String posterName = filmsToShow.get(position).getPoster();
            int res = getContext().getResources().getIdentifier(posterName.replaceAll(".jpg",
                    ""), "drawable", getContext().getPackageName()); // поиск ИД по имени

            if(res!=0) iconImageView.setImageResource(res); // если нет такого ИД
            else {
                try { // пробуем установить пользовательское изображение
                    File imageFile = new File(getContext().getFilesDir() + "/" + posterName); // пользовательское изображение
                    InputStream is = new FileInputStream(imageFile);

                    Bitmap userImage = BitmapFactory.decodeStream(is); // фото в стрим
                    iconImageView.setImageBitmap(userImage); // установка фото
                } catch (Exception e) {iconImageView.setImageResource(R.drawable.kpi_logo);} // стандартная картинка

            }
            return row;
        }

        public String handle(String str){ // обработчик строки
            if(str.equals("")) return "None"; // если не задан любой из параметров
            else return str;

        }
    }
}