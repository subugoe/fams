package de.unigoettingen.sub.fams

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.impl.StringEscapeUtils
import io.vertx.core.json.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class FileSystemVerticle extends AbstractVerticle {

    static final VALID = 'valid'
    static final QUEUED = 'queued'

    def id

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

        vertx
                .eventBus()
                .consumer('process', { message ->
            println(message.body())
        })

        vertx.deployVerticle(ConverterVerticle.class.getName(), new DeploymentOptions().setConfig(json));
    }

    def getMetadata(String id) {
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build()

        Request request = new Request.Builder()
                .url(getUrl(id))
                .head()
                .build();

        Response response = client
                .newCall(request)
                .execute();

        def size = response.header('Content-Length')

        def data = [
                'size'  : size,
                'url'   : StringEscapeUtils.escapeJavaScript(getUrl(id)),
                'status': size ? VALID : QUEUED
        ]

        if (!size) {
            start()
        }

        return data
    }

    private static getUrl(id) {
        return "http://gdz.sub.uni-goettingen.de/download/${id}/${id}___LOG_0001.pdf"
    }
}
