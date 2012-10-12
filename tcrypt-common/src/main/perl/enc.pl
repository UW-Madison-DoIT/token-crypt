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
use MIME::Base64 qw(encode_base64);
use File::Slurp qw(read_file);

my $key=$ARGV[0];
my $token=$ARGV[1];

# Create string to encrypt: TOKEN:base64(md5(TOKEN))
my $digest=md5($token);
my $digest_b64=encode_base64($digest, "");
my $token_and_digest = $token . ":" . $digest_b64;

# Encrypt String
my $key_txt = read_file($key);
my $rsa_pub = Crypt::OpenSSL::RSA->new_public_key($key_txt);
$rsa_pub->use_pkcs1_padding();
my $token_enc = $rsa_pub->encrypt($token_and_digest);
my $token_enc_b64=encode_base64($token_enc, "");

print "ENC($token_enc_b64)\n";



