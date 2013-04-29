#!/bin/bash
set -e

PRIVATE_KEY_FILE=$1
ENC_FILE_NAME=$2
PLAIN_FILE_NAME=${ENC_FILE_NAME%.*}
KEY_FILE=keyfile.enc

# Make sure we're using UTF-8
LANG=en_US.UTF-8

# Swap out IFS to just care about new line
OIFS="${IFS}"
NIFS=$'\n'
IFS="${NIFS}"

# Extract and decrypt KEY and IV
for i in `tar -x -f $ENC_FILE_NAME -O $KEY_FILE | openssl enc -d -a -A | openssl rsautl -decrypt -inkey $PRIVATE_KEY_FILE`
do
    if [[ -z "$KEY" ]]
    then
        KEY=$i
    elif [[ -z "$IV" ]]
    then
        IV=$i
    elif [[ -z "$EXPECTED_HASH" ]]
    then
        EXPECTED_HASH=$i
    else
        echo "$KEY_FILE should contain exactly 3 lines, more than 3 lines were found"
        exit 1
    fi
done


# Revert IFS
IFS="${OIFS}"

# Verify the KEY and IV were correctly extracted
if [[ -z "$KEY" ]]
then
    echo "$KEY_FILE should contain exactly 3 lines, 0 lines were found"
    exit 1
fi
if [[ -z "$IV" ]]
then
    echo "$KEY_FILE should contain exactly 3 lines, 1 line was found"
    exit 1
fi
if [[ -z "$EXPECTED_HASH" ]]
then
    echo "$KEY_FILE should contain exactly 3 lines, 2 lines were found"
    exit 1
fi

# Extract and decrypt the file
tar -x -f $ENC_FILE_NAME -O --exclude=$KEY_FILE | openssl enc -d -out $PLAIN_FILE_NAME -aes-256-cbc -K "$KEY" -iv "$IV"

# Calculate the actual hash code
ACTUAL_HASH=`cat $PLAIN_FILE_NAME | openssl dgst -md5 -binary | openssl enc -A -a`

if [[ "$ACTUAL_HASH" != "$EXPECTED_HASH" ]]; then
    echo "Hash $ACTUAL_HASH doesn't match expected hash $EXPECTED_HASH for decrypted file $PLAIN_FILE_NAME" >&2
    exit 1
fi

echo "Decrypted $ENC_FILE_NAME to $PLAIN_FILE_NAME"

