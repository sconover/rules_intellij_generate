const assert = require('chai').assert

const echo = require("../src/echo")

suite("echo", () => {
    test("echo basics", () => {
        assert.equal(echo.doEcho("foo"), "echoing: foo")
    })

    test("echo basics again", () => {
        assert.equal(echo.doEcho("bar"), "echoing: bar")
    })
})
