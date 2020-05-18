package lalalambda.server.modules.main.endpoints


import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent
import com.bolyartech.forge.server.handler.RouteHandler
import com.bolyartech.forge.server.response.Response
import com.bolyartech.forge.server.route.RequestContext
import lalalambda.simple.SimpleLambdaDispatcher

class SimpleLambdaEp constructor(private val dis: SimpleLambdaDispatcher) : RouteHandler {
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
