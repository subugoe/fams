package de.unigoettingen.sub.fams

import io.vertx.core.impl.StringEscapeUtils
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * @author Ingo Pfennigstorf <i.pfennigstorf@gmail.com>
 */
class FileSystemCheck {

    val VALID: String = "valid"
    val QUEUED: String = "queued"

    fun getMetadata(id: String, contextId: String): Map<String, Any> {
        val client: OkHttpClient = OkHttpClient()
                .newBuilder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build()

        val request: Request = Request.Builder()
                .url(getUrl(id, contextId))
                .head()
                .build();

        val response: Response = client
                .newCall(request)
                .execute();

        var size: String? = response.header("Content-Length")

        if (size === null) size = "0"

        val realSize: Int = size.toInt()

        val status = if (realSize > 0) VALID else QUEUED
        val data: Map<String, Any> = hashMapOf(
                "id" to id,
                "size" to realSize,
                "url" to StringEscapeUtils.escapeJavaScript(getUrl(id, contextId)),
                "status" to status,
                "context" to contextId)

        return data
    }

    private fun getUrl(id: String, context: String): String {
        return "http://${context}.sub.uni-goettingen.de/download/${id}/${id}___LOG_0001.pdf"
    }

}
