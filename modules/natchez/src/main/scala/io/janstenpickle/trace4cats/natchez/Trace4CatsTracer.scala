package io.janstenpickle.trace4cats.natchez

import cats.effect.kernel.{Resource, Sync}
import io.janstenpickle.trace4cats.ToHeaders
import io.janstenpickle.trace4cats.kernel.{SpanCompleter, SpanSampler}
import io.janstenpickle.trace4cats.model.SpanKind
import natchez.{EntryPoint, Kernel, Span}

object Trace4CatsTracer {
  def entryPoint[F[_]: Sync](
    sampler: SpanSampler[F],
    completer: SpanCompleter[F],
    toHeaders: ToHeaders = ToHeaders.standard
  ): EntryPoint[F] =
    new EntryPoint[F] {
      override def root(name: String): Resource[F, Span[F]] =
        Trace4CatsSpan(io.janstenpickle.trace4cats.Span.root(name, SpanKind.Internal, sampler, completer), toHeaders)

      override def continue(name: String, kernel: Kernel): Resource[F, Span[F]] =
        Trace4CatsSpan(
          toHeaders.toContext(KernelConverter.from(kernel)) match {
            case None => io.janstenpickle.trace4cats.Span.root(name, SpanKind.Server, sampler, completer)
            case Some(parent) =>
              io.janstenpickle.trace4cats.Span.child(name, parent, SpanKind.Server, sampler, completer)
          },
          toHeaders
        )

      override def continueOrElseRoot(name: String, kernel: Kernel): Resource[F, Span[F]] = continue(name, kernel)
    }
}
