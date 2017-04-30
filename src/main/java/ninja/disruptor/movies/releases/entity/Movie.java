package ninja.disruptor.movies.releases.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.*;

@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "findByTitle", query = "SELECT m FROM Movie m WHERE m.title = :title"),
        @NamedQuery(name = "findAll", query = "SELECT m FROM Movie m"),
        @NamedQuery(name = "findAllDownloaded", query = "SELECT m FROM Movie m WHERE m.downloaded = :downloaded")
})
@Data
@Entity
@Table(name = "movies")
@ToString
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String url;
    private String cover;
    private Boolean downloaded;

    public Movie(String title, String url, String cover, Boolean downloaded) {
        this.title = title;
        this.url = url;
        this.cover = cover;
        this.downloaded = downloaded;
    }

    public Movie(Movie movie) {
        this(movie.getTitle(), movie.getUrl(), movie.getCover(), movie.getDownloaded());
    }

    public Movie(JsonObject json) {
        this.id = json.getJsonNumber("id").longValue();
        this.title = json.getString("title");
        this.url = json.getString("url");
        this.cover = json.getString("cover");
        this.downloaded = json.getBoolean("downloaded");
    }

    public JsonObject toJson() {
        return Json
                .createObjectBuilder()
                .add("id", id)
                .add("title", title)
                .add("url", url)
                .add("cover", cover)
                .add("downloaded", downloaded)
                .build();
    }
}
