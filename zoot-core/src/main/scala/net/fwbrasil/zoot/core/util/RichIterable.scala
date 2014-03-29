package net.fwbrasil.zoot.core.util

object RichIterable {

    implicit class RichIterable[T](iterable: Iterable[T]) {
        def findDefined[R](f: T => Option[R]): Option[R] =
            iterable.foldLeft(None: Option[R]) {
                (base, item) =>
                    base orElse f(item)
            }

        def zipWith[R](f: T => R) =
            iterable.map(e => (e, f(e)))

        def onlyOne =
            if (iterable.size == 1)
                iterable.head
            else
                throw new IllegalStateException(s"Expected 1 element, got ${iterable.size}.")

        def groupByUnique[K](f: T => K) =
            iterable.groupBy(f(_)).mapValues(_.onlyOne)

        def ifNonEmpty(f: Iterable[T] => Unit) = {
            if (iterable.nonEmpty)
                f(iterable)
            iterable
        }
    }
}