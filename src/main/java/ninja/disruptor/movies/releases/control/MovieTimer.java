package ninja.disruptor.movies.releases.control;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import ninja.disruptor.movies.releases.entity.Movie;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class MovieTimer {

    @Inject
    MovieExtractor caller;

    @PersistenceContext
    EntityManager em;

    @PostConstruct
    public void setUp() {
        callSites();
    }

    @Schedule(hour = "*/12")
    public void callSites() {
        List<Movie> movies = new ArrayList<>();
        movies.addAll(caller.getTopBewertet30Days());
        movies.addAll(caller.getTopVerleih30Days());
        movies.addAll(caller.getTopALaCarte30Days());
        movies.addAll(caller.getTopVerleih12Monate());

        for (Movie movie : movies) {
            List<Movie> dbMovies = em.createNamedQuery("findByTitle", Movie.class)
                    .setParameter("title", movie.getTitle())
                    .getResultList();
            if (dbMovies.isEmpty()) {
                em.merge(movie);
                callBot(movie.getUrl());
            }
        }
    }

    private void callBot(String text) {
        SlackSession session = SlackSessionFactory.createWebSocketSlackSession(System.getenv("SLACK_TOKEN"));
        try {
            session.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SlackChannel channel = session.findChannelByName("general"); //make sure bot is a member of the channel.
        session.sendMessage(channel, text);
    }

}
