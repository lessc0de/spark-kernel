/*
 * Copyright 2014 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.spark.kernel.protocol.v5.kernel.socket

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import akka.util.ByteString
import akka.zeromq.ZMQMessage
import com.typesafe.config.ConfigFactory
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FunSpecLike, Matchers}

object HeartbeatSpec {
  val config = """
    akka {
      loglevel = "WARNING"
    }"""
}

class HeartbeatSpec extends TestKit(ActorSystem("HeartbeatActorSpec", ConfigFactory.parseString(HeartbeatSpec.config)))
with ImplicitSender with FunSpecLike with Matchers with MockitoSugar {
  val SomeMessage: String = "some message"
  val SomeZMQMessage: ZMQMessage = ZMQMessage(ByteString(SomeMessage.getBytes))

  describe("HeartbeatActor") {
    val socketFactory = mock[SocketFactory]
    val probe : TestProbe = TestProbe()
    when(socketFactory.Heartbeat(any(classOf[ActorSystem]), any(classOf[ActorRef]))).thenReturn(probe.ref)

    val heartbeat = system.actorOf(Props(classOf[Heartbeat], socketFactory))

    describe("send heartbeat") {
      it("should receive and send same ZMQMessage") {
        heartbeat ! SomeZMQMessage
        probe.expectMsg(SomeZMQMessage)
      }
    }
  }
}
