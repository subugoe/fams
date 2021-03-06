package de.unigoettingen.sub.fams

import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import io.vertx.core.impl.StringEscapeUtils

class FileSystemCheck {

    val VALID: String = "valid"
    val QUEUED: String = "queued"

    fun getMetadata(document: String, log: String, contextId: String): Map<String, Any> {

        val accessKey = System.getenv("ACCESS_KEY")
        val secretKey = System.getenv("SECRET_KEY")
        val credentials = BasicAWSCredentials(accessKey, secretKey)

        val clientConfig = ClientConfiguration()
        clientConfig.protocol = Protocol.HTTP

        val conn = AmazonS3Client(credentials, clientConfig)
        conn.setEndpoint("http://s3.fs.gwdg.de")

        var size: Long

        try {
            val documentMetadata = conn.getObjectMetadata(contextId, "pdf/${document}/${log}.pdf")
            size = documentMetadata.contentLength
        } catch (e: Exception) {
            size = 0
        }

        val status = if (size > 0) VALID else QUEUED
        val data: Map<String, Any> = hashMapOf(
                "document" to document,
                "log" to log,
                "size" to size,
                "url" to StringEscapeUtils.escapeJavaScript(getUrl(document, log, contextId)),
                "status" to status,
                "context" to contextId)

        return data
    }

    private fun getUrl(id: String, log: String, context: String): String {
        return "http://${context}.sub.uni-goettingen.de/download/pdf/${id}/${log}.pdf"
    }

}
