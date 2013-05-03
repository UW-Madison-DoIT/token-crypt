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
TAR_FILE_NAME=$2
PLAIN_FILE_NAME=${TAR_FILE_NAME%.*}
ENC_FILE_NAME=`basename ${PLAIN_FILE_NAME}.enc`
KEY_FILE=keyfile.enc
HASH_FILE=hashfile.enc

# Make sure we're using UTF-8
LANG=en_US.UTF-8

# Swap out IFS to just care about new line
OIFS="${IFS}"
NIFS=$'\n'
IFS="${NIFS}"

# Extract and decrypt KEY and IV
for i in `tar -x -f $TAR_FILE_NAME -O $KEY_FILE | openssl enc -d -a -A | openssl rsautl -decrypt -inkey $PRIVATE_KEY_FILE`
do
    if [[ -z "$KEY" ]]
    then
        KEY=$i
    elif [[ -z "$IV" ]]
    then
        IV=$i
    else
        echo "$KEY_FILE should contain exactly 2 lines, more than 2 lines were found" >&2
        exit 1
    fi
done

# Revert IFS
IFS="${OIFS}"

# Verify the KEY and IV were correctly extracted
if [[ -z "$KEY" ]]
then
    echo "$KEY_FILE should contain exactly 2 lines, 0 lines were found" >&2
    exit 1
fi
if [[ -z "$IV" ]]
then
    echo "$KEY_FILE should contain exactly 2 lines, 1 line was found" >&2
    exit 1
fi


# Extract and decrypt EXPECTED_HASH
EXPECTED_HASH=`tar -x -f $TAR_FILE_NAME -O $HASH_FILE | openssl enc -d -a -A | openssl rsautl -decrypt -inkey $PRIVATE_KEY_FILE`
if [[ -z "$EXPECTED_HASH" ]]
then
    echo "$HASH_FILE did not contain expected hash code" >&2
    exit 1
fi

# Extract and decrypt the file
tar -x -f $TAR_FILE_NAME -O $ENC_FILE_NAME | openssl enc -d -out $PLAIN_FILE_NAME -aes-256-cbc -K "$KEY" -iv "$IV"

# Calculate the actual hash code
ACTUAL_HASH=`cat $PLAIN_FILE_NAME | openssl dgst -md5 -binary | openssl enc -A -a`

if [[ "$ACTUAL_HASH" != "$EXPECTED_HASH" ]]; then
    echo "Hash $ACTUAL_HASH doesn't match expected hash $EXPECTED_HASH for decrypted file $PLAIN_FILE_NAME The file should be deleted" >&2
    exit 1
fi

echo "Decrypted $TAR_FILE_NAME to $PLAIN_FILE_NAME"

