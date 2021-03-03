package ua.kpi.comsys.IO8206.DB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {FilmEntities.class, PosterEntities.class, ImageEntities.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FilmDao filmDao();
    public abstract PosterDao posterDao();
    public abstract ImageDao imageDao();
}
