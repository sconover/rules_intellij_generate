import { longGen } from "lgen/long-gen"
import { Timestamp } from "bson"

export function printTimestamp() {
  const l = longGen()
  const t = Timestamp.fromBits(l.low, l.high)
  console.log(t)
}
