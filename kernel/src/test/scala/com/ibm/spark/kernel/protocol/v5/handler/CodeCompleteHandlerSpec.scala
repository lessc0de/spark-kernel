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

package com.ibm.spark.kernel.protocol.v5.handler

import akka.actor._
import akka.testkit.{TestProbe, ImplicitSender, TestKit}
import com.ibm.spark.kernel.protocol.v5.KernelStatusType
import com.ibm.spark.kernel.protocol.v5.KernelStatusType._
import com.ibm.spark.kernel.protocol.v5.content.CompleteRequest
import com.ibm.spark.kernel.protocol.v5._
import com.ibm.spark.kernel.protocol.v5.kernel.ActorLoader
import com.ibm.spark.kernel.protocol.v5Test._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FunSpecLike, BeforeAndAfter, Matchers}
import org.mockito.Mockito._
import scala.concurrent.duration._

class CodeCompleteHandlerSpec extends TestKit(
  ActorSystem("CodeCompleteHandlerSpec")
) with ImplicitSender with FunSpecLike with Matchers with MockitoSugar
  with BeforeAndAfter {

  var actorLoader: ActorLoader = _
  var handlerActor: ActorRef = _
  var kernelMessageRelayProbe: TestProbe = _
  var interpreterProbe: TestProbe = _
  var statusDispatchProbe: TestProbe = _

  before {
    actorLoader = mock[ActorLoader]

    handlerActor = system.actorOf(Props(classOf[CodeCompleteHandler], actorLoader))

    kernelMessageRelayProbe = TestProbe()
    when(actorLoader.load(SystemActorType.KernelMessageRelay))
      .thenReturn(system.actorSelection(kernelMessageRelayProbe.ref.path.toString))

    interpreterProbe = new TestProbe(system)
    when(actorLoader.load(SystemActorType.Interpreter))
      .thenReturn(system.actorSelection(interpreterProbe.ref.path.toString))

    statusDispatchProbe = new TestProbe(system)
    when(actorLoader.load(SystemActorType.StatusDispatch))
      .thenReturn(system.actorSelection(statusDispatchProbe.ref.path.toString))
  }

  def replyToHandlerWithOkAndResult() = {
    val expectedClass = classOf[CompleteRequest]
    interpreterProbe.expectMsgClass(expectedClass)
    interpreterProbe.reply((0, List[String]()))
  }

  def replyToHandlerWithOkAndBadResult() = {
    val expectedClass = classOf[CompleteRequest]
    interpreterProbe.expectMsgClass(expectedClass)
    interpreterProbe.reply("hello")
  }

  describe("CodeCompleteHandler (ActorLoader)") {
    it("should send a CompleteRequest") {
      handlerActor ! MockCompleteRequestKernelMessage
      replyToHandlerWithOkAndResult()
      var completeReplyMessage: KernelMessage = null
      // Set to 500ms because of a timing issue with the test
      kernelMessageRelayProbe.receiveWhile(500.milliseconds) {
        case message : KernelMessage =>
          val messageType = message.header.msg_type
          if (messageType == MessageType.Outgoing.CompleteReply.toString)
            completeReplyMessage = message
      }
      completeReplyMessage should not be (null)
    }

    it("should throw an error for bad JSON") {
      handlerActor ! MockKernelMessageWithBadJSON
      var result = false
      try {
        replyToHandlerWithOkAndResult()
      }
      catch {
        case t: Throwable => result = true
      }
      result should be (true)
    }

    it("should throw an error for bad code completion") {
      handlerActor ! MockCompleteRequestKernelMessage
      try {
        replyToHandlerWithOkAndBadResult()
      }
      catch {
        case error: Exception => error.getMessage() should be ("Parse error in CodeCompleteHandler")
      }
    }

    it("should send an idle message") {
      handlerActor ! MockCompleteRequestKernelMessage
      replyToHandlerWithOkAndResult()
      var idle = false
      statusDispatchProbe.receiveWhile(100.milliseconds) {
        case Tuple2(status: KernelStatusType, header: Header) =>
          if(status == KernelStatusType.Idle)
            idle = true
        }
      idle should be (true)
      }
    }
}
