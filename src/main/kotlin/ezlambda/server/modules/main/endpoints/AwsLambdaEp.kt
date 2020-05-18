package ezlambda.server.modules.main.endpoints

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
import com.google.gson.Gson
import ezlambda.aws.AwsLambdaDispatcher
import ezlambda.aws.AwsRequestEvent
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.servlet.http.HttpServletResponse

class AwsLambdaEp constructor(private val dis: AwsLambdaDispatcher) : RouteHandler {
    private val gson = Gson()

    override fun handle(ctx: RequestContext): Response {
        val inputRaw = gson.fromJson(ctx.body, AwsRequestEvent::class.java)
        val awsContext = AwsContext()

        val input = APIGatewayV2ProxyRequestEvent()
        val ri = APIGatewayV2ProxyRequestEvent.RequestIdentity()
        ri.cognitoIdentityPoolId = inputRaw.requestContext.identity.cognitoIdentityPoolId
        ri.accountId = inputRaw.requestContext.identity.accountId
        ri.cognitoIdentityId = inputRaw.requestContext.identity.cognitoIdentityId
        ri.caller = inputRaw.requestContext.identity.caller
        ri.apiKey = inputRaw.requestContext.identity.apiKey
        ri.sourceIp = inputRaw.requestContext.identity.sourceIp
        ri.cognitoAuthenticationType = inputRaw.requestContext.identity.cognitoAuthenticationType
        ri.cognitoAuthenticationProvider = inputRaw.requestContext.identity.cognitoAuthenticationProvider
        ri.userArn = inputRaw.requestContext.identity.userArn
        ri.userAgent = inputRaw.requestContext.identity.userAgent
        ri.user = inputRaw.requestContext.identity.user
        ri.accessKey = inputRaw.requestContext.identity.accessKey

        val rc = APIGatewayV2ProxyRequestEvent.RequestContext()
        rc.accountId = inputRaw.requestContext.accountId
        rc.resourceId = inputRaw.requestContext.resourceId
        rc.stage = inputRaw.requestContext.stage
        rc.resourceId = inputRaw.requestContext.resourceId
        rc.identity = ri
        rc.resourcePath = inputRaw.requestContext.resourcePath
        rc.authorizer = inputRaw.requestContext.authorizer
        rc.httpMethod = inputRaw.requestContext.httpMethod
        rc.apiId = inputRaw.requestContext.apiId
        rc.connectedAt = inputRaw.requestContext.connectedAt
        rc.connectionId = inputRaw.requestContext.connectionId
        rc.domainName = inputRaw.requestContext.domainName
        rc.error = inputRaw.requestContext.error
        rc.eventType = inputRaw.requestContext.eventType
        rc.extendedRequestId = inputRaw.requestContext.extendedRequestId
        rc.integrationLatency = inputRaw.requestContext.integrationLatency
        rc.messageDirection = inputRaw.requestContext.messageDirection
        rc.messageId = inputRaw.requestContext.messageId
        rc.requestTime = inputRaw.requestContext.requestTime
        rc.requestTimeEpoch = inputRaw.requestContext.requestTimeEpoch
        rc.routeKey = inputRaw.requestContext.routeKey
        rc.status = inputRaw.requestContext.status

        input.resource = inputRaw.resource
        input.path = inputRaw.path
        input.httpMethod = inputRaw.httpMethod
        input.headers = inputRaw.headers
        input.multiValueHeaders = inputRaw.multiValueHeaders
        input.queryStringParameters = inputRaw.queryStringParameters
        input.multiValueQueryStringParameters = inputRaw.multiValueQueryStringParameters
        input.pathParameters = inputRaw.pathParameters
        input.stageVariables = inputRaw.stageVariables
        input.requestContext = rc
        input.body = inputRaw.body
        input.isIsBase64Encoded = inputRaw.isBase64Encoded

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
        if (awsResp.headers != null) {
            for ((k, v) in awsResp.headers) {
                if (k.toLowerCase() == HEADER_CONTENT_TYPE) {
                    contentType = v
                    break
                } else {
                    resp.addHeader(k, v)
                }
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