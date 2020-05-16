package org.example.kforge

import myLambdas.MyDispatcher

fun main(args: Array<String>) {
    val lambdaServer = La3Server(MyDispatcher())
    lambdaServer.start()
}