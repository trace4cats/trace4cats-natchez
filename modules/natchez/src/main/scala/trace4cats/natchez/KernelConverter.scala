package trace4cats.natchez

import natchez.Kernel
import trace4cats.model.TraceHeaders

object KernelConverter extends TraceHeaders.Converter[Kernel] {
  def from(t: Kernel): TraceHeaders = TraceHeaders(t.toHeaders)
  def to(h: TraceHeaders): Kernel = Kernel(h.values)
}
