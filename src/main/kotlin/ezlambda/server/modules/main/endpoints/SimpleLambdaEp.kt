package ezlambda.server.modules.main.endpoints


import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent
import com.bolyartech.forge.server.handler.RouteHandler
import com.bolyartech.forge.server.response.Response
import com.bolyartech.forge.server.route.RequestContext
import ezlambda.simple.SimpleLambdaDispatcher

class SimpleLambdaEp constructor(private val dis: SimpleLambdaDispatcher) : RouteHandler {
    override fun handle(ctx: RequestContext): Response {
        val input = APIGatewayV2ProxyRequestEvent()
        input.path = ctx.pathInfoString
        input.body = ctx.body
        input.httpMethod = ctx.method.literal.toLowerCase()

        val awsContext = AwsContext()

        return AwsLambdaResponse(dis.handleRequest(input, awsContext))
    }
}
