package trace4cats.natchez

import _root_.natchez.{Kernel, Span, TraceValue => V}
import cats.Applicative
import cats.effect.kernel.{Resource, Sync}
import cats.syntax.show._
import trace4cats.ToHeaders
import trace4cats.model.AttributeValue._
import trace4cats.natchez.SpanKindConverter.convert

import java.net.URI

final case class Trace4CatsSpan[F[_]: Sync](span: trace4cats.Span[F], toHeaders: ToHeaders) extends Span[F] {
  override def put(fields: (String, V)*): F[Unit] =
    span.putAll(fields.map {
      case (k, V.StringValue(v)) => k -> StringValue(v)
      case (k, V.NumberValue(v)) => k -> DoubleValue(v.doubleValue())
      case (k, V.BooleanValue(v)) => k -> BooleanValue(v)
    }: _*)

  override def log(fields: (String, V)*): F[Unit] = put(fields: _*)

  override def log(event: String): F[Unit] = put("event" -> V.StringValue(event))

  override def attachError(err: Throwable, fields: (String, V)*): F[Unit] =
    put(
      ("error.message" -> V.StringValue(err.getMessage)) ::
        ("error.class" -> V.StringValue(err.getClass.getSimpleName)) ::
        fields.toList: _*
    )

  override def kernel: F[Kernel] = Applicative[F].pure(KernelConverter.to(toHeaders.fromContext(span.context)))

  override def span(name: String, options: Span.Options): Resource[F, Span[F]] =
    Trace4CatsSpan(span.child(name, convert(options.spanKind)), toHeaders)

  override def spanId: F[Option[String]] = Applicative[F].pure(Some(span.context.spanId.show))

  override def traceId: F[Option[String]] = Applicative[F].pure(Some(span.context.traceId.show))

  override def traceUri: F[Option[URI]] = Applicative[F].pure(None)
}

object Trace4CatsSpan {
  def apply[F[_]: Sync](span: Resource[F, trace4cats.Span[F]], toHeaders: ToHeaders): Resource[F, Span[F]] =
    span.map(Trace4CatsSpan(_, toHeaders))
}
