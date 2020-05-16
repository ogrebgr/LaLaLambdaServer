package org.example.kforge.modules.main.endpoints

import com.amazonaws.services.lambda.runtime.ClientContext
import com.amazonaws.services.lambda.runtime.CognitoIdentity
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent
import com.bolyartech.forge.server.handler.RouteHandler
import com.bolyartech.forge.server.response.Response
import com.bolyartech.forge.server.route.RequestContext
import com.google.common.io.ByteStreams
import lalalambda.generic.AwsLambdaDispatcher
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.servlet.http.HttpServletResponse

class AwsLambdaEp constructor(private val dis: AwsLambdaDispatcher) : RouteHandler {
    override fun handle(ctx: RequestContext): Response {
        val input = APIGatewayV2ProxyRequestEvent()
        input.path = ctx.pathInfoString
        input.body = ctx.body
        input.httpMethod = ctx.method.literal.toLowerCase()
// TODO add other parameters
        val awsContext = AwsContext()

        return AwsLambdaResponse(dis.handleRequest(input, awsContext))
    }
}

class AwsLambdaResponse constructor(private val awsResp: APIGatewayV2ProxyResponseEvent) : Response {
    companion object {
        private const val HEADER_CONTENT_TYPE = "content-type"
        private const val DEFAULT_CONTENT_TYPE = "text/plain"
    }

    override fun toServletResponse(resp: HttpServletResponse) {

        var contentType: String = DEFAULT_CONTENT_TYPE
        for ((k, v) in awsResp.headers) {
            if (k.toLowerCase() == HEADER_CONTENT_TYPE) {
                contentType = v
                break
            } else {
                resp.addHeader(k, v)
            }
        }

        resp.status = HttpServletResponse.SC_OK
        resp.contentType = contentType
        if (awsResp.body != null) {
            resp.setContentLength(awsResp.body.length)
            val out = resp.outputStream

            val `is`: InputStream = ByteArrayInputStream(awsResp.body.toByteArray(charset("UTF-8")))
            ByteStreams.copy(`is`, out)
            out.flush()
            out.close()
        } else {
            resp.setContentLength(0)
        }
    }
}

class AwsContext : Context {
    override fun getAwsRequestId(): String {
        TODO("Not yet implemented")
    }

    override fun getLogStreamName(): String {
        TODO("Not yet implemented")
    }

    override fun getClientContext(): ClientContext {
        TODO("Not yet implemented")
    }

    override fun getFunctionName(): String {
        TODO("Not yet implemented")
    }

    override fun getRemainingTimeInMillis(): Int {
        TODO("Not yet implemented")
    }

    override fun getLogger(): LambdaLogger {
        TODO("Not yet implemented")
    }

    override fun getInvokedFunctionArn(): String {
        TODO("Not yet implemented")
    }

    override fun getMemoryLimitInMB(): Int {
        TODO("Not yet implemented")
    }

    override fun getLogGroupName(): String {
        TODO("Not yet implemented")
    }

    override fun getFunctionVersion(): String {
        TODO("Not yet implemented")
    }

    override fun getIdentity(): CognitoIdentity {
        TODO("Not yet implemented")
    }

}