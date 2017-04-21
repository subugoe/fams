package de.unigoettingen.sub.fileservice

import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.CorsHandler

/**
 *
 * @author Ingo Pfennigstorf <i.pfennigstorf@gmail.com>
 */
class Server {

    static void start() {

        def vertx = Vertx.vertx()

        Logger logger = LoggerFactory.getLogger('processing')

        def server = vertx.createHttpServer()
        def router = Router.router(vertx)
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST))

        def route = router.route(HttpMethod.GET, '/pdf/:id')

        route.handler({ routingContext ->
            def id = (String) routingContext.request().getParam("id")
            def files = new FileSystemVerticle(id)

            def state = new JsonObject(files.getMetadata(id))

            vertx.eventBus().consumer('process', { s ->
                logger.info(s.body())
            })

            def response = routingContext.response()
            response.putHeader('content-type', 'application/json')

            // Write to the response and end it
            response.end(state.encodePrettily())
        })

        server.requestHandler(router.&accept).listen(8080)
    }
}
