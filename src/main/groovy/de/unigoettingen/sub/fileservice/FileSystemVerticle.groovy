package de.unigoettingen.sub.fileservice

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class FileSystemVerticle extends AbstractVerticle {

    def id

    def host = 'gdz.sub.uni-goettingen.de'

    FileSystemVerticle(id) {
        this.id = id
    }

    Vertx getVertx() {
        return Vertx.vertx()
    }

    @Override
    void start() {
        def json = new JsonObject([
                'id': id
        ])

        vertx.eventBus().consumer("process", { message ->
            println(message.body())
        })

        vertx.deployVerticle(ConverterVerticle.class.getName(), new DeploymentOptions().setConfig(json));
    }

    Map getMetadata(String id) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getUrl(id))
                .head()
                .build();

        Response response = client.newCall(request).execute();
        def size = response.header('Content-Length')

        def data = [
                'size': size,
                'url' : getUrl(id),
                'status': size ? 'valid' : 'queued'
        ]

        if (!size) {
            start()
        }


        return data

    }

    private String getUrl(id) {
        return "http://${host}/pdfcache/${id}/${id}___LOG_0001.pdf"
    }
}
