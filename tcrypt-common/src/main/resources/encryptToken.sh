#!/bin/bash
#
# Copyright 2012, Board of Regents of the University of
# Wisconsin System. See the NOTICE file distributed with
# this work for additional information regarding copyright
# ownership. Board of Regents of the University of Wisconsin
# System licenses this file to you under the Apache License,
# Version 2.0 (the "License"); you may not use this file
# except in compliance with the License. You may obtain a
# copy of the License at:
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on
# an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
#

set -e

PUBLIC_KEY_FILE=$1
DEC_VALUE=$2

# Make sure we're using UTF-8
LANG=en_US.UTF-8

# Calculate the actual hash of the decrypted token
ACTUAL_HASH=`echo -n ${DEC_VALUE} | openssl dgst -md5 -binary | openssl enc -a -A`

# Encrypt the token
ENC_VALUE=`echo -n ${DEC_VALUE}:${ACTUAL_HASH} | openssl rsautl -encrypt -inkey $PUBLIC_KEY_FILE -pubin | openssl enc -a -A`

echo "ENC($ENC_VALUE)"
