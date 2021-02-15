package ua.kpi.comsys.IO8206;

public class Film {
    private String Title, Year, Type, imdbID, Poster;

    public Film(String title, String year, String Type, String imdbID, String poster) {
        this.Title = title;
        this.Year = year;
        this.Type = Type;
        this.imdbID = imdbID;
        this.Poster = poster;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        this.Year = year;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getPoster() {
        return Poster;
    }

    public void setPoster(String poster) {
        this.Poster = poster;
    }

    @Override
    public String toString() {
//        return "Films{" +
//                "title='" + Title + '\'' +
//                ", year='" + Year + '\'' +
//                ", contentType='" + Type + '\'' +
//                ", imdb='" + imdbID + '\'' +
//                ", poster='" + Poster + '\'' +
//                '}';
        return  Title + '\n' +
                Year + '\n' +
                "Type: " + Type;
    }
}
