// Copyright (c) 2020 The DAML Authors. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.api.testtool.infrastructure

import com.digitalasset.ledger.api.v1.event.{ArchivedEvent, CreatedEvent, Event, ExercisedEvent}
import com.digitalasset.ledger.api.v1.transaction.{Transaction, TransactionTree, TreeEvent}

object TransactionHelpers {
  def archivedEvents(transaction: Transaction): Vector[ArchivedEvent] =
    events(transaction).flatMap(_.event.archived.toList).toVector

  def createdEvents(tree: TransactionTree): Vector[CreatedEvent] =
    events(tree).flatMap(_.kind.created.toList).toVector

  def createdEvents(transaction: Transaction): Vector[CreatedEvent] =
    events(transaction).flatMap(_.event.created.toList).toVector

  def exercisedEvents(tree: TransactionTree): Vector[ExercisedEvent] =
    events(tree).flatMap(_.kind.exercised.toList).toVector

  private def events(transaction: Transaction): Iterator[Event] =
    transaction.events.iterator

  private def events(tree: TransactionTree): Iterator[TreeEvent] =
    tree.eventsById.valuesIterator
}
