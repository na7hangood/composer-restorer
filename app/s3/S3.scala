package s3

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectResult
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.model.Region
import java.io.ByteArrayInputStream
import scala.collection.JavaConverters._

object S3 {
  import play.api.Play.current
  lazy val config = play.api.Play.configuration

  lazy val draftBucket: String = config.getString("s3.draftbucket").get
  lazy val liveBucket: String = config.getString("s3.livebucket").get
  lazy val accessKey: String = config.getString("AWS_ACCESS_KEY").get
  lazy val secretKey: String = config.getString("AWS_SECRET_KEY").get

  lazy val credentials = new BasicAWSCredentials(accessKey, secretKey)
  lazy val s3Client = new AmazonS3Client(credentials)


  val getLiveSnapshot: (String, String) => S3Object = getSnapshot(_, _, draftBucket)
  val getDraftSnapshot: (String, String) => S3Object = getSnapshot(_, _, liveBucket)

  private def getSnapshot(id: String, timestamp: String, bucketName: String): S3Object = {
    val key = id + "." + timestamp + ".json"
    s3Client.getObject(new GetObjectRequest(bucketName, key))
  }

  val listLiveSnapshots = listSnapshots(liveBucket)
  val listDraftSnapshots = listSnapshots(draftBucket)

  def listSnapshots(bucket: String): List[String] = {
    val objects = s3Client.listObjects(bucket)
    objects.getObjectSummaries().asScala.map(x => x.getKey()).toList
  }

  val listLiveForId: (String) => List[String] = listSnapshotsById(_, liveBucket)
  val listDraftForId: (String) => List[String] = listSnapshotsById(_, draftBucket)

  private def listSnapshotsById(id: String, bucket: String): List[String] =
    listSnapshots(bucket).filter(x => checkId(x, id))

  def fetchTimeStamps(id: String, bucket: String): List[Option[String]] =
    listSnapshotsById(id, bucket).map(x => x.split("\\.").lift(2))

  private def checkId(key: String, id: String): Boolean = key.split("\\.").head == id

  val deleteLive: (String, String) => Unit = deleteSnapshot(_, _, liveBucket)
  val deleteDraft: (String, String) => Unit = deleteSnapshot(_, _, draftBucket)

  private def deleteSnapshot(id: String, timestamp: String, bucket: String) = {
    val key = id + "." + timestamp + ".json"
    s3Client.deleteObject(new DeleteObjectRequest(bucket, key))
  }

}
