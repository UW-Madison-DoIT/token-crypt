#!/bin/bash
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
