// Copyright (c) 2020 The DAML Authors. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.participant.state.index.v2

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.daml.ledger.participant.state.v1.Configuration
import com.digitalasset.ledger.api.domain.ConfigurationEntry

import scala.concurrent.Future

/**
  * Serves as a backend to implement
  * [[com.digitalasset.ledger.api.v1.admin.config_management_service.ConfigManagementServiceGrpc]]
  *
  */
trait IndexConfigManagementService {

  /** Looks up the current configuration, if set, and the offset from which
    * to subscribe to new configuration entries using [[configurationEntries]].
    */
  def lookupConfiguration(): Future[Option[(Long, Configuration)]]

  /** Retrieve configuration entries. */
  def configurationEntries(startInclusive: Option[Long]): Source[ConfigurationEntry, NotUsed]

}
