package trace4cats.natchez

import natchez.Kernel
import trace4cats.model.TraceHeaders

object KernelConverter extends TraceHeaders.Converter[Kernel] {
  def from(t: Kernel): TraceHeaders = TraceHeaders.of(t.toHeaders)
  def to(h: TraceHeaders): Kernel = Kernel(h.values.map { case (k, v) => k.toString -> v })
}
