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
        callSite();
    }

    @Schedule(hour = "*/12")
    public void callSite() {
        for (Movie movie : caller.getTop100Range30Days()) {
            List<Movie> movies = em.createNamedQuery("findByTitle", Movie.class)
                    .setParameter("title", movie.getTitle())
                    .getResultList();
            if (movies.isEmpty()) {
                em.merge(movie);
            }
        }
    }

}
