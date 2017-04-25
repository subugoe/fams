package de.unigoettingen.sub.fams

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.CorsHandler

/**
 * @author Ingo Pfennigstorf <i.pfennigstorf@gmail.com>
 */
class Server extends AbstractVerticle {

    @Override
    void start(Future<Void> fut) {
        def logger = LoggerFactory.getLogger(Server.class)

        def server = vertx.createHttpServer()
        def router = Router.router(vertx)
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST))

        def route = router.route(HttpMethod.GET, '/pdf/:contextId/:id')

        route.handler({ routingContext ->
            def id = (String) routingContext.request().getParam('id')
            def contextId = (String) routingContext.request().getParam('contextId')

            def state = new JsonObject(FileSystemCheck.getMetadata(id, contextId))

            if (state.getString('status') == FileSystemCheck.QUEUED) {
                vertx.deployVerticle(ConverterVerticle.class.getName(), new DeploymentOptions().setConfig(state));

            }

            vertx.eventBus().consumer('process', { s ->
                logger.info(s.body())
            })

            routingContext
                    .response()
                    .putHeader('content-type', 'application/json')
                    .end(state.encodePrettily())
        })

        def port = config().getInteger('PORT', 8080)

        server
                .requestHandler(router.&accept)
                .listen(port, { result ->
            if (result.succeeded()) {
                fut.complete()
            } else {
                logger.fatal(result.cause().toString())
                fut.fail(result.cause())
            }
        })
    }
}
