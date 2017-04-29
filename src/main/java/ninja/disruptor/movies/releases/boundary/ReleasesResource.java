package ninja.disruptor.movies.releases.boundary;

import com.airhacks.porcupine.execution.boundary.Dedicated;
import ninja.disruptor.movies.releases.control.MovieExtractor;
import ninja.disruptor.movies.releases.entity.Movie;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Path("releases")
public class ReleasesResource {

    @Dedicated
    @Inject
    ExecutorService releases;

    @Inject
    MovieExtractor extractor;

    @Inject
    ReleasesService service;

    @PersistenceContext
    EntityManager em;

    @PUT
    public void updateEntry(@Suspended AsyncResponse response, JsonObject movie) {
        supplyAsync(() -> em.merge(new Movie(movie)))
                .thenApply(response::resume);
    }

    @Path("{id}/downloaded")
    @PUT
    public void update(@Suspended AsyncResponse response, Boolean downloaded, @PathParam("id") Long id) {
        supplyAsync(() -> service.updateDownloaded(id, downloaded))
                .thenAccept(response::resume);
    }

    @GET
    public void getReleases(@Suspended AsyncResponse response) {
        supplyAsync(() -> getMoviesAsJson(
                em.createNamedQuery("findAll", Movie.class)
                        .getResultList()))
                .thenApply(response::resume);
    }

    @GET
    @Path("videobuster")
    public void searchReleases(@Suspended AsyncResponse response) {
        supplyAsync(() -> getMoviesAsJson(extractor.getTop100Range30Days()))
                .thenApply(response::resume);
    }

    private JsonArray getMoviesAsJson(List<Movie> movies) {
        JsonArrayBuilder movieArray = Json.createArrayBuilder();
        for (Movie movie : movies) {
            movieArray.add(movie.toJson());
        }
        return movieArray.build();
    }
}
