package ezlambda.server.modules.main

import com.bolyartech.forge.server.HttpMethod
import com.bolyartech.forge.server.module.HttpModule
import com.bolyartech.forge.server.route.Route
import com.bolyartech.forge.server.route.RouteImpl
import ezlambda.server.modules.main.endpoints.SimpleLambdaEp
import ezlambda.simple.SimpleLambdaDispatcher
import java.util.*

class MainModule constructor(private val disSimple: SimpleLambdaDispatcher) :
    HttpModule {
    companion object {

        private const val MODULE_SYSTEM_NAME = "main"
        private const val MODULE_VERSION_CODE = 1
        private const val MODULE_VERSION_NAME = "1.0.0"
        private const val PATH_PREFIX = "/"
    }

    override fun createRoutes(): List<Route> {
        val ret = ArrayList<Route>()

        val epSimple = SimpleLambdaEp(disSimple)
        ret.add(RouteImpl(HttpMethod.GET, PATH_PREFIX, epSimple))
        ret.add(RouteImpl(HttpMethod.POST, PATH_PREFIX, epSimple))
        ret.add(RouteImpl(HttpMethod.PUT, PATH_PREFIX, epSimple))
        ret.add(RouteImpl(HttpMethod.DELETE, PATH_PREFIX, epSimple))

        return ret
    }

    override fun getSystemName(): String {
        return MODULE_SYSTEM_NAME
    }

    override fun getShortDescription(): String {
        return ""
    }

    override fun getVersionCode(): Int {
        return MODULE_VERSION_CODE
    }

    override fun getVersionName(): String {
        return MODULE_VERSION_NAME
    }
}