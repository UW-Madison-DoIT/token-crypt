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
PLAIN_FILE=$2
ENC_FILE_NAME=${PLAIN_FILE}.enc
OUT_FILE_NAME=${PLAIN_FILE}.tar
KEY_FILE=keyfile.enc
HASH_FILE=hashfile.enc

# Make sure we're using UTF-8
LANG=en_US.UTF-8

# Generate a random 128 byte base64 encoded password
PASSWORD=$(openssl rand 128 | openssl enc -A -a)

# Swap out IFS to just care about new line
OIFS="${IFS}"
NIFS=$'\n'
IFS="${NIFS}"

# Generate KEY and IV
for i in `openssl enc -aes-256-cbc -P -k "$PASSWORD"`
do
    if [[ $i == key* ]]
    then
        KEY=`cut -d "=" -f 2 <<< "$i"`
    elif [[ $i == iv* ]]
    then
        IV=`cut -d "=" -f 2 <<< "$i"`
    fi

done

# Revert IFS
IFS="${OIFS}"

# Generate hash of plain file for verification
PLAIN_HASH=`cat $PLAIN_FILE| openssl dgst -md5 -binary | openssl enc -A -a`

# Encrypt the KEY and IV into keyfile.inc for later decryption
echo -e "${KEY}\n${IV}" | openssl rsautl -encrypt -inkey $PUBLIC_KEY_FILE -pubin | openssl enc -A -a > $KEY_FILE

# Encrypt the PLAIN_HASH into hashfile.inc for later decryption
echo -e "${PLAIN_HASH}" | openssl rsautl -encrypt -inkey $PUBLIC_KEY_FILE -pubin | openssl enc -A -a > $HASH_FILE

# Encrypt the file using the KEY and IV
openssl enc -in $PLAIN_FILE -out $ENC_FILE_NAME -aes-256-cbc -K "$KEY" -iv "$IV"

# Bundle into a tar file
tar -cf $OUT_FILE_NAME $KEY_FILE $ENC_FILE_NAME $HASH_FILE

# Clean up temp files
rm $KEY_FILE $ENC_FILE_NAME $HASH_FILE

