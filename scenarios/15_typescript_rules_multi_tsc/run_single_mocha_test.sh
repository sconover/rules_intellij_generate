#!/bin/bash -e

node_path=$1
node_js=$2
mocha_bin=$3
test_path=$4

echo "env NODE_PATH=$node_path $node_js $mocha_bin --ui tdd --compilers ts:ts-node/register $test_path"

exec env NODE_PATH=$node_path $node_js $mocha_bin --ui tdd --compilers ts:ts-node/register $test_path
