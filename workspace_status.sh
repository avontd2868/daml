#!/usr/bin/env bash
# Copyright (c) 2020 The DAML Authors. All rights reserved.
# SPDX-License-Identifier: Apache-2.0

set -euo pipefail

echo "STABLE_VERSION_DATE $(TZ=UTC git log -n1 -s --format=%cd --date=short -- VERSION)"
