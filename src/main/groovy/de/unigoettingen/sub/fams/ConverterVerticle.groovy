package de.unigoettingen.sub.fams

import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.json.JsonObject

/**
 * @author Ingo Pfennigstorf <i.pfennigstorf@gmail.com>
 */
class ConverterVerticle extends AbstractVerticle {
    String id
    int code = 0

    @Override
    void start() {
        id = config().getString('id')
        compute()
        send()
    }

    void compute() {
        HttpClientOptions options = new HttpClientOptions()
                .setDefaultHost('https://processing.sub.uni-goettingen.de')

        HttpClient client = vertx.createHttpClient(options)
        client.post("/process/pdf/${id}", { response ->
            code = response.statusCode()
        }).putHeader('id', id).end(id)
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
                .put('code', code)
    }
}
