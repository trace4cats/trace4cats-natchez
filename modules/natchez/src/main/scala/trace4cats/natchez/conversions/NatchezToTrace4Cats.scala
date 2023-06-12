package trace4cats.natchez.conversions

import _root_.natchez.{Span, Trace => NatchezTrace, TraceValue => V}
import cats.Applicative
import cats.syntax.foldable._
import cats.syntax.functor._
import trace4cats.model.AttributeValue.{
  BooleanList,
  BooleanValue,
  DoubleList,
  DoubleValue,
  LongList,
  LongValue,
  StringList,
  StringValue
}
import trace4cats.model.{AttributeValue, SpanKind, SpanStatus, TraceHeaders}
import trace4cats.natchez.KernelConverter
import trace4cats.natchez.SpanKindConverter.convert
import trace4cats.{ErrorHandler, ToHeaders, Trace}

trait NatchezToTrace4Cats {
  implicit def natchezToTrace4Cats[F[_]: Applicative](implicit trace: NatchezTrace[F]): Trace[F] =
    new Trace[F] {
      override def put(key: String, value: AttributeValue): F[Unit] = putAll(key -> value)
      override def putAll(fields: (String, AttributeValue)*): F[Unit] =
        trace.put(fields.map {
          case (k, StringValue(v)) => k -> V.StringValue(v.value)
          case (k, DoubleValue(v)) => k -> V.NumberValue(v.value)
          case (k, LongValue(v)) => k -> V.NumberValue(v.value)
          case (k, BooleanValue(v)) => k -> V.BooleanValue(v.value)
          case (k, StringList(v)) => k -> V.StringValue(v.value.mkString_("[", ", ", "]"))
          case (k, BooleanList(v)) => k -> V.StringValue(v.value.mkString_("[", ", ", "]"))
          case (k, DoubleList(v)) => k -> V.StringValue(v.value.mkString_("[", ", ", "]"))
          case (k, LongList(v)) => k -> V.StringValue(v.value.mkString_("[", ", ", "]"))
        }: _*)
      override def span[A](name: String, kind: SpanKind, errorHandler: ErrorHandler)(fa: F[A]): F[A] =
        trace.span(name, Span.Options.Defaults.withSpanKind(convert(kind)))(fa)
      override def headers(toHeaders: ToHeaders): F[TraceHeaders] = trace.kernel.map(KernelConverter.from)
      override def setStatus(status: SpanStatus): F[Unit] = Applicative[F].unit
      override def traceId: F[Option[String]] = trace.traceId
    }
}
