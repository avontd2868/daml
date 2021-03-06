// Copyright (c) 2020 The DAML Authors. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.rxjava.grpc

import java.util.concurrent.TimeUnit

import com.daml.ledger.javaapi.data.{Command, CreateCommand, Identifier, Record}
import com.daml.ledger.rxjava._
import com.daml.ledger.rxjava.grpc.helpers.{DataLayerHelpers, LedgerServices, TestConfiguration}
import com.google.protobuf.empty.Empty
import org.scalatest.{FlatSpec, Matchers, OptionValues}

import scala.collection.JavaConverters._
import scala.concurrent.Future

class CommandSubmissionClientImplTest
    extends FlatSpec
    with Matchers
    with AuthMatchers
    with OptionValues
    with DataLayerHelpers {

  val ledgerServices = new LedgerServices("command-submission-service-ledger")

  behavior of "[3.1] CommandSubmissionClientImpl.submit"

  it should "send a commands to the ledger" in {
    ledgerServices.withCommandSubmissionClient(Future.successful(Empty.defaultInstance)) {
      (client, serviceImpl) =>
        val commands = genCommands(List.empty)
        client
          .submit(
            commands.getWorkflowId,
            commands.getApplicationId,
            commands.getCommandId,
            commands.getParty,
            commands.getLedgerEffectiveTime,
            commands.getMaximumRecordTime,
            commands.getCommands
          )
          .timeout(TestConfiguration.timeoutInSeconds, TimeUnit.SECONDS)
          .blockingGet()
        val receivedCommands = serviceImpl.getSubmittedRequest.value.getCommands
        receivedCommands.ledgerId shouldBe ledgerServices.ledgerId
        receivedCommands.applicationId shouldBe commands.getApplicationId
        receivedCommands.workflowId shouldBe commands.getWorkflowId
        receivedCommands.commandId shouldBe commands.getCommandId
        receivedCommands.getLedgerEffectiveTime.seconds shouldBe commands.getLedgerEffectiveTime.getEpochSecond
        receivedCommands.getLedgerEffectiveTime.nanos shouldBe commands.getLedgerEffectiveTime.getNano
        receivedCommands.getMaximumRecordTime.seconds shouldBe commands.getMaximumRecordTime.getEpochSecond
        receivedCommands.getMaximumRecordTime.nanos shouldBe commands.getMaximumRecordTime.getNano
        receivedCommands.party shouldBe commands.getParty
        receivedCommands.commands.size shouldBe commands.getCommands.size()
    }
  }

  def toAuthenticatedServer(fn: CommandSubmissionClient => Any): Any =
    ledgerServices.withCommandSubmissionClient(
      Future.successful(Empty.defaultInstance),
      mockedAuthService) { (client, _) =>
      fn(client)
    }

  def submitDummyCommand(client: CommandSubmissionClient, accessToken: Option[String] = None) = {
    val recordId = new Identifier("recordPackageId", "recordModuleName", "recordEntityName")
    val record = new Record(recordId, List.empty[Record.Field].asJava)
    val command = new CreateCommand(new Identifier("a", "a", "b"), record)
    val commands = genCommands(List[Command](command), Option(someParty))
    accessToken
      .fold(
        client
          .submit(
            commands.getWorkflowId,
            commands.getApplicationId,
            commands.getCommandId,
            commands.getParty,
            commands.getLedgerEffectiveTime,
            commands.getMaximumRecordTime,
            commands.getCommands
          ))(
        client
          .submit(
            commands.getWorkflowId,
            commands.getApplicationId,
            commands.getCommandId,
            commands.getParty,
            commands.getLedgerEffectiveTime,
            commands.getMaximumRecordTime,
            commands.getCommands,
            _
          ))
      .timeout(TestConfiguration.timeoutInSeconds, TimeUnit.SECONDS)
      .blockingGet()
  }

  behavior of "Authorization"

  it should "fail without a token" in {
    toAuthenticatedServer { client =>
      expectUnauthenticated {
        submitDummyCommand(client)
      }
    }
  }

  it should "fail with the wrong token" in {
    toAuthenticatedServer { client =>
      expectPermissionDenied {
        submitDummyCommand(client, Option(someOtherPartyReadWriteToken))
      }
    }
  }

  it should "fail with insufficient authorization" in {
    toAuthenticatedServer { client =>
      expectPermissionDenied {
        submitDummyCommand(client, Option(somePartyReadToken))
      }
    }
  }

  it should "succeed with the correct authorization" in {
    toAuthenticatedServer { client =>
      submitDummyCommand(client, Option(somePartyReadWriteToken))
    }
  }

}
