package ninja.disruptor.movies.releases.control;

import ninja.disruptor.movies.releases.entity.Movie;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
        for (Movie movie : caller.getTopBewertet30Days()) {
            List<Movie> movies = em.createNamedQuery("findByTitle", Movie.class)
                    .setParameter("title", movie.getTitle())
                    .getResultList();
            if (movies.isEmpty()) {
                em.merge(movie);
            }
        }
        for (Movie movie : caller.getTopBewertet30Days()) {
            List<Movie> movies = em.createNamedQuery("findByTitle", Movie.class)
                    .setParameter("title", movie.getTitle())
                    .getResultList();
            if (movies.isEmpty()) {
                em.merge(movie);
            }
        }
        for (Movie movie : caller.getTopALaCarte30Days()) {
            List<Movie> movies = em.createNamedQuery("findByTitle", Movie.class)
                    .setParameter("title", movie.getTitle())
                    .getResultList();
            if (movies.isEmpty()) {
                em.merge(movie);
            }
        }
    }

}
