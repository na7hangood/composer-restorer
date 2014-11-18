package s3

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectResult
import com.amazonaws.services.s3.model.GetObjectRequest
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





  private def getSnapshot(id: String, timestamp: String, bucketName: String): S3Object = {
    val credentials = new BasicAWSCredentials(accessKey, secretKey)
    val s3Client = new AmazonS3Client(credentials)
    val key = id + "." + timestamp + ".json"
    val objectReq = new GetObjectRequest(bucketName, key)
    s3Client.getObject(objectReq)
  }

  def getLiveSnapshot(id: String, timestamp: String): S3Object =
    getSnapshot(id, timestamp, draftBucket)
  def getDraftSnapshot(id: String, timestamp: String): S3Object =
    getSnapshot(id, timestamp, liveBucket)

}
