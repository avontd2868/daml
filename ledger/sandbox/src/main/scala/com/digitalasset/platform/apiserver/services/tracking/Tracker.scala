// Copyright (c) 2020 The DAML Authors. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.digitalasset.platform.apiserver.services.tracking

import com.digitalasset.ledger.api.v1.command_service.SubmitAndWaitRequest
import com.digitalasset.ledger.api.v1.completion.Completion

import scala.concurrent.{ExecutionContext, Future}

private[tracking] trait Tracker extends AutoCloseable {

  def track(request: SubmitAndWaitRequest)(implicit ec: ExecutionContext): Future[Completion]
}

private[tracking] object Tracker {

  class WithLastSubmission(delegate: Tracker) extends Tracker {

    override def close(): Unit = delegate.close()

    @volatile private var lastSubmission = System.nanoTime()

    def getLastSubmission: Long = lastSubmission

    override def track(request: SubmitAndWaitRequest)(
        implicit ec: ExecutionContext): Future[Completion] = {
      lastSubmission = System.nanoTime()
      delegate.track(request)
    }
  }

  object WithLastSubmission {
    def apply(delegate: Tracker): WithLastSubmission = new WithLastSubmission(delegate)
  }
}
