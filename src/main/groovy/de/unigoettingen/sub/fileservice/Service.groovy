package de.unigoettingen.sub.fileservice

import io.vertx.core.Launcher
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router

class Service extends Launcher {
    static void main(String[] args) {
        def vertx = Vertx.vertx();
        def server = vertx.createHttpServer()
        def router = Router.router(vertx)

        def route = router.route(HttpMethod.GET, '/pdf/:id')

        route.handler({ routingContext ->
            def id = routingContext.request().getParam("id")
            def distribute = new FileSystemVerticle(id);
            distribute.start()

            def response = routingContext.response()
            response.putHeader("content-type", "text/plain")

            // Write to the response and end it
            response.end(id)
        })

        server.requestHandler(router.&accept).listen(8080)
    }
}
