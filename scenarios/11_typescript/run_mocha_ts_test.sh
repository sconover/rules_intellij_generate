#!/bin/bash -e

working_dir=$1
node_path=$2
node_js=$3
mocha_bin=$4
test_path=$5

export NODE_PATH=$node_path
# cd $working_dir
exec $node_js $mocha_bin --require ts-node/register --ui tdd $test_path
