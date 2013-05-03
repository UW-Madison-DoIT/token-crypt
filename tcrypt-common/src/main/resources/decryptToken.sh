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

PRIVATE_KEY_FILE=$1
ENC_TOKEN=$2

# Make sure we're using UTF-8
LANG=en_US.UTF-8

# Strip off ENC()
ENC_TOKEN=`cut -d "(" -f 2 <<< "$ENC_TOKEN"`
ENC_TOKEN=`cut -d ")" -f 1 <<< "$ENC_TOKEN"`

# Decrypt the token
DEC_TOKEN=`echo $ENC_TOKEN | openssl enc -A -a -d | openssl rsautl -decrypt -inkey $PRIVATE_KEY_FILE`

# Split out the value and hash
DEC_VALUE=`cut -d ":" -f 1 <<< "$DEC_TOKEN"`
DEC_HASH=`cut -d ":" -f 2 <<< "$DEC_TOKEN"`

# Calculate the actual hash of the decrypted token
ACTUAL_HASH=`echo -n $DEC_VALUE | openssl dgst -md5 -binary | openssl enc -A -a`

if [ "$ACTUAL_HASH" == "$DEC_HASH" ]; then
    echo -n $DEC_VALUE
else
    echo "Hash $ACTUAL_HASH doesn't match expected hash $DEC_HASH for decrypted value $DEC_VALUE" >&2
    exit 1
fi

