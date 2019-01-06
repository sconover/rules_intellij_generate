import * as Long from "long"
import {longGen} from "lgen/long-gen"
import {assert} from "chai"
import "mocha" // this MUST be imported or ts compilation will fail because mocha won't be aware of the mocha types

suite("long-gen suite", () => {
    test("long-gen returns a long with 7's", () => {
        assert.deepEqual(longGen(), new Long(77777))
    })
})
