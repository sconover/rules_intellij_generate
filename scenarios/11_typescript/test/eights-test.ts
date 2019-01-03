import * as Long from "long"
import {eights} from "../src/eights"
import {assert} from "chai"
import "mocha" // this MUST be imported or ts compilation will fail because mocha won't be aware of the mocha types

suite("ts example suite", () => {
    test("eights function should return 888 Long", () => {
        assert.deepEqual(eights(), new Long(888))
    })

    test("eights function should return 888 Long (fails)", () => {
        // failure tests left in for convenience, but commented out so that the bazel test run is green
        // assert.deepEqual(eights(), new Long(999))
    })
})
