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
import org.joda.time.DateTime

class S3 {
  import play.api.Play.current
  lazy val config = play.api.Play.configuration

  lazy val draftBucket: String = config.getString("s3.draftbucket").get
  lazy val liveBucket: String = config.getString("s3.livebucket").get
  lazy val accessKey: String = config.getString("AWS_ACCESS_KEY").get
  lazy val secretKey: String = config.getString("AWS_SECRET_KEY").get

  lazy val credentials = new BasicAWSCredentials(accessKey, secretKey)
  lazy val s3Client = new AmazonS3Client(credentials)


  val getLiveSnapshot: String => S3Object = getSnapshot(_, liveBucket)
  val getDraftSnapshot: String => S3Object = getSnapshot(_, draftBucket)

  private def getSnapshot(key: String, bucketName: String): S3Object = {
    s3Client.getObject(new GetObjectRequest(bucketName, key))
  }


  val listLiveSnapshots = listSnapshots(liveBucket)
  val listDraftSnapshots = listSnapshots(draftBucket)

  private def listSnapshots(bucket: String): List[String] = {
    val objects = s3Client.listObjects(bucket)
    objects.getObjectSummaries().asScala.map(x => x.getKey()).toList
  }

  val listLiveForId: String => List[String] = listSnapshotsById(_, liveBucket)
  val listDraftForId: String => List[String] = listSnapshotsById(_, draftBucket)

  private def listSnapshotsById(id: String, bucket: String): List[String] = {
    val key = idToKey(id)
    val checkId: (String, String) =>  Boolean = getId(_) == _
    listSnapshots(bucket).filter(x => checkId(x, id))
  }



  val deleteLive: String => Unit = deleteSnapshot(_, liveBucket)
  val deleteDraft: String => Unit = deleteSnapshot(_, draftBucket)

  private def deleteSnapshot(key: String, bucket: String) =
    s3Client.deleteObject(new DeleteObjectRequest(bucket, key))

  private def toBeRetired(bucket: String): List[String] = {
    val prevTime = new DateTime().minusDays(7)
    val shouldDelete: DateTime => Boolean = _.isBefore(prevTime)
    val timestamp: String => Option[String] = _.split("_").lift(1)

    listSnapshots(bucket).filter(x => {
      val d = DateTime.parse(timestamp(x).get)
      shouldDelete(d)
    })
  }

  // helpers to make the objects more manageable
  private val getId: String => String = _.split("_").head.substring(12)
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
}
