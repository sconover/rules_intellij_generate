import {assert} from "chai"
import "mocha" // this magically makes the mocha "test" method be found from any working directory. see https://stackoverflow.com/a/43721170
import {doEcho} from "../src/echo"

suite("echo", () => {
    test("echo basics", () => {
        assert.equal(doEcho("foo"), "echoing: foo")
    })

    test("echo basics again", () => {
        assert.equal(doEcho("bar"), "echoing: bar")
    })
})

