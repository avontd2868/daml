-- Copyright (c) 2019 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
-- SPDX-License-Identifier: Apache-2.0

-- | Test utils
module DA.Test.Util (standardizeQuotes) where

import qualified Data.Text as T

standardizeQuotes :: T.Text -> T.Text
standardizeQuotes msg = let
        repl '‘' = '\''
        repl '’' = '\''
        repl '`' = '\''
        repl  c   = c
    in  T.map repl msg
