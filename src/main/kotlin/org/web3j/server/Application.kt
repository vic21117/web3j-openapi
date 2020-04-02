/*
 * Copyright 2020 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.server

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.glassfish.jersey.server.ServerProperties
import org.glassfish.jersey.servlet.ServletContainer
import kotlin.system.exitProcess

fun main() {
    val resourceConfig = Web3jResourceConfig().apply {
        // FIXME Load contract resource classes from eg. command line
        registerClasses(GreeterResourceImpl::class.java)
    }

    val servletHolder = ServletHolder(ServletContainer(resourceConfig)).apply {
        setInitParameter(ServerProperties.PROVIDER_PACKAGES, "org.web3j.something, org.web3j.server")
        initOrder = 0
    }

    val servletContextHandler = ServletContextHandler(ServletContextHandler.NO_SESSIONS).apply {
        addServlet(servletHolder, "/*")
        contextPath = "/*"
    }

    val server = Server(8080).apply {
        handler = servletContextHandler
    }

    try {
        server.start()
        server.join()
    } catch (ex: Exception) {
        exitProcess(1)
    } finally {
        server.destroy()
    }
}
