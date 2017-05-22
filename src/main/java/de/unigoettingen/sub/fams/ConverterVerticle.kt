package de.unigoettingen.sub.fams

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory

/**
 * @author Ingo Pfennigstorf <i.pfennigstorf@gmail.com>
 */
class ConverterVerticle : AbstractVerticle() {

    override fun start(fut: Future<Void>) {
        val id: String = config().getString("id")
        val contextId: String = config().getString("context")
        compute(id, contextId)
        send(id, contextId)
    }

    fun compute(id: String, contextId: String) {
        val logger = LoggerFactory.getLogger("processing")

        val options = HttpClientOptions()
                .setDefaultHost("https://processing.sub.uni-goettingen.de")

        val parameterMap: Map<String, String> = hashMapOf(
                "id" to id,
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

    private fun send(id: String, contextId: String) {
        vertx
                .eventBus()
                .send<Any>("process", toJson(id, contextId), { ar ->
                    if (ar.succeeded()) {
                        vertx.close()
                    }
                })
    }

    private fun toJson(id: String, contextId: String): JsonObject {
        return JsonObject()
                .put("id", id)
                .put("context", contextId)
    }
}
