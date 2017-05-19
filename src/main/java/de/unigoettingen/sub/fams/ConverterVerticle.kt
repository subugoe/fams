package de.unigoettingen.sub.fams

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.core.json.JsonObject

/**
 * @author Ingo Pfennigstorf <i.pfennigstorf@gmail.com>
 */
class ConverterVerticle : AbstractVerticle {

    var id: String = ""
    var contextId: String = ""

    constructor() : super()

    override fun start(fut: Future<Void>) {
        id = config().getString("id")
        contextId = config().getString("context")
        compute()
        send()
    }

    fun compute() {
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

    private fun send() {
        vertx
                .eventBus()
                .send<Any>("process", toJson(), { ar ->
                    if (ar.succeeded()) {
                        vertx.close()
                    }
                })
    }

    private fun toJson(): JsonObject {
        return JsonObject()
                .put("id", id)
                .put("context", contextId)
    }
}
