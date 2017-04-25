package de.unigoettingen.sub.fams

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory

/**
 * @author Ingo Pfennigstorf <i.pfennigstorf@gmail.com>
 */
class ConverterVerticle extends AbstractVerticle {
    def id
    def contextId

    @Override
    void start(Future<Void> fut) {
        id = config().getString('id')
        contextId = config().getString('context')
        compute()
        send()
    }

    void compute() {
        def logger = LoggerFactory.getLogger('processing')

        def options = new HttpClientOptions()
                .setDefaultHost('https://processing.sub.uni-goettingen.de')

        def parameters = new JsonObject([
                id     : id,
                context: contextId
        ]).toString()

        def client = vertx.createHttpClient(options)
        client.post("/process/pdf/", { response ->
            logger.info(response.statusCode() + ' - ' + response.statusMessage())
        })
                .setChunked(true)
                .write(parameters)
                .end()
    }

    private void send() {
        vertx
                .eventBus()
                .send('process', toJson(), { ar ->
            if (ar.succeeded()) {
                vertx.close()
            }
        })
    }

    private JsonObject toJson() {
        return new JsonObject()
                .put('id', id)
                .put('context', contextId)
    }
}
