package de.unigoettingen.sub.fams

import io.vertx.core.impl.StringEscapeUtils
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 *
 * @author Ingo Pfennigstorf <i.pfennigstorf@gmail.com>
 */
class FileSystemCheck {

    static final VALID = 'valid'
    static final QUEUED = 'queued'

    def static getMetadata(String id, String contextId) {
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build()

        Request request = new Request.Builder()
                .url(getUrl(id, contextId))
                .head()
                .build();

        Response response = client
                .newCall(request)
                .execute();

        def size = response.header('Content-Length')

        def data = [
                'size'   : size,
                'url'    : StringEscapeUtils.escapeJavaScript(getUrl(id, contextId)),
                'status' : size ? VALID : QUEUED,
                'context': contextId
        ]

        return data
    }

    private static getUrl(id, context) {
        return "http://${context}.sub.uni-goettingen.de/download/${id}/${id}___LOG_0001.pdf"
    }

}
