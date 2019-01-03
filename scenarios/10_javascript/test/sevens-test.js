const Long = require("long")
const sevens = require("../src/sevens")
const assert = require("chai").assert

suite("js example suite", () => {
    test("seven function should return 777 Long", () => {
        assert.deepEqual(sevens(), new Long(777))
    })

    test("seven function should return 777 Long (fails)", () => {
        // failure tests left in for convenience, but commented out so that the bazel test run is green
        // assert.deepEqual(sevens(), new Long(888))
    })
})
