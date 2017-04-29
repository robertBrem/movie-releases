package ninja.disruptor.movies.releases.boundary;

import ninja.disruptor.movies.releases.entity.Movie;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ReleasesService {

    @PersistenceContext
    EntityManager em;

    public Movie updateDownloaded(Long id, Boolean downloaded) {
        Movie movie = em.find(Movie.class, id);
        movie.setDownloaded(downloaded);
        return em.merge(movie);
    }

}
