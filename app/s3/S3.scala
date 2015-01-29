package s3

import com.amazonaws.auth.{BasicAWSCredentials, DefaultAWSCredentialsProviderChain}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model._
import java.io.ByteArrayInputStream
import scala.collection.JavaConverters._
import org.joda.time.DateTime
import play.api.libs.json.Json
import config._

class S3 {
  import play.api.Play.current
  lazy val config = RestorerConfig

  lazy val draftBucket: String = config.draftBucket
  lazy val liveBucket: String = config.liveBucket
  val accessKeys = Seq(config.accessKey ++ config.secretKey).flatten

  val s3Client = {
    if (accessKeys.length == 2)
      new AmazonS3Client(new BasicAWSCredentials(accessKeys(0), accessKeys(1)))
    else
      new AmazonS3Client(new DefaultAWSCredentialsProviderChain())
  }

  val getLiveSnapshot: String => S3Object = getObject(_, liveBucket)
  val getDraftSnapshot: String => S3Object = getObject(_, draftBucket)

  def getObject(key: String, bucketName: String): S3Object = {
    s3Client.getObject(new GetObjectRequest(bucketName, key))
  }

  val listLiveSnapshots = listSnapshots(liveBucket)
  val listDraftSnapshots = listSnapshots(draftBucket)

  private def listSnapshots(bucket: String, id: Option[String] = None): List[String] = {
    val request = new ListObjectsRequest().withBucketName(bucket)
    val requestWithId = id.map { i =>
      val key = idToKey(i)
      request.withPrefix(key)
    }.getOrElse(request)
    val objects = s3Client.listObjects(requestWithId)
    objects.getObjectSummaries.asScala.map(x => x.getKey).toList
  }

  val getObjects: ListObjectsRequest => ObjectListing = s3Client.listObjects(_)
  val objectRequest: String => ListObjectsRequest =
    new ListObjectsRequest().withBucketName(_)

  val listLiveForId: String => List[String] = id => listSnapshots(liveBucket, Some(id))
  val listDraftForId: String => List[String] = id => listSnapshots(draftBucket, Some(id))

  val deleteLive: String => Unit = deleteSnapshot(_, liveBucket)
  val deleteDraft: String => Unit = deleteSnapshot(_, draftBucket)

  private def deleteSnapshot(key: String, bucket: String) =
    s3Client.deleteObject(new DeleteObjectRequest(bucket, key))

  private def toBeRetired(bucket: String): List[String] = {
    val prevTime = new DateTime().minusDays(7)
    val shouldDelete: DateTime => Boolean = _.isBefore(prevTime)
    val timestamp: String => Option[String] = _.split("/").lift(7)

    listSnapshots(bucket).filter(x => {
      val d = DateTime.parse(timestamp(x).get)
      shouldDelete(d)
    })
  }

  // helpers to make the objects more manageable
  private val getId: String => Option[String] = _.split("/").lift(6)
  private val idToKey: String => String = s =>
    s.substring(0, 6).split("").mkString("/").substring(1) + "/" + s


  /*
   * Any shapshots more than seven days old should be deleted.
   * This is an expensive operation (potentially)
   */
  def retireSnapshots(): List[Unit] = {
    val retiredLives = toBeRetired(liveBucket)
    val retiredDrafts = toBeRetired(draftBucket)

    retiredLives.map(deleteLive)
    retiredDrafts.map(deleteDraft)
  }


  def saveItem(bucket: String, id: String, item: String): PutObjectResult = {

    if(!s3Client.doesBucketExist(bucket)) {
      s3Client.createBucket(bucket, Region.EU_Ireland)
    }

    val contentLength = item.getBytes().length
    val metaData = new ObjectMetadata()
    metaData.setContentType("application/json; charset=utf-8")
    metaData.setContentLength(contentLength)
    s3Client.putObject(bucket, id, new ByteArrayInputStream(item.getBytes()), metaData)
  }
}
