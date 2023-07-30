package trace4cats.natchez

import cats.effect.kernel.{Resource, Sync}
import natchez.{EntryPoint, Kernel, Span}
import trace4cats.ToHeaders
import trace4cats.kernel.{SpanCompleter, SpanSampler}
import trace4cats.natchez.SpanKindConverter.convert

object Trace4CatsTracer {
  def entryPoint[F[_]: Sync](
    sampler: SpanSampler[F],
    completer: SpanCompleter[F],
    toHeaders: ToHeaders = ToHeaders.standard
  ): EntryPoint[F] =
    new EntryPoint[F] {
      override def root(name: String, options: Span.Options): Resource[F, Span[F]] =
        Trace4CatsSpan(trace4cats.Span.root(name, convert(options.spanKind), sampler, completer), toHeaders)

      override def continue(name: String, kernel: Kernel, options: Span.Options): Resource[F, Span[F]] =
        Trace4CatsSpan(
          toHeaders.toContext(KernelConverter.from(kernel)) match {
            case None => trace4cats.Span.root(name, convert(options.spanKind), sampler, completer)
            case Some(parent) =>
              trace4cats.Span.child(name, parent, convert(options.spanKind), sampler, completer)
          },
          toHeaders
        )

      override def continueOrElseRoot(name: String, kernel: Kernel, options: Span.Options): Resource[F, Span[F]] =
        continue(name, kernel, options)
    }
}
