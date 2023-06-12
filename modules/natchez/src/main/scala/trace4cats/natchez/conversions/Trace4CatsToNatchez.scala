package trace4cats.natchez.conversions

import java.net.URI
import _root_.natchez.{Kernel, Trace => NatchezTrace, TraceValue => V}
import cats.Applicative
import cats.syntax.functor._
import trace4cats.Trace
import trace4cats.model.AttributeValue.{BooleanValue, DoubleValue, StringValue}
import trace4cats.natchez.KernelConverter
import cats.effect.Resource
import cats.~>
import natchez.Span
import trace4cats.natchez.SpanKindConverter.convert

trait Trace4CatsToNatchez {
  implicit def trace4CatsToNatchez[F[_]: Applicative](implicit trace: Trace[F]): NatchezTrace[F] =
    new NatchezTrace[F] {

      override def put(fields: (String, V)*): F[Unit] =
        trace.putAll(fields.map {
          case (k, V.StringValue(v)) => k -> StringValue(v)
          case (k, V.NumberValue(v)) => k -> DoubleValue(v.doubleValue())
          case (k, V.BooleanValue(v)) => k -> BooleanValue(v)
        }: _*)

      override def log(fields: (String, V)*): F[Unit] = put(fields: _*)

      override def log(event: String): F[Unit] = put("event" -> V.StringValue(event))

      override def attachError(err: Throwable, fields: (String, V)*): F[Unit] = put(
        ("error.message" -> V.StringValue(err.getMessage)) ::
          ("error.class" -> V.StringValue(err.getClass.getSimpleName)) ::
          fields.toList: _*
      )

      override def spanR(name: String, options: Span.Options): Resource[F, F ~> F] = Resource.pure {
        new (F ~> F) {
          def apply[A](fa: F[A]): F[A] = span(name, options)(fa)
        }
      }

      override def span[A](name: String, options: Span.Options)(k: F[A]): F[A] =
        trace.span[A](name, convert(options.spanKind))(k)

      override def kernel: F[Kernel] = trace.headers.map(KernelConverter.to)

      override def traceId: F[Option[String]] = trace.traceId

      override def traceUri: F[Option[URI]] = Applicative[F].pure(None)
    }
}
