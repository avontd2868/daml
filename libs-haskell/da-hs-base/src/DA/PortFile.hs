-- Copyright (c) 2020 The DAML Authors. All rights reserved.
-- SPDX-License-Identifier: Apache-2.0

module DA.PortFile (readPortFile, maxRetries, retryDelayMillis) where

import Control.Concurrent
import qualified Data.Text.IO as T
import Safe (readMay)
import System.Exit
import System.IO

readPortFile :: Int -> String -> IO Int
readPortFile 0 _file = do
  T.hPutStrLn stderr "Port file was not written to in time."
  exitFailure
readPortFile n file =
  readMay <$> readFile file >>= \case
    Nothing -> do
      threadDelay (1000 * retryDelayMillis)
      readPortFile (n-1) file
    Just p -> pure p

retryDelayMillis :: Int
retryDelayMillis = 50

maxRetries :: Int
maxRetries = 60 * (1000 `div` retryDelayMillis)

