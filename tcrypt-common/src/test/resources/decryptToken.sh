#!/bin/bash
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

