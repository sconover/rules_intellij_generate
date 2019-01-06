#!/bin/bash -e

node_path=$1
node_js=$2
tsconfig_json_path=$3
mocha_bin=$4
test_path=$5

# see https://github.com/TypeStrong/ts-node/issues/561#issuecomment-375836931
# ..and https://www.sitepoint.com/community/t/ts-node-how-to-use-tsconfig-paths-with-node-modules/300468/6

exec env NODE_PATH=$node_path TS_NODE_PROJECT=$tsconfig_json_path $node_js $mocha_bin --ui tdd --require ts-node/register --require tsconfig-paths/register $test_path
