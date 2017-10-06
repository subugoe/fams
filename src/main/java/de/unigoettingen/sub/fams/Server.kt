package de.unigoettingen.sub.fams

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.CorsHandler

/**
 * @author Ingo Pfennigstorf <i.pfennigstorf@gmail.com>
 */
class Server : AbstractVerticle() {

    override fun start(fut: Future<Void>) {
        val logger: Logger = LoggerFactory.getLogger("Server")
        val server: HttpServer = vertx.createHttpServer()
        val router: Router = Router.router(vertx)

        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST))

        val route = router.route(HttpMethod.GET, "/pdf/:contextId/:document/:log")

        route.handler({ routingContext ->
            val document: String = routingContext.request().getParam("document")
            val log: String = routingContext.request().getParam("log")
            val contextId: String = routingContext.request().getParam("contextId")
            val fileSystemCheck: FileSystemCheck = FileSystemCheck()
            val state = JsonObject(fileSystemCheck.getMetadata(document, log, contextId))

            if (state.getString("status") === fileSystemCheck.QUEUED) {
                vertx.deployVerticle("de.unigoettingen.sub.fams.ConverterVerticle", DeploymentOptions().setConfig(state));
            }

            vertx.eventBus().consumer<Any>("process", { s ->
                logger.info(s.body())
            })

            routingContext
                    .response()
                    .putHeader("content-type", "application/json")
                    .end(state.encodePrettily())
        })

        val port: Int = if ((System.getenv("PORT").toInt() > 0)) System.getenv("PORT").toInt() else 8080

        server
                .requestHandler({ router.accept(it) })
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
