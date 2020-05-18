package lalalambda.server

import myLambdas.MyAwsDispatcher
import myLambdas.MySimpleDispatcher

fun main(args: Array<String>) {
    val lambdaServer = La3Server(MyAwsDispatcher(), MySimpleDispatcher())
    lambdaServer.start()
}