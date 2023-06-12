package trace4cats.natchez

import natchez.Span
import trace4cats.model.SpanKind

object SpanKindConverter {
  def convert(kind: Span.SpanKind): SpanKind = kind match {
    case Span.SpanKind.Internal => SpanKind.Internal
    case Span.SpanKind.Client => SpanKind.Client
    case Span.SpanKind.Server => SpanKind.Server
    case Span.SpanKind.Producer => SpanKind.Producer
    case Span.SpanKind.Consumer => SpanKind.Consumer
  }

  def convert(kind: SpanKind): Span.SpanKind = kind match {
    case SpanKind.Internal => Span.SpanKind.Internal
    case SpanKind.Client => Span.SpanKind.Client
    case SpanKind.Server => Span.SpanKind.Server
    case SpanKind.Producer => Span.SpanKind.Producer
    case SpanKind.Consumer => Span.SpanKind.Consumer
  }
}
