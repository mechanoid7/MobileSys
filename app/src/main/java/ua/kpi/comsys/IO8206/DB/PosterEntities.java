package ua.kpi.comsys.IO8206.DB;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "poster")
public class PosterEntities {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String url, fileName;

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getFileName() {
        return fileName;
    }
}