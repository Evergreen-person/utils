package io.kinoplan.utils.zio.reactivemongo

import reactivemongo.api.{DB, FailoverStrategy}
import reactivemongo.api.bson._
import reactivemongo.api.bson.collection.BSONCollection
import zio.{Task, ZIO}

import io.kinoplan.utils.reactivemongo.base.Queries
import io.kinoplan.utils.zio.reactivemongo.metrics.ReactiveMongoMetric._

class ReactiveMongoOperations[T](coll: Task[BSONCollection]) {
  def collection: Task[BSONCollection] = coll

  def insertMany(values: List[T])(implicit
    w: BSONDocumentWriter[T]
  ) = for {
    coll <- collection
    result <- ZIO.fromFuture(implicit ec => Queries.insertManyQ(coll)(values))
    _ <- insertCounter.tagged(collectionLabels(coll)).increment
  } yield result

  def insertOne(value: T)(implicit
    w: BSONDocumentWriter[T]
  ) = for {
    coll <- collection
    result <- ZIO.fromFuture(implicit ec => Queries.insertOneQ(coll)(value))
    _ <- insertCounter.tagged(collectionLabels(coll)).increment
  } yield result

  def update(
    q: BSONDocument,
    u: BSONDocument,
    multi: Boolean = false,
    upsert: Boolean = false,
    arrayFilters: Seq[BSONDocument] = Seq.empty[BSONDocument]
  ) = for {
    coll <- collection
    result <- ZIO.fromFuture(implicit ec => Queries.updateQ(coll)(q, u, multi, upsert, arrayFilters))
    _ <- updateCounter.tagged(collectionLabels(coll)).increment
  } yield result

  def updateMany(values: List[T], f: T => (BSONDocument, BSONDocument, Boolean, Boolean)) = for {
    coll <- collection
    result <- ZIO.fromFuture(implicit ec => Queries.updateManyQ(coll)(values, f))
    _ <- updateCounter.tagged(collectionLabels(coll)).increment
  } yield result

  def upsert(q: BSONDocument, value: T)(implicit
    w: BSONDocumentWriter[T]
  ) = for {
    coll <- collection
    result <- ZIO.fromFuture(implicit ec => Queries.upsertQ(coll)(q, value))
    _ <- updateCounter.tagged(collectionLabels(coll)).increment
  } yield result

  def saveOne(q: BSONDocument, value: T, multi: Boolean = false, upsert: Boolean = true)(implicit
    w: BSONDocumentWriter[T]
  ) = for {
    coll <- collection
    result <- ZIO.fromFuture(implicit ec => Queries.saveQ(coll)(q, value, multi, upsert))
    _ <- updateCounter.tagged(collectionLabels(coll)).increment
  } yield result

  def saveMany(values: List[T], f: T => (BSONDocument, T, Boolean, Boolean))(implicit
    w: BSONDocumentWriter[T]
  ) = for {
    coll <- collection
    result <- ZIO.fromFuture(implicit ec => Queries.saveManyQ(coll)(values, f))
    _ <- updateCounter.tagged(collectionLabels(coll)).increment
  } yield result

  def delete(q: BSONDocument) = for {
    coll <- collection
    result <- ZIO.fromFuture(implicit ec => Queries.deleteQ(coll)(q))
    _ <- removeCounter.tagged(collectionLabels(coll)).increment
  } yield result

  def deleteByIds(ids: Set[BSONObjectID]) = for {
    coll <- collection
    result <- ZIO.fromFuture(implicit ec => Queries.deleteByIdsQ(coll)(ids))
    _ <- removeCounter.tagged(collectionLabels(coll)).increment
  } yield result

  def deleteById(id: BSONObjectID) = for {
    coll <- collection
    result <- ZIO.fromFuture(implicit ec => Queries.deleteByIdQ(coll)(id))
    _ <- removeCounter.tagged(collectionLabels(coll)).increment
  } yield result

}

object ReactiveMongoOperations {
  def apply[T](collection: Task[BSONCollection]) = new ReactiveMongoOperations[T](collection)

  def apply[T](
    db: Task[DB],
    collectionName: String,
    failoverStrategyO: Option[FailoverStrategy] = None
  ) = new ReactiveMongoOperations[T](
    db.map(db => failoverStrategyO.fold(db.collection(collectionName))(db.collection(collectionName, _)))
  )

}
