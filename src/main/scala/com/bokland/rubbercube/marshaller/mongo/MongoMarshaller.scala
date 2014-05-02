package com.bokland.rubbercube.marshaller.mongo

import com.bokland.rubbercube.marshaller.{Unmarshaller, Marshaller}
import com.mongodb.util.JSON
import com.mongodb.casbah.query.Imports._

/**
 * Created by remeniuk on 5/2/14.
 */
trait MongoMarshaller[T] extends Marshaller[T, DBObject] with Unmarshaller[DBObject, T] {

  def marshal(obj: T): DBObject

  def unmarshal(obj: DBObject): T

  def toJson(obj: T) = marshal(obj).toString

  def fromJson(json: String) = unmarshal(JSON.parse(json).asInstanceOf[DBObject])

}
