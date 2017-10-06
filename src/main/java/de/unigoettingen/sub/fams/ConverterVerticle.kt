package de.unigoettingen.sub.fams

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory

class ConverterVerticle : AbstractVerticle() {

    override fun start(fut: Future<Void>) {
        val document: String = config().getString("document")
        val log: String = config().getString("log")
        val contextId: String = config().getString("context")
        compute(document, log, contextId)
        send(document, log, contextId)
    }

    fun compute(document: String, log: String, contextId: String) {
        val logger = LoggerFactory.getLogger("processing")

        val options = HttpClientOptions()
                .setDefaultHost("https://processing.sub.uni-goettingen.de")

        val parameterMap: Map<String, String> = hashMapOf(
                "document" to document,
                "context" to contextId
        )

        val parameters = JsonObject(parameterMap).toString()

        val client: HttpClient = vertx.createHttpClient(options)
        client.post("/process/pdf/", { response ->
            logger.info("$response.statusCode() - $response.statusMessage()")
        })
                .setChunked(true)
                .write(parameters)
                .end()
    }

    private fun send(document: String, log: String, contextId: String) {
        vertx
                .eventBus()
                .send<Any>("process", toJson(document, log, contextId), { ar ->
                    if (ar.succeeded()) {
                        vertx.close()
                    }
                })
    }

    private fun toJson(document: String, log: String, contextId: String): JsonObject {
        return JsonObject()
                .put("document", document)
                .put("log", log)
                .put("context", contextId)
    }
}
