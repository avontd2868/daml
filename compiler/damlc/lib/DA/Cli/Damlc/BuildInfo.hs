-- Copyright (c) 2020 The DAML Authors. All rights reserved.
-- SPDX-License-Identifier: Apache-2.0


module DA.Cli.Damlc.BuildInfo
  ( buildInfo
  ) where

import qualified Text.PrettyPrint.ANSI.Leijen as PP
import Data.Monoid ((<>))
import SdkVersion

buildInfo :: PP.Doc
buildInfo = "SDK Version: " <> PP.text sdkVersion
