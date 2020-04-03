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
package com.helloworld.api

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isEqualTo
import com.helloworld.api.model.GreeterDeployParameters
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.web3j.openapi.client.ClientBuilder
import org.web3j.openapi.client.ClientService

class HelloWorldApiTest {

    lateinit var helloWorldApi: HelloWorldApi

    @BeforeEach
    fun setUp() {
        val service = ClientService("http://localhost:8080")
        helloWorldApi = ClientBuilder.build(HelloWorldApi::class.java, service)
    }

    @Test
    fun `list contracts`() {
        assertThat(helloWorldApi.contracts.findAll()).containsOnly("Greeter")
    }

    @Test
    fun `deploy Greeter and call greet`() {
        val receipt = helloWorldApi.contracts.greeter.deploy(
            GreeterDeployParameters("Test greeter")
        )

        val greet = helloWorldApi.contracts.greeter.greet(receipt.contractAddress)
        assertThat(greet).isEqualTo("Test greeter")
    }
}
