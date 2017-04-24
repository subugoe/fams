package de.unigoettingen.sub.fams

import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.CorsHandler

/**
 * @author Ingo Pfennigstorf <i.pfennigstorf@gmail.com>
 */
class Server {

    static void start() {

        def vertx = Vertx.vertx()

        def logger = LoggerFactory.getLogger('processing')

        def server = vertx.createHttpServer()
        def router = Router.router(vertx)
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST))

        def route = router.route(HttpMethod.GET, '/pdf/:id')

        route.handler({ routingContext ->
            def id = (String) routingContext.request().getParam('id')
            def files = new FileSystemVerticle(id)

            def state = new JsonObject(files.getMetadata(id))

            vertx.eventBus().consumer('process', { s ->
                logger.info(s.body())
            })

            def response = routingContext.response()
            response.putHeader('content-type', 'application/json')

            response.end(state.encodePrettily())
        })

        def port = System.getenv('PORT').toInteger() > 0 ? System.getenv('PORT').toInteger() : 8080

        server.requestHandler(router.&accept).listen(port)
    }
}
