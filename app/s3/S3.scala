package s3

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model._
import java.io.ByteArrayInputStream
import scala.collection.JavaConverters._
import org.joda.time.DateTime
import play.api.libs.json.Json

class S3 {
  import play.api.Play.current
  lazy val config = play.api.Play.configuration

  lazy val draftBucket: String = config.getString("s3.draftbucket").get
  lazy val liveBucket: String = config.getString("s3.livebucket").get
  lazy val accessKey: String = config.getString("AWS_ACCESS_KEY").get
  lazy val secretKey: String = config.getString("AWS_SECRET_KEY").get

  lazy val credentials = new BasicAWSCredentials(accessKey, secretKey)
  lazy val s3Client = new AmazonS3Client(credentials)


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

  // helpers to make the objects more manageable
  private val getId: String => Option[String] = _.split("/").lift(6)
  private val idToKey: String => String = s =>
    s.substring(0, 6).split("").mkString("/").substring(1) + "/" + s


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
