package de.unigoettingen.sub.fams

import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import io.vertx.core.impl.StringEscapeUtils

/**
 * @author Ingo Pfennigstorf <i.pfennigstorf@gmail.com>
 */
class FileSystemCheck {

    val VALID: String = "valid"
    val QUEUED: String = "queued"

    fun getMetadata(id: String, contextId: String): Map<String, Any> {

        val accessKey = System.getenv("ACCESS_KEY")
        val secretKey = System.getenv("SECRET_KEY")
        val credentials = BasicAWSCredentials(accessKey, secretKey)

        val clientConfig = ClientConfiguration()
        clientConfig.protocol = Protocol.HTTP

        val conn = AmazonS3Client(credentials, clientConfig)
        conn.setEndpoint("http://s3.fs.gwdg.de")

        var size: Long

        try {
            val document = conn.getObjectMetadata(contextId, "pdf/${id}/${id}.pdf")
            size = document.contentLength
        } catch (e: Exception) {
            size = 0
        }

        val status = if (size > 0) VALID else QUEUED
        val data: Map<String, Any> = hashMapOf(
                "id" to id,
                "size" to size,
                "url" to StringEscapeUtils.escapeJavaScript(getUrl(id, contextId)),
                "status" to status,
                "context" to contextId)

        return data
    }

    private fun getUrl(id: String, context: String): String {
        return "http://${context}.sub.uni-goettingen.de/download/${id}/${id}.pdf"
    }

}
