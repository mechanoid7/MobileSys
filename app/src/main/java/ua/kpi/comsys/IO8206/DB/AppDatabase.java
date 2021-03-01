package ua.kpi.comsys.IO8206.DB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {FilmEntities.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FilmDao filmDao();
}
