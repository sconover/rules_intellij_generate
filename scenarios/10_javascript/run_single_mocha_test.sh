#!/bin/bash -e

node_path=$1
node_js=$2
mocha_bin=$3
test_path=$4

exec env NODE_PATH=$node_path $node_js $mocha_bin --ui tdd $test_path
