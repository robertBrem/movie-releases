package ninja.disruptor.movies.releases.control;

import ninja.disruptor.movies.releases.entity.Movie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.ejb.Stateless;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class MovieExtractor {

    public List<Movie> getTopBewertet30Days() {
        List<Movie> movies = new ArrayList<>();
        for (int pageIndex = 1; pageIndex <= 5; pageIndex++) {
            movies.addAll(getMoviesForPage(getDocumentTopBewertet30Tage(pageIndex)));
        }
        return movies;
    }

    public List<Movie> getTopVerleih30Days() {
        List<Movie> movies = new ArrayList<>();
        for (int pageIndex = 1; pageIndex <= 5; pageIndex++) {
            movies.addAll(getMoviesForPage(getDocumentTopVerleih30Tage(pageIndex)));
        }
        return movies;
    }

    public List<Movie> getTopALaCarte30Days() {
        List<Movie> movies = new ArrayList<>();
        for (int pageIndex = 1; pageIndex <= 5; pageIndex++) {
            movies.addAll(getMoviesForPage(getDocumentALaCarte(pageIndex)));
        }
        return movies;
    }

    public List<Movie> getMoviesForPage(Document doc) {
        List<Movie> movies = new ArrayList<>();
        Elements list = doc.select(".list");
        for (Element entry : list) {
            movies.addAll(getMoviesFromListClass(entry));
        }
        return movies;
    }

    public List<Movie> getMoviesFromListClass(Element entry) {
        List<Movie> movies = new ArrayList<>();
        Elements movieEntries = entry.select("li");
        for (Element movie : movieEntries) {
            movies.add(getMovieFromLiElement(movie));
        }
        return movies;
    }

    public Movie getMovieFromLiElement(Element movie) {
        Elements title = movie.select(".title");
        String url = "https://www.videobuster.de" + title.attr("href");
        String coverFound = getCover(movie);
        return new Movie(title.html(), url, coverFound, false);
    }

    public String getCover(Element movie) {
        String coverFound = null;
        Elements cover = movie.select(".cover");
        for (Element noscript : cover) {
            Elements image = noscript.select("noscript");
            for (Element img : image) {
                for (Element i : img.select("img")) {
                    coverFound = i.attr("src");
                }
            }
        }
        return coverFound;
    }

    public Document getDocumentALaCarte(int pageIndex) {
        Document doc = null;
        try {
            doc = Jsoup
                    .connect("https://www.videobuster.de/top-alacarte.php?pospage=" + pageIndex + "&wrapped=100&search_title=&tab_search_content=movies&view=0&wrapped=100#titlelist_head")
                    .timeout(10 * 1000)
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return doc;
    }

    public Document getDocumentTopVerleih30Tage(int pageIndex) {
        Document doc = null;
        try {
            doc = Jsoup
                    .connect("https://www.videobuster.de/top-dvd-verleih-30-tage.php?pospage=" + pageIndex + "&wrapped=100&search_title=&tab_search_content=movies&view=0&wrapped=100#titlelist_head")
                    .timeout(10 * 1000)
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return doc;
    }

    public Document getDocumentTopBewertet30Tage(int pageIndex) {
        Document doc = null;
        try {
            doc = Jsoup
                    .connect("https://www.videobuster.de/top-bewertet-30-tage.php?pospage=" + pageIndex + "&wrapped=100&search_title=&tab_search_content=movies&view=0&wrapped=100#titlelist_head")
                    .timeout(10 * 1000)
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return doc;
    }

}
