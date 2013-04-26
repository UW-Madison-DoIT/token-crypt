#!/bin/bash

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

# Use for debugging
echo -e "${KEY}\n${IV}" > keyfile

# Encrypt the KEY and IV into keyfile.inc for later decryption
echo -e "${KEY}\n${IV}" | openssl rsautl -encrypt -inkey my.wisc.edu-public.pem -pubin | openssl enc -A -a > keyfile.enc

# Encrypt the file using the KEY and IV
openssl enc -in testFile.txt -out testFile.txt.enc -aes-256-cbc -K "$KEY" -iv "$IV"

# Bundle into a tar file
tar -cf testFile.tar keyfile.enc testFile.txt.enc

