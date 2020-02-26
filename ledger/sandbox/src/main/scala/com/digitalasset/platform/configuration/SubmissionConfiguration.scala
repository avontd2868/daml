// Copyright (c) 2020 The DAML Authors. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.digitalasset.platform.configuration

import java.time.Duration

final case class SubmissionConfiguration(
    maxTtl: Duration,
)

object SubmissionConfiguration {
  lazy val default: SubmissionConfiguration =
    SubmissionConfiguration(
      maxTtl = Duration.ofDays(1),
    )
}
