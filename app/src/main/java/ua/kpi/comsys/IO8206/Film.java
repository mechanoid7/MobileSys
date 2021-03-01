package ua.kpi.comsys.IO8206;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

public class Film {
    public String Title, Year, Type, imdbID, Poster, Rated, Runtime, Genre, imdbRating, imdbVotes, Released, Production,
            Language, Country, Awards, Director, Writer, Actors, Plot;

    public Film(String title, String year, String type, String imdbID, String poster, String rated, String production,
                String runtime, String genre, String imdbRating, String imdbVotes, String language,
                String country, String awards, String director, String writer, String actors, String plot, String released) {
        this.Title = title;
        this.Year = year;
        this.Type = type;
        this.imdbID = imdbID;
        this.Poster = poster;
        this.Rated = rated;
        this.Runtime = runtime;
        this.Genre = genre;
        this.imdbRating = imdbRating;
        this.imdbVotes = imdbVotes;
        this.Language = language;
        this.Country = country;
        this.Awards = awards;
        this.Director = director;
        this.Writer = writer;
        this.Actors = actors;
        this.Plot = plot;
        this.Released = released;
        this.Production = production;
    }

    public String getProduction() {
        return Production;
    }

    public void setProduction(String production) {
        Production = production;
    }

    public String getReleased() {
        return Released;
    }

    public void setReleased(String released) {
        Released = released;
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

    public String getRated() {
        return Rated;
    }

    public void setRated(String rated) {
        Rated = rated;
    }

    public String getRuntime() {
        return Runtime;
    }

    public void setRuntime(String runtime) {
        Runtime = runtime;
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        Genre = genre;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getImdbVotes() {
        return imdbVotes;
    }

    public void setImdbVotes(String imdbVotes) {
        this.imdbVotes = imdbVotes;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getAwards() {
        return Awards;
    }

    public void setAwards(String awards) {
        Awards = awards;
    }

    public String getDirector() {
        return Director;
    }

    public void setDirector(String director) {
        Director = director;
    }

    public String getWriter() {
        return Writer;
    }

    public void setWriter(String writer) {
        Writer = writer;
    }

    public String getActors() {
        return Actors;
    }

    public void setActors(String actors) {
        Actors = actors;
    }

    public String getPlot() {
        return Plot;
    }

    public void setPlot(String plot) {
        Plot = plot;
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
