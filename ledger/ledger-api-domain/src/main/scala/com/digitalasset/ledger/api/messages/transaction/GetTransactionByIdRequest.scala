// Copyright (c) 2020 The DAML Authors. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.digitalasset.ledger.api.messages.transaction

import brave.propagation.TraceContext
import com.digitalasset.daml.lf.data.Ref.Party
import com.digitalasset.ledger.api.domain.{LedgerId, TransactionId}

import scala.collection.immutable

final case class GetTransactionByIdRequest(
    ledgerId: LedgerId,
    transactionId: TransactionId,
    requestingParties: immutable.Set[Party],
    traceContext: Option[TraceContext])
