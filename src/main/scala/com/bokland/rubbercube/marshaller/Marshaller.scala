package com.bokland.rubbercube.marshaller

/**
 * Created by remeniuk on 4/29/14.
 */
trait Marshaller[From, To] {

  def marshal(obj: From): To

}

trait Unmarshaller[From, To] {

  def unmarshal(obj: From): To

}
