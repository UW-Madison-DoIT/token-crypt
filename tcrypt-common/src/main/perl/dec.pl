#! /usr/bin/env perl
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


use strict;

use Digest::MD5 qw(md5);
use Crypt::OpenSSL::RSA;
use MIME::Base64 qw(encode_base64 decode_base64);
use File::Slurp qw(read_file);

my $key=$ARGV[0];
my $token_enc_b64=$ARGV[1];

# Decrypt String
my $key_txt = read_file($key);
my $rsa_priv = Crypt::OpenSSL::RSA->new_private_key($key_txt);
$rsa_priv->use_pkcs1_padding();
my $token_enc=decode_base64($token_enc_b64);
my $token_and_digest = $rsa_priv->decrypt($token_enc);

# Pull out the token and the base64 encoded token md5
my $seperatorIndex = rindex($token_and_digest, ":");
if ($seperatorIndex < 0) {
    die "PasswordWithHash string doesn't contain seperator: :\n";
}
my $token = substr($token_and_digest, 0, $seperatorIndex);
my $expected_digest_b64 = substr($token_and_digest, $seperatorIndex + 1);

# Validate the MD5
my $digest=md5($token);
my $digest_b64=encode_base64($digest, "");
if ($digest_b64 ne $expected_digest_b64) {
    die "MD5 of the decrypted token is not valid\n";
}

print "$token\n";
