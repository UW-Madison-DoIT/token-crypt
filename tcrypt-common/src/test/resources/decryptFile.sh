#!/bin/bash
set -e

PRIVATE_KEY_FILE=$1
ENC_FILE_NAME=$2
KEY_FILE=keyfile.enc

gtar -x -f encfile.tar -O lorem.txt.enc | openssl enc -d -out lorem.txt -aes-256-cbc -k `gtar -x -f encfile.tar -O keyfile.enc | openssl enc -d -a -A | openssl rsautl -decrypt -inkey private.pem`

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

# Encrypt the KEY and IV into keyfile.inc for later decryption
echo -e "${KEY}\n${IV}" | openssl rsautl -encrypt -inkey $PUBLIC_KEY_FILE -pubin | openssl enc -A -a > $KEY_FILE

# Encrypt the file using the KEY and IV
openssl enc -in $PLAIN_FILE -out $ENC_FILE_NAME -aes-256-cbc -K "$KEY" -iv "$IV"

# Bundle into a tar file
echo "tar -cf $OUT_FILE_NAME $KEY_FILE $ENC_FILE_NAME"
tar -cf $OUT_FILE_NAME $KEY_FILE $ENC_FILE_NAME

# Clean up temp files
rm $KEY_FILE $ENC_FILE_NAME

